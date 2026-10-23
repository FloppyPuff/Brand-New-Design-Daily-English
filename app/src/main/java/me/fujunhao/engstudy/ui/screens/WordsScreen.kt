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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton

private data class Word(val text: String, val phonetic: String, val meaning: String)

private val Queue = listOf(
    Word("ambiguous", "/æmˈbɪɡjuəs/", "adj. 含糊的；模棱两可的"),
    Word("preclude", "/prɪˈkluːd/", "v. 阻止；排除"),
    Word("scrutinize", "/ˈskruːtənaɪz/", "v. 仔细检查；详审"),
    Word("ubiquitous", "/juːˈbɪkwɪtəs/", "adj. 普遍存在的；无处不在的"),
    Word("pragmatic", "/præɡˈmætɪk/", "adj. 务实的；实用主义的"),
    Word("alleviate", "/əˈliːvieɪt/", "v. 减轻；缓和"),
    Word("cognitive", "/ˈkɒɡnətɪv/", "adj. 认知的；认识的"),
)

@Composable
fun WordsScreen(backdrop: Backdrop) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column {
                Text("今日复习", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "剩余 20 词 · 新词 5 · 待加固 3",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        item { ReviewCard(backdrop, Queue[0]) }
        items(Queue.size - 1) { i ->
            MiniWordCard(backdrop, Queue[i + 1])
        }
    }
}

@Composable
private fun ReviewCard(backdrop: Backdrop, word: Word) {
    var revealed by remember { mutableStateOf(false) }
    GlassCard(backdrop) {
        Text(word.text, style = MaterialTheme.typography.headlineMedium)
        Text(
            word.phonetic,
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
                .clickable { revealed = true }
                .fillMaxWidth(),
        )
        Row(
            modifier = Modifier.padding(top = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GlassPillButton("忘记", {}, backdrop)
            GlassPillButton("模糊", {}, backdrop)
            GlassPillButton("认识", {}, backdrop, prominent = true)
        }
    }
}

@Composable
private fun MiniWordCard(backdrop: Backdrop, word: Word) {
    GlassCard(backdrop, contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(word.text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(word.phonetic, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
