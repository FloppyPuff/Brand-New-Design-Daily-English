package me.fujunhao.engstudy.ui.screens

import androidx.compose.foundation.clickable
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
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton
import me.fujunhao.engstudy.ui.glass.GlassPillField

private data class Sentence(val en: String, val zh: String)

private val Sentences = listOf(
    Sentence(
        "The emphasis on data gathered first-hand, combined with a cross-cultural perspective, makes this study a unique and important contribution to the field.",
        "对第一手资料的强调，加上跨文化的视角，使这项研究成为该领域独特而重要的贡献。",
    ),
    Sentence(
        "Only gradually was the by-product of the institution noted, and only more gradually still was this effect considered as a directive factor in the conduct of the institution.",
        "该机构的副产品只是逐渐被人们注意到，而这一作用被视作机构运作的指导性因素的过程则更为缓慢。",
    ),
    Sentence(
        "While it is easy to ignore in our contact with them the effect of our acts upon their disposition, it is not so easy as in dealing with adults.",
        "尽管在与年轻人接触时，我们很容易忽略自己的行为对他们性情的影响，但这并不像与成年人打交道时那样容易忽略。",
    ),
)

@Composable
fun TranslateScreen(backdrop: Backdrop) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column {
                Text("翻译训练", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "真题译句 + 自由输句离线查词",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        item { GlassPillField(backdrop, "输入句子，离线查词提示", modifier = Modifier.fillMaxWidth()) }
        items(Sentences.size) { i -> SentenceCard(backdrop, Sentences[i]) }
    }
}

@Composable
private fun SentenceCard(backdrop: Backdrop, sentence: Sentence) {
    var revealed by remember { mutableStateOf(false) }
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
            { revealed = !revealed },
            backdrop,
            modifier = Modifier.padding(top = 14.dp),
        )
    }
}
