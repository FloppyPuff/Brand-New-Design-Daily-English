package me.fujunhao.engstudy.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.data.repository.SyncStatus
import me.fujunhao.engstudy.feature.settings.SettingsViewModel
import me.fujunhao.engstudy.feature.study.StudyViewModel
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton
import me.fujunhao.engstudy.ui.icons.AppIcons

@Composable
fun ProfileScreen(
    backdrop: Backdrop,
    versionName: String = "0.2.0",
    studyViewModel: StudyViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val study by studyViewModel.state.collectAsStateWithLifecycle()
    val settings by settingsViewModel.state.collectAsStateWithLifecycle()
    val stats = study.stats
    val email = settings.sync.email.ifBlank { null }
    val syncLabel = when (settings.sync.status) {
        SyncStatus.SAVING -> "同步中…"
        SyncStatus.DIRTY -> "有未同步改动"
        SyncStatus.OFFLINE -> "离线"
        SyncStatus.SIGNED_OUT -> "未登录"
        SyncStatus.IDLE -> if (settings.sync.lastSyncedAt > 0) "已同步" else "尚未同步"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column {
                Text("我的", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "账号、学习记录与设置",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        item {
            AccountCard(
                backdrop = backdrop,
                email = email,
                syncing = settings.sync.status == SyncStatus.SAVING,
                onLogout = settingsViewModel::logout,
            )
        }
        item {
            GlassCard(backdrop) {
                Text("学习记录", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    StatColumn("已学", stats?.learned ?: 0, "词")
                    StatColumn("熟悉", stats?.mastered ?: 0, "词")
                    StatColumn("连续", stats?.streak ?: 0, "天")
                    StatColumn("专注", stats?.totalMinutes ?: 0, "分")
                }
            }
        }
        item {
            GlassCard(backdrop, contentPadding = PaddingValues(vertical = 6.dp)) {
                EntryRow("日常词表", "到期 ${stats?.due ?: 0} 词 · 待加固 ${stats?.difficult ?: 0} 词") {}
                EntryRow("我的笔记", "收藏词句、薄弱与错题（迁移中）") {}
                EntryRow("复习计划", "到期待复习的单词（迁移中）") {}
            }
        }
        item {
            GlassCard(backdrop, contentPadding = PaddingValues(vertical = 6.dp)) {
                EntryRow("专注计时", "累计 ${stats?.totalMinutes ?: 0} 分钟（迁移中）") {}
                EntryRow("同步", syncLabel, settingsViewModel::syncNow)
                EntryRow("设置", "强调色、账号与关于（迁移中）") {}
            }
        }
        if (settings.message.isNotBlank()) {
            item {
                Text(
                    settings.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                )
            }
        }
        item {
            Text(
                "EngStudy $versionName",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun AccountCard(
    backdrop: Backdrop,
    email: String?,
    syncing: Boolean,
    onLogout: () -> Unit,
) {
    GlassCard(backdrop) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    AppIcons.Profile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp),
                )
            }
            Column(Modifier.weight(1f).padding(start = 14.dp)) {
                Text(
                    email ?: "未登录",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    if (email != null) "已登录 · 学习进度自动同步" else "登录后学习进度可跨设备同步",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (email == null) {
            GlassPillButton(
                text = "登录或注册（迁移中）",
                onClick = {},
                backdrop = backdrop,
                prominent = true,
                modifier = Modifier.padding(top = 16.dp),
            )
        } else {
            GlassPillButton(
                text = if (syncing) "同步中…" else "退出登录",
                onClick = onLogout,
                backdrop = backdrop,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun StatColumn(label: String, value: Int, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "$label · $unit",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EntryRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Icon(
            AppIcons.ArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}
