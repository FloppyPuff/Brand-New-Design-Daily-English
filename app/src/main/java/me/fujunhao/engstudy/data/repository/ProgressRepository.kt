package me.fujunhao.engstudy.data.repository

import me.fujunhao.engstudy.core.learning.LearningEngine
import me.fujunhao.engstudy.core.learning.PaperEngine
import me.fujunhao.engstudy.core.model.AppState
import me.fujunhao.engstudy.core.model.StudyEvent
import me.fujunhao.engstudy.core.model.createState
import me.fujunhao.engstudy.core.sync.StateMapper
import me.fujunhao.engstudy.core.sync.normalizeLegacy
import me.fujunhao.engstudy.data.content.PaperQuestionDto
import me.fujunhao.engstudy.data.local.DraftEntity
import me.fujunhao.engstudy.data.local.ProgressDao
import me.fujunhao.engstudy.data.local.SavedSentenceEntity
import me.fujunhao.engstudy.data.local.SavedWordEntity
import me.fujunhao.engstudy.data.local.toEntity
import me.fujunhao.engstudy.di.ApplicationScope
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class ProgressRepository @Inject constructor(
  private val dao: ProgressDao,
  private val mapper: StateMapper,
  private val syncTrigger: SyncTrigger,
  @ApplicationScope private val scope: CoroutineScope,
) {
  private val mutex = Mutex()
  private val _state = MutableStateFlow(createState())
  private val _loaded = MutableStateFlow(false)
  val state: StateFlow<AppState> = _state.asStateFlow()
  val loaded: StateFlow<Boolean> = _loaded.asStateFlow()

  init {
    scope.launch {
      syncTrigger.remoteState.filterNotNull().collect { remote ->
        mutex.withLock {
          _state.value = remote
          _loaded.value = true
        }
      }
    }
  }

  suspend fun load() {
    mutex.withLock {
      if (_loaded.value) return
      _state.value = mapper.snapshot()
      _loaded.value = true
    }
  }

  suspend fun rateWord(id: String, quality: String) = mutate { current ->
    val next = LearningEngine.rateWord(current, id, quality)
    dao.upsertWord(next.records.getValue(id).toEntity(id))
    dao.insertEvent(next.events.last().toEntity())
    next
  }

  suspend fun recordSentence(id: String, answer: String, rating: String) = mutate { current ->
    val next = LearningEngine.recordSentence(current, id, answer, rating)
    dao.upsertSentence(next.sentenceRecords.getValue(id).toEntity(id))
    dao.upsertDraft(DraftEntity(id, next.drafts.getValue(id)))
    dao.insertEvent(next.events.last().toEntity())
    next
  }

  suspend fun recordPaper(
    key: String,
    answers: Map<String, String>,
    questions: List<PaperQuestionDto>,
    words: List<String>,
    rating: String = "",
  ) = mutate { current ->
    val next = PaperEngine.recordPaper(current, key, answers, questions, words, rating)
    mapper.savePaper(key, next.papers.getValue(key))
    dao.insertEvent(next.events.last().toEntity())
    next
  }

  suspend fun setDraft(id: String, text: String) = mutate { current ->
    val next = current.copy(drafts = current.drafts + (id to text))
    dao.upsertDraft(DraftEntity(id, text))
    next
  }

  suspend fun toggleSavedWord(id: String) = mutate { current ->
    val saved = current.savedWords.contains(id)
    val next = current.copy(savedWords = if (saved) current.savedWords - id else current.savedWords + id)
    if (saved) dao.deleteSavedWord(id) else dao.insertSavedWord(SavedWordEntity(id))
    next
  }

  suspend fun toggleSavedSentence(id: String) = mutate { current ->
    val saved = current.savedSentences.contains(id)
    val next = current.copy(savedSentences = if (saved) current.savedSentences - id else current.savedSentences + id)
    if (saved) dao.deleteSavedSentence(id) else dao.insertSavedSentence(SavedSentenceEntity(id))
    next
  }

  suspend fun updateSettings(dailyGoal: Int, sentenceGoal: Int, focusMinutes: Int, exam: String) = mutate { current ->
    val next = current.copy(
      settings = current.settings.copy(
        dailyGoal = dailyGoal.coerceIn(1, 100),
        sentenceGoal = sentenceGoal.coerceIn(1, 20),
        focusMinutes = focusMinutes.coerceIn(1, 120),
        exam = if (exam == "postgrad") "postgrad" else "cet6",
      ),
    )
    dao.upsertSettings(next.settings.toEntity())
    next
  }

  suspend fun addFocus(seconds: Int) = mutate { current ->
    if (seconds <= 0) return@mutate current
    val event = StudyEvent(type = "focus", seconds = seconds, at = System.currentTimeMillis(), exam = current.settings.exam)
    dao.insertEvent(event.toEntity())
    current.copy(events = current.events + event)
  }

  suspend fun replaceFromRemote(remote: AppState) {
    mutex.withLock {
      val normalized = normalizeLegacy(remote)
      mapper.replaceAll(normalized)
      _state.value = normalized
      _loaded.value = true
    }
  }

  suspend fun importState(imported: AppState) {
    mutex.withLock {
      val normalized = normalizeLegacy(imported)
      mapper.replaceAll(normalized)
      _state.value = normalized
      _loaded.value = true
      syncTrigger.markDirty()
    }
  }

  private suspend fun mutate(block: suspend (AppState) -> AppState) {
    mutex.withLock {
      val next = block(_state.value)
      _state.value = next
      syncTrigger.markDirty()
    }
  }
}
