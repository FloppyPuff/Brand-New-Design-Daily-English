package me.fujunhao.engstudy.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.core.learning.Statistics
import me.fujunhao.engstudy.core.learning.WeekSlice
import me.fujunhao.engstudy.feature.stats.StatsViewModel
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton

@Composable
fun StatsScreen(
    backdrop: Backdrop,
    onBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    val stats = ui.stats

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassPillButton("返回", onBack, backdrop)
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text("学习记录", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "CET-6 与考研进度合并统计",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
        when {
            ui.loading -> item { GlassCard(backdrop) { Text("正在统计…") } }
            ui.error.isNotBlank() -> item {
                GlassCard(backdrop) {
                    Text(ui.error)
                    GlassPillButton(
                        "重试",
                        viewModel::retry,
                        backdrop,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }
            }
            stats != null -> {
                item { StatGrid(backdrop, stats) }
                item { WeekPanel(backdrop, stats.week) }
                item { MasteryPanel(backdrop, stats) }
            }
        }
    }
}

@Composable
private fun StatGrid(backdrop: Backdrop, stats: Statistics) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(backdrop, "累计已学", stats.learned, "词", "当前词库 ${stats.total} 词", Modifier.weight(1f))
            StatCard(backdrop, "较熟悉词汇", stats.mastered, "词", "连续记住 3 次以上", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(backdrop, "学习天数", stats.activeDays, "天", "连续 ${stats.streak} 天", Modifier.weight(1f))
            StatCard(backdrop, "累计专注", stats.totalMinutes, "分钟", "今天 ${stats.todayMinutes} 分钟", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                backdrop,
                "真题练习",
                stats.paperCorrect,
                "题",
                "共答 ${stats.paperTotal} 题 · 今天 ${stats.todayPapers} 组",
                Modifier.weight(1f),
            )
            StatCard(backdrop, "待复习", stats.due, "词", "待加固 ${stats.difficult} 词", Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(
    backdrop: Backdrop,
    label: String,
    value: Int,
    unit: String,
    hint: String,
    modifier: Modifier = Modifier,
) {
    GlassCard(backdrop, modifier = modifier, contentPadding = PaddingValues(16.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 6.dp)) {
            Text(
                value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                unit,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
            )
        }
        Text(
            hint,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun WeekPanel(backdrop: Backdrop, week: List<WeekSlice>) {
    val max = week.maxOfOrNull { it.words + it.sentences }?.coerceAtLeast(1) ?: 1
    GlassCard(backdrop, modifier = Modifier.fillMaxWidth()) {
        Text("最近七天的节奏", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            week.forEach { slice ->
                val value = slice.words + slice.sentences
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .height(96.dp)
                            .width(22.dp),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height((96f * value / max).coerceAtLeast(if (value > 0) 6f else 3f).dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (value > 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                ),
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(slice.label, style = MaterialTheme.typography.labelSmall)
                    Text(
                        value.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun MasteryPanel(backdrop: Backdrop, stats: Statistics) {
    val total = stats.total.coerceAtLeast(1)
    val learnedFraction = (stats.learned.toFloat() / total).coerceIn(0f, 1f)
    val masteredFraction = (stats.mastered.toFloat() / total).coerceIn(0f, 1f)
    GlassCard(backdrop, modifier = Modifier.fillMaxWidth()) {
        Text("词汇掌握情况", style = MaterialTheme.typography.titleSmall)
        Text(
            "已学 ${stats.learned} / ${stats.total} 词",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp),
        )
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(learnedFraction)
                    .height(10.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
            )
            Box(
                Modifier
                    .fillMaxWidth(masteredFraction)
                    .height(10.dp)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
        Text(
            "较熟悉 ${stats.mastered} 词 · 待复习 ${stats.due} 词",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
