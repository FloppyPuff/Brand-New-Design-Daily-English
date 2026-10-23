package me.fujunhao.engstudy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.data.content.WordDto
import me.fujunhao.engstudy.feature.study.StudyViewModel
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton

@Composable
fun WordsScreen(backdrop: Backdrop, viewModel: StudyViewModel = hiltViewModel()) {
    val ui by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(ui.content != null) {
        if (ui.content != null && ui.wordQueue.isEmpty()) viewModel.startSession("new")
    }

    val word = ui.content?.wordById?.get(ui.wordQueue.getOrNull(ui.wordIndex))
    val upcoming = ui.wordQueue.drop(ui.wordIndex + 1).take(6)
        .mapNotNull { id -> ui.content?.wordById?.get(id) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column {
                Text("今日复习", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "到期 ${ui.stats?.due ?: 0} 词 · 新词 ${ui.plan?.newWords?.size ?: 0} · 待加固 ${ui.stats?.difficult ?: 0}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        when {
            ui.loading -> item {
                GlassCard(backdrop) {
                    Text("正在加载词库…", style = MaterialTheme.typography.bodyLarge)
                }
            }
            ui.error.isNotBlank() -> item {
                GlassCard(backdrop) {
                    Text(ui.error, style = MaterialTheme.typography.bodyLarge)
                    GlassPillButton(
                        "重试",
                        viewModel::reload,
                        backdrop,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }
            }
            word != null -> {
                item {
                    ReviewCard(
                        backdrop = backdrop,
                        word = word,
                        revealed = ui.revealed,
                        position = ui.wordIndex + 1,
                        total = ui.wordQueue.size,
                        onReveal = viewModel::revealWord,
                        onRate = viewModel::rateWord,
                        saved = ui.progress.savedWords.contains(word.id),
                        onToggleSave = { viewModel.toggleSavedWord(word.id) },
                    )
                }
                items(upcoming.size) { index ->
                    MiniWordCard(backdrop, upcoming[index])
                }
            }
            else -> item {
                GlassCard(backdrop) {
                    Text(
                        "这一组完成了",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "可以换一组新词，或先复习到期的单词。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        GlassPillButton("换一组", { viewModel.startSession("new") }, backdrop)
                        GlassPillButton(
                            "复习到期",
                            { viewModel.startSession("review") },
                            backdrop,
                            prominent = true,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(
    backdrop: Backdrop,
    word: WordDto,
    revealed: Boolean,
    position: Int,
    total: Int,
    onReveal: () -> Unit,
    onRate: (String) -> Unit,
    saved: Boolean,
    onToggleSave: () -> Unit,
) {
    GlassCard(backdrop) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                "第 $position / $total 词",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    word.pos.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    if (saved) "已收藏" else "收藏",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (saved) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable(onClick = onToggleSave),
                )
            }
        }
        Text(
            word.id,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            word.phonetic.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp),
        )
        Text(
            if (revealed) word.meaning else "点击卡片查看释义",
            style = if (revealed) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.labelMedium,
            color = if (revealed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 14.dp)
                .clickable(onClick = onReveal)
                .fillMaxWidth(),
        )
        Row(
            modifier = Modifier.padding(top = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GlassPillButton("忘记", { onRate("again") }, backdrop)
            GlassPillButton("模糊", { onRate("hard") }, backdrop)
            GlassPillButton("认识", { onRate("good") }, backdrop, prominent = true)
        }
    }
}

@Composable
private fun MiniWordCard(backdrop: Backdrop, word: WordDto) {
    GlassCard(backdrop, contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(word.id, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(
                word.phonetic.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
