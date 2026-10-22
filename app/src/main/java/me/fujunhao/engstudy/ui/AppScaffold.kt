package me.fujunhao.engstudy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import me.fujunhao.engstudy.ui.glass.GlassTabBar
import me.fujunhao.engstudy.ui.glass.TabSpec
import me.fujunhao.engstudy.ui.icons.AppIcons
import me.fujunhao.engstudy.ui.screens.PapersScreen
import me.fujunhao.engstudy.ui.screens.TranslateScreen
import me.fujunhao.engstudy.ui.screens.WordsScreen

@Composable
fun AppScaffold() {
    var tab by remember { mutableIntStateOf(0) }
    val backdrop = rememberLayerBackdrop()

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Everything inside this Box is recorded into the backdrop layer,
        // so glass components floating above can blur/refract the real content.
        Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
            when (tab) {
                0 -> WordsScreen(backdrop)
                1 -> TranslateScreen(backdrop)
                else -> PapersScreen(backdrop)
            }
        }

        GlassTabBar(
            backdrop = backdrop,
            tabs = listOf(
                TabSpec(AppIcons.Words, "背词"),
                TabSpec(AppIcons.Translate, "翻译"),
                TabSpec(AppIcons.Papers, "真题"),
            ),
            selected = tab,
            onSelect = { tab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 40.dp, vertical = 20.dp),
        )
    }
}
