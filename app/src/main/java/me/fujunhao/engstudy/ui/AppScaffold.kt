package me.fujunhao.engstudy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
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
    val useLayer = GlassDebug.useLayer
    val layerBackdrop = rememberLayerBackdrop()
    val staticBackdrop = rememberCanvasBackdrop {
        drawRect(
            Brush.linearGradient(
                colors = listOf(Color(0xFF8FB1FF), Color(0xFFE4D3FF), Color(0xFFB9E6D9)),
                start = Offset.Zero,
                end = Offset(size.width, size.height),
            ),
        )
    }
    val backdrop = if (useLayer) layerBackdrop else staticBackdrop

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Everything inside this Box is recorded into the backdrop layer,
        // so glass components floating above can blur/refract the real content.
        Box(
            Modifier
                .fillMaxSize()
                .then(if (useLayer) Modifier.layerBackdrop(layerBackdrop) else Modifier),
        ) {
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
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 40.dp, vertical = 12.dp),
        )
    }
}
