package me.fujunhao.engstudy.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.mutableStateOf
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
import me.fujunhao.engstudy.ui.glass.LiquidTabBar
import me.fujunhao.engstudy.ui.glass.LiquidTabSpec
import me.fujunhao.engstudy.ui.icons.AppIcons
import me.fujunhao.engstudy.ui.screens.AuthScreen
import me.fujunhao.engstudy.ui.screens.FocusScreen
import me.fujunhao.engstudy.ui.screens.NotebookScreen
import me.fujunhao.engstudy.ui.screens.PaperPracticeScreen
import me.fujunhao.engstudy.ui.screens.PapersScreen
import me.fujunhao.engstudy.ui.screens.ProfileScreen
import me.fujunhao.engstudy.ui.screens.SettingsScreen
import me.fujunhao.engstudy.ui.screens.StatsScreen
import me.fujunhao.engstudy.ui.screens.TranslateScreen
import me.fujunhao.engstudy.ui.screens.WordsScreen

private sealed interface AppRoute {
    data object Root : AppRoute
    data object Auth : AppRoute
    data object Settings : AppRoute
    data class Practice(val setId: String) : AppRoute
    data object Notebook : AppRoute
    data object Stats : AppRoute
    data object Focus : AppRoute
}

@Composable
fun AppScaffold() {
    var tab by remember { mutableIntStateOf(0) }
    var route by remember { mutableStateOf<AppRoute>(AppRoute.Root) }
    BackHandler(enabled = route != AppRoute.Root) { route = AppRoute.Root }
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
            when (route) {
                AppRoute.Root -> when (tab) {
                    0 -> WordsScreen(backdrop)
                    1 -> TranslateScreen(backdrop)
                    2 -> PapersScreen(
                        backdrop = backdrop,
                        onOpenPaper = { route = AppRoute.Practice(it) },
                    )
                    else -> ProfileScreen(
                        backdrop = backdrop,
                        versionName = me.fujunhao.engstudy.BuildConfig.VERSION_NAME,
                        onLogin = { route = AppRoute.Auth },
                        onSettings = { route = AppRoute.Settings },
                        onNotebook = { route = AppRoute.Notebook },
                        onStats = { route = AppRoute.Stats },
                        onFocus = { route = AppRoute.Focus },
                    )
                }
                AppRoute.Auth -> AuthScreen(
                    backdrop = backdrop,
                    onBack = { route = AppRoute.Root },
                    onDone = { route = AppRoute.Root },
                )
                is AppRoute.Practice -> {
                    val practice = route as AppRoute.Practice
                    PaperPracticeScreen(
                        backdrop = backdrop,
                        setId = practice.setId,
                        onBack = { route = AppRoute.Root },
                    )
                }
                AppRoute.Notebook -> NotebookScreen(
                    backdrop = backdrop,
                    onBack = { route = AppRoute.Root },
                )
                AppRoute.Focus -> FocusScreen(
                    backdrop = backdrop,
                    onBack = { route = AppRoute.Root },
                )
                AppRoute.Stats -> StatsScreen(
                    backdrop = backdrop,
                    onBack = { route = AppRoute.Root },
                )
                AppRoute.Settings -> SettingsScreen(
                    backdrop = backdrop,
                    onBack = { route = AppRoute.Root },
                    onLogin = { route = AppRoute.Auth },
                    versionName = me.fujunhao.engstudy.BuildConfig.VERSION_NAME,
                )
            }
        }

        if (route == AppRoute.Root) LiquidTabBar(
            backdrop = backdrop,
            tabs = listOf(
                LiquidTabSpec(AppIcons.Words, "背词"),
                LiquidTabSpec(AppIcons.Translate, "翻译"),
                LiquidTabSpec(AppIcons.Papers, "真题"),
                LiquidTabSpec(AppIcons.Profile, "我的"),
            ),
            selected = tab,
            onSelect = { tab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 12.dp),
        )
    }
}
