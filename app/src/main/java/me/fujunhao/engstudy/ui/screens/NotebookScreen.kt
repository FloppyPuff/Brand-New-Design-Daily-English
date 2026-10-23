package me.fujunhao.engstudy.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import me.fujunhao.engstudy.feature.notebook.NotebookViewModel
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton

@Composable
fun NotebookScreen(
    backdrop: Backdrop,
    onBack: () -> Unit,
    viewModel: NotebookViewModel = hiltViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    val wordCount = ui.data.words.size
    val sentenceCount = ui.data.sentences.size

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
                    Text("我的笔记", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "收藏 $wordCount 词 · $sentenceCount 句",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassPillButton(
                    "我的收藏",
                    { viewModel.setTab("saved") },
                    backdrop,
                    prominent = ui.tab == "saved",
                )
                GlassPillButton(
                    "薄弱 & 错题",
                    { viewModel.setTab("mistakes") },
                    backdrop,
                    prominent = ui.tab == "mistakes",
                )
            }
        }
        item {
            OutlinedTextField(
                value = ui.query,
                onValueChange = viewModel::setQuery,
                placeholder = { Text("搜索单词、释义或句子…") },
                singleLine = true,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        when {
            ui.loading -> item {
                GlassCard(backdrop, modifier = Modifier.fillMaxWidth()) { Text("正在加载笔记…") }
            }
            ui.error.isNotBlank() -> item {
                GlassCard(backdrop, modifier = Modifier.fillMaxWidth()) {
                    Text(ui.error)
                    GlassPillButton(
                        "重试",
                        viewModel::retry,
                        backdrop,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }
            }
            wordCount == 0 && sentenceCount == 0 -> item {
                GlassCard(backdrop, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        if (ui.tab == "saved") "还没有收藏的单词或句子。" else "薄弱与错题会在这里汇总。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            else -> {
                items(wordCount) { index ->
                    val item = ui.data.words[index]
                    GlassCard(
                        backdrop,
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                item.word.id,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                item.word.phonetic.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            item.word.meaning,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
                items(sentenceCount) { index ->
                    val item = ui.data.sentences[index]
                    GlassCard(backdrop, modifier = Modifier.fillMaxWidth()) {
                        Text(item.sentence.en, style = MaterialTheme.typography.bodyMedium)
                        if (item.sentence.zh.isNotBlank()) {
                            Text(
                                item.sentence.zh,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
