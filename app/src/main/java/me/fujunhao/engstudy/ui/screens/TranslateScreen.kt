package me.fujunhao.engstudy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.data.content.SentenceDto
import me.fujunhao.engstudy.feature.study.StudyViewModel
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton
import me.fujunhao.engstudy.ui.glass.GlassPillField

private const val SentencePageSize = 30

@Composable
fun TranslateScreen(backdrop: Backdrop, viewModel: StudyViewModel = hiltViewModel()) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    val sentences = ui.content?.sentences.orEmpty().take(SentencePageSize)
    var revealed by remember { mutableStateOf<Set<String>>(emptySet()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column {
                Text("翻译训练", style = MaterialTheme.typography.headlineSmall)
                Text(
                    if (ui.loading) "正在加载语料…"
                    else "${ui.content?.sentences?.size ?: 0} 句长难句与段落 · 真题译句",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        item { GlassPillField(backdrop, "输入句子，离线查词提示", modifier = Modifier.fillMaxWidth()) }
        if (ui.error.isNotBlank()) {
            item {
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
        }
        items(sentences.size) { index ->
            val sentence = sentences[index]
            SentenceCard(
                backdrop = backdrop,
                sentence = sentence,
                revealed = sentence.id in revealed,
                onToggle = {
                    revealed = if (sentence.id in revealed) revealed - sentence.id
                    else revealed + sentence.id
                },
            )
        }
    }
}

@Composable
private fun SentenceCard(
    backdrop: Backdrop,
    sentence: SentenceDto,
    revealed: Boolean,
    onToggle: () -> Unit,
) {
    GlassCard(backdrop) {
        Text(sentence.en, style = MaterialTheme.typography.bodyLarge)
        if (revealed) {
            Text(
                sentence.zh,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
        GlassPillButton(
            if (revealed) "收起参考译文" else "对照参考译文",
            onToggle,
            backdrop,
            modifier = Modifier.padding(top = 14.dp),
        )
    }
}
