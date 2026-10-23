package me.fujunhao.engstudy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import me.fujunhao.engstudy.feature.study.StudyViewModel
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton

@Composable
fun FocusScreen(
    backdrop: Backdrop,
    onBack: () -> Unit,
    viewModel: StudyViewModel = hiltViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    val focus = ui.focus
    val fraction = if (focus.totalSeconds > 0) {
        (focus.remainingSeconds.toFloat() / focus.totalSeconds).coerceIn(0f, 1f)
    } else {
        1f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlassPillButton("返回", onBack, backdrop)
        }
        Column {
            Text("专注计时", style = MaterialTheme.typography.headlineSmall)
            Text(
                "累计 ${ui.stats?.totalMinutes ?: 0} 分钟 · 今天 ${ui.stats?.todayMinutes ?: 0} 分钟",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        GlassCard(backdrop, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    formatClock(focus.remainingSeconds),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    if (focus.running) "专注中…" else "准备好就开始",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(fraction)
                            .height(10.dp)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    GlassPillButton(
                        if (focus.running) "暂停" else "开始专注",
                        {
                            if (focus.running) viewModel.pauseFocus() else viewModel.startFocus()
                        },
                        backdrop,
                        prominent = !focus.running,
                    )
                    GlassPillButton("重置", viewModel::resetFocus, backdrop)
                }
                Spacer(Modifier.height(4.dp))
            }
        }
        Text(
            "专注时长会累计到学习记录；退出页面计时仍会继续。",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatClock(seconds: Int): String {
    val safe = seconds.coerceAtLeast(0)
    return "%02d:%02d".format(safe / 60, safe % 60)
}
