package me.fujunhao.engstudy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.core.learning.PaperEngine
import me.fujunhao.engstudy.core.learning.PaperGroup
import me.fujunhao.engstudy.core.model.PaperRecord
import me.fujunhao.engstudy.data.content.PaperQuestionDto
import me.fujunhao.engstudy.feature.papers.PapersViewModel
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton

@Composable
fun PaperPracticeScreen(
    backdrop: Backdrop,
    setId: String,
    onBack: () -> Unit,
    viewModel: PapersViewModel = hiltViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    var groupIndex by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(setId) { viewModel.openPaper(setId) }

    val paper = ui.paper
    val groups = remember(paper) { paper?.let { PaperEngine.paperGroups(it) } ?: emptyList() }
    val activeGroup = groups.firstOrNull { it.index == groupIndex }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassPillButton(
                    if (activeGroup == null) "返回" else "组列表",
                    { if (activeGroup == null) onBack() else groupIndex = null },
                    backdrop,
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        paper?.let { "${it.year} ${it.exam}" } ?: "真题",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        if (activeGroup == null) "${groups.size} 组练习" else activeGroup.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
        if (ui.loadingPaper) {
            item { GlassCard(backdrop) { Text("正在加载试卷…") } }
        } else if (ui.paperError.isNotBlank()) {
            item {
                GlassCard(backdrop) {
                    Text(ui.paperError)
                    GlassPillButton(
                        "重试",
                        { viewModel.openPaper(setId) },
                        backdrop,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }
            }
        } else if (activeGroup == null) {
            items(groups.size) { index ->
                GroupCard(
                    backdrop = backdrop,
                    group = groups[index],
                    record = ui.progress.papers[groups[index].key],
                    onClick = {
                        groupIndex = groups[index].index
                        viewModel.openGroup(setId, groups[index].index)
                    },
                )
            }
        } else {
            item { PassageCard(backdrop, activeGroup) }
            items(activeGroup.questions.size) { index ->
                val question = activeGroup.questions[index]
                QuestionCard(
                    backdrop = backdrop,
                    question = question,
                    answer = ui.answers[question.number.toString()].orEmpty(),
                    checked = ui.checked,
                    translation = activeGroup.translation,
                    onPick = { choice -> viewModel.pick(question.number, choice) },
                    onText = { text -> viewModel.updateAnswer(question.number, text) },
                )
            }
            item {
                if (activeGroup.translation) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassPillButton(
                            if (ui.checked) "收起参考译文" else "对照参考译文",
                            viewModel::toggleReference,
                            backdrop,
                        )
                        GlassPillButton("加入复习", { viewModel.rate("review") }, backdrop)
                        GlassPillButton("已掌握", { viewModel.rate("mastered") }, backdrop, prominent = true)
                    }
                } else {
                    Column {
                        if (ui.checked) {
                            val score = PaperEngine.gradePaper(ui.answers, activeGroup.questions)
                            Text(
                                "答对 ${score.correct} / ${score.total} 题",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 10.dp),
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            GlassPillButton(
                                if (ui.checked) "重新作答" else "提交批改",
                                { if (ui.checked) viewModel.reset() else viewModel.submit() },
                                backdrop,
                                prominent = !ui.checked,
                            )
                            if (ui.checked) {
                                GlassPillButton("加入复习", { viewModel.rate("review") }, backdrop)
                                GlassPillButton("已掌握", { viewModel.rate("mastered") }, backdrop)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupCard(backdrop: Backdrop, group: PaperGroup, record: PaperRecord?, onClick: () -> Unit) {
    val status = when {
        record == null -> "未开始"
        record.total > 0 -> "已批改 ${record.correct} / ${record.total}"
        record.rating.isNotBlank() -> "已理解"
        else -> "已作答"
    }
    GlassCard(backdrop, modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(group.label, style = MaterialTheme.typography.titleMedium)
                Text(
                    group.section.ifBlank { group.instructions }.take(40),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Text(
                status,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PassageCard(backdrop: Backdrop, group: PaperGroup) {
    if (group.data.passage.isEmpty()) return
    var expanded by remember(group.index) { mutableStateOf(false) }
    GlassCard(backdrop) {
        Text(
            group.data.passage.joinToString("\n\n"),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (expanded) Int.MAX_VALUE else 8,
            overflow = TextOverflow.Ellipsis,
        )
        GlassPillButton(
            if (expanded) "收起原文" else "展开全文",
            { expanded = !expanded },
            backdrop,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

@Composable
private fun QuestionCard(
    backdrop: Backdrop,
    question: PaperQuestionDto,
    answer: String,
    checked: Boolean,
    translation: Boolean,
    onPick: (String) -> Unit,
    onText: (String) -> Unit,
) {
    GlassCard(backdrop) {
        Text(
            question.stem?.takeIf { it.isNotBlank() } ?: "第 ${question.number} 题",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        if (translation) {
            OutlinedTextField(
                value = answer,
                onValueChange = onText,
                placeholder = { Text("写下你的翻译…") },
                minLines = 3,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
            if (checked && !question.answer.isNullOrBlank()) {
                Text(
                    "参考译文：${question.answer}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        } else {
            question.options.forEach { (key, text) ->
                val selected = answer == key
                val correct = checked && question.answer == key
                val wrong = checked && selected && question.answer != key
                val color = when {
                    correct -> MaterialTheme.colorScheme.primary
                    wrong -> MaterialTheme.colorScheme.error
                    selected -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clickable { onPick(key) }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("$key.", style = MaterialTheme.typography.titleSmall, color = color)
                    Spacer(Modifier.width(8.dp))
                    Text(text, style = MaterialTheme.typography.bodyMedium, color = color)
                }
            }
        }
    }
}
