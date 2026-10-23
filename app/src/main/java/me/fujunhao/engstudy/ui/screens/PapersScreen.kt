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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.core.model.AppState
import me.fujunhao.engstudy.data.content.PaperIndexDto
import me.fujunhao.engstudy.feature.papers.PapersViewModel
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassChip
import me.fujunhao.engstudy.ui.glass.GlassPillButton

@Composable
fun PapersScreen(
    backdrop: Backdrop,
    onOpenPaper: (String) -> Unit = {},
    viewModel: PapersViewModel = hiltViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    val practiced = ui.paperIndex.count { set -> paperDone(ui.progress, set.id) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column {
                Text("考研真题", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "${ui.paperIndex.size} 套 · 已练 $practiced 套 · 完形 / 阅读 / 翻译 / 写作",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        when {
            ui.loadingIndex -> item {
                GlassCard(backdrop) { Text("正在加载真题…", style = MaterialTheme.typography.bodyLarge) }
            }
            ui.indexError.isNotBlank() -> item {
                GlassCard(backdrop) {
                    Text(ui.indexError, style = MaterialTheme.typography.bodyLarge)
                    GlassPillButton(
                        "重试",
                        viewModel::loadIndex,
                        backdrop,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }
            }
            else -> items(ui.paperIndex.size) { index ->
                PaperCard(backdrop, ui.paperIndex[index], ui.progress) { onOpenPaper(ui.paperIndex[index].id) }
            }
        }
    }
}

private fun paperDone(progress: AppState, setId: String): Boolean =
    progress.papers.filterKeys { it.startsWith("$setId#") }.any { (_, record) ->
        record.total > 0 || record.rating.isNotBlank()
    }

private fun paperMeta(set: PaperIndexDto): String = listOfNotNull(
    if (set.questions > 0) "${set.questions} 题" else null,
    set.counts["cloze"]?.let { "完形 $it" },
    set.counts["reading"]?.let { "阅读 $it" },
).joinToString(" · ")

@Composable
private fun PaperCard(
    backdrop: Backdrop,
    set: PaperIndexDto,
    progress: AppState,
    onClick: () -> Unit,
) {
    val records = progress.papers.filterKeys { it.startsWith("${set.id}#") }.values
    val total = records.sumOf { it.total }
    val correct = records.sumOf { it.correct }
    val status = when {
        total > 0 -> "已批改 $correct / $total"
        records.any { it.rating.isNotBlank() } -> "已理解"
        else -> "未开始"
    }
    GlassCard(backdrop, modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${set.year} ${set.exam}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                status,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (paperMeta(set).isNotBlank()) {
            Text(
                paperMeta(set),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Row(
            modifier = Modifier.padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GlassChip("完形", backdrop)
            GlassChip("阅读", backdrop)
            GlassChip("翻译", backdrop)
            GlassChip("写作", backdrop)
        }
    }
}
