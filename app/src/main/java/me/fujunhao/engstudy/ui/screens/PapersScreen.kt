package me.fujunhao.engstudy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassChip

private data class Paper(val year: Int, val kind: String, val status: String)

private val Papers = listOf(
    Paper(2026, "英语一", "未开始"),
    Paper(2025, "英语一", "阅读 34/40"),
    Paper(2024, "英语一", "已二刷"),
    Paper(2023, "英语一", "已一刷"),
    Paper(2022, "英语一", "已一刷"),
    Paper(2021, "英语一", "已一刷"),
)

@Composable
fun PapersScreen(backdrop: Backdrop) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 52.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column {
                Text("考研真题", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "完形 / 阅读交互答题 · 翻译接入训练 · 写作浏览",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        items(Papers.size) { i -> PaperCard(backdrop, Papers[i]) }
    }
}

@Composable
private fun PaperCard(backdrop: Backdrop, paper: Paper) {
    GlassCard(backdrop) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${paper.year} ${paper.kind}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                paper.status,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
