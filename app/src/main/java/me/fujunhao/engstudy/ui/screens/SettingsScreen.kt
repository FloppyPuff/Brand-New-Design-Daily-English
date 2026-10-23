package me.fujunhao.engstudy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.data.repository.AuthState
import me.fujunhao.engstudy.data.repository.SyncStatus
import me.fujunhao.engstudy.feature.settings.SettingsViewModel
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton

@Composable
fun SettingsScreen(
    backdrop: Backdrop,
    onBack: () -> Unit,
    onLogin: () -> Unit,
    versionName: String = "0.2.0",
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    var dailyGoal by remember(ui.settings.dailyGoal) { mutableIntStateOf(ui.settings.dailyGoal) }
    var sentenceGoal by remember(ui.settings.sentenceGoal) { mutableIntStateOf(ui.settings.sentenceGoal) }
    var focusMinutes by remember(ui.settings.focusMinutes) { mutableIntStateOf(ui.settings.focusMinutes) }
    val signedIn = ui.auth is AuthState.Member

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassPillButton("返回", onBack, backdrop)
        }
        Column {
            Text("设置", style = MaterialTheme.typography.headlineSmall)
            Text(
                "学习计划、账号与同步",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        GlassCard(backdrop) {
            Text("账号", style = MaterialTheme.typography.titleSmall)
            Text(
                (ui.sync.email.ifBlank { null }) ?: "未登录",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp),
            )
            Row(modifier = Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (signedIn) {
                    GlassPillButton("退出登录", viewModel::logout, backdrop)
                } else {
                    GlassPillButton("去登录", onLogin, backdrop, prominent = true)
                }
                GlassPillButton(
                    text = if (ui.sync.status == SyncStatus.SAVING) "同步中…" else "立即同步",
                    onClick = viewModel::syncNow,
                    backdrop = backdrop,
                )
            }
        }
        GlassCard(backdrop) {
            Text("学习计划", style = MaterialTheme.typography.titleSmall)
            StepperRow(backdrop, "每日新词", dailyGoal, "词", 5, 100, 5) { dailyGoal = it }
            StepperRow(backdrop, "每日句子", sentenceGoal, "句", 1, 10, 1) { sentenceGoal = it }
            StepperRow(backdrop, "专注时长", focusMinutes, "分钟", 10, 60, 5) { focusMinutes = it }
            GlassPillButton(
                text = if (ui.busy) "保存中…" else "保存计划",
                onClick = { viewModel.save(dailyGoal, sentenceGoal, focusMinutes, ui.settings.exam) },
                backdrop = backdrop,
                prominent = true,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
        GlassCard(backdrop) {
            Text("同步状态", style = MaterialTheme.typography.titleSmall)
            Text(
                when (ui.sync.status) {
                    SyncStatus.SAVING -> "同步中…"
                    SyncStatus.DIRTY -> "有未同步改动"
                    SyncStatus.OFFLINE -> "当前离线，联网后会自动同步"
                    SyncStatus.SIGNED_OUT -> "未登录，进度只保存在本机"
                    SyncStatus.IDLE -> if (ui.sync.lastSyncedAt > 0) "已同步到账号" else "尚未同步"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
            if (ui.sync.problem.isNotBlank()) {
                Text(
                    ui.sync.problem,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
        if (ui.message.isNotBlank()) {
            Text(
                ui.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            )
        }
        Text(
            "EngStudy $versionName · 语料与后端与 Daily English 共用",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun StepperRow(
    backdrop: Backdrop,
    label: String,
    value: Int,
    unit: String,
    min: Int,
    max: Int,
    step: Int,
    onChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        GlassPillButton(
            text = "−",
            onClick = { onChange((value - step).coerceAtLeast(min)) },
            backdrop = backdrop,
        )
        Text(
            "$value $unit",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        GlassPillButton(
            text = "+",
            onClick = { onChange((value + step).coerceAtMost(max)) },
            backdrop = backdrop,
        )
    }
}

