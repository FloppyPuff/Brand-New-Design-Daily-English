package me.fujunhao.engstudy.ui.glass

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

data class LiquidTabSpec(
    val icon: ImageVector,
    val label: String,
)

private val BarHeight = 70.dp
private val ChipHeight = 56.dp
private val DockHeight = 84.dp
private val BarInset = 4.dp
private val ChipGap = 10.dp
private const val PressedScale = 1.45f

/**
 * Liquid-glass tab bar in the EngStudy visual language: a frosted capsule with
 * a floating lens chip. The chip rests inset inside the bar, grows 1.45x and
 * overflows while pressed or dragged, and samples a combined backdrop so the
 * accent labels magnify inside the lens. Taps and horizontal drags share one
 * gesture layer, matching the Liquid Glass baseline.
 */
@Composable
fun LiquidTabBar(
    backdrop: Backdrop,
    tabs: List<LiquidTabSpec>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (tabs.isEmpty()) return
    val dark = isSystemInDarkTheme()
    val accent = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.onSurfaceVariant
    val containerColor = glassTint().copy(alpha = if (dark) 0.68f else 0.62f)
    val chipRest = if (dark) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.08f)
    val scope = rememberCoroutineScope()
    val lastIndex = tabs.lastIndex
    val tabsBackdrop = rememberLayerBackdrop()
    val combinedBackdrop = rememberCombinedBackdrop(backdrop, tabsBackdrop)

    val position = remember { Animatable(selected.coerceIn(0, lastIndex).toFloat()) }
    val pressProgress = remember { Animatable(0f) }
    val pressScaleX = remember { Animatable(1f) }
    val pressScaleY = remember { Animatable(1f) }
    val velocity = remember { Animatable(0f) }

    fun press() {
        scope.launch {
            launch { pressProgress.animateTo(1f, spring(1f, 1000f, 0.001f)) }
            launch { pressScaleX.animateTo(PressedScale, spring(0.6f, 250f, 0.001f)) }
            launch { pressScaleY.animateTo(PressedScale, spring(0.7f, 250f, 0.001f)) }
        }
    }

    fun release() {
        scope.launch {
            launch { pressProgress.animateTo(0f, spring(1f, 1000f, 0.001f)) }
            launch { pressScaleX.animateTo(1f, spring(0.6f, 250f, 0.001f)) }
            launch { pressScaleY.animateTo(1f, spring(0.7f, 250f, 0.001f)) }
            launch { velocity.animateTo(0f, spring(0.5f, 300f, 0.05f)) }
        }
    }

    fun glideTo(target: Float) {
        scope.launch {
            press()
            position.animateTo(target, spring(1f, 900f, 0.001f))
            release()
        }
    }

    var bootstrapped by remember { mutableStateOf(false) }
    LaunchedEffect(selected) {
        val target = selected.coerceIn(0, lastIndex).toFloat()
        if (!bootstrapped) {
            bootstrapped = true
            position.snapTo(target)
        } else {
            glideTo(target)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(DockHeight),
        contentAlignment = Alignment.Center,
    ) {
        val density = LocalDensity.current
        val insetPx = with(density) { BarInset.toPx() }
        val gapPx = with(density) { ChipGap.toPx() }
        val tabWidth = (maxWidth - BarInset * 2) / tabs.size
        val tabWidthPx = with(density) { tabWidth.toPx() }
        val progress = pressProgress.value
        val positionValue = position.value

        val glow = Modifier.drawWithContent {
            drawContent()
            val p = pressProgress.value
            if (p > 0.01f) {
                val cx = insetPx + (position.value + 0.5f) * tabWidthPx
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.22f * p),
                            Color.White.copy(alpha = 0f),
                        ),
                        center = Offset(cx, size.height / 2f),
                        radius = size.minDimension * 1.5f,
                    ),
                    blendMode = BlendMode.Plus,
                )
            }
        }

        // 1. visible glass bar.
        Box(
            Modifier
                .align(Alignment.Center)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedCornerShape(GlassSpec.BarCorner) },
                    effects = {
                        vibrancy()
                        blur(30.dp.toPx())
                        lens(24.dp.toPx(), 24.dp.toPx())
                    },
                    highlight = { Highlight.Default.copy(alpha = 0.85f) },
                    layerBlock = {
                        val scale = 1f + 16.dp.toPx() / size.width * progress
                        scaleX = scale
                        scaleY = scale
                    },
                    onDrawSurface = { drawRect(containerColor) },
                )
                .then(glow)
                .height(BarHeight)
                .fillMaxWidth(),
        )

        // 2. hidden accent-tinted twin, recorded for the lens to magnify.
        Box(
            Modifier
                .align(Alignment.Center)
                .alpha(0f)
                .layerBackdrop(tabsBackdrop)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedCornerShape(GlassSpec.BarCorner) },
                    effects = {
                        vibrancy()
                        blur(10.dp.toPx())
                        lens(24.dp.toPx() * progress, 24.dp.toPx() * progress)
                    },
                    highlight = { Highlight.Default.copy(alpha = progress) },
                    onDrawSurface = { drawRect(containerColor) },
                )
                .then(glow)
                .height(DockHeight)
                .fillMaxWidth()
                .graphicsLayer { colorFilter = ColorFilter.tint(accent) },
        ) {
            TabRow(
                tabs = tabs,
                selected = selected,
                accent = accent,
                ink = ink,
                positionValue = positionValue,
                fadeUnderChip = false,
            )
        }

        // 3. floating lens chip: inset at rest, overflows while pressed.
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .graphicsLayer {
                    translationX = insetPx + gapPx / 2f + positionValue * tabWidthPx
                }
                .drawBackdrop(
                    backdrop = combinedBackdrop,
                    shape = { RoundedCornerShape(percent = 50) },
                    effects = {
                        lens(
                            10.dp.toPx() * progress,
                            14.dp.toPx() * progress,
                            chromaticAberration = true,
                        )
                    },
                    highlight = { Highlight.Default.copy(alpha = 0.42f + 0.58f * progress) },
                    shadow = { Shadow.Default.copy(alpha = 0.16f + 0.44f * progress) },
                    innerShadow = {
                        InnerShadow.Default.copy(radius = 12.dp, alpha = 0.08f + 0.32f * progress)
                    },
                    layerBlock = {
                        var sx = pressScaleX.value
                        var sy = pressScaleY.value
                        val v = velocity.value / 10f
                        sx /= 1f - (v * 0.75f).coerceIn(-0.2f, 0.2f)
                        sy *= 1f - (v * 0.25f).coerceIn(-0.2f, 0.2f)
                        scaleX = sx
                        scaleY = sy
                    },
                    onDrawSurface = {
                        drawRect(accent.copy(alpha = if (dark) 0.16f else 0.10f), alpha = 1f - progress)
                        drawRect(chipRest, alpha = (1f - progress) * 0.35f)
                        drawRect(Color.Black.copy(alpha = 0.03f * progress))
                    },
                )
                .height(ChipHeight)
                .width(tabWidth - ChipGap),
        )

        // 4. icon row above the chip; labels fade out under the lens.
        Box(
            Modifier
                .align(Alignment.Center)
                .height(BarHeight)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            TabRow(
                tabs = tabs,
                selected = selected,
                accent = accent,
                ink = ink,
                positionValue = positionValue,
                fadeUnderChip = true,
            )
        }

        // 5. one gesture layer for taps and drags.
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(tabs.size, tabWidthPx, insetPx, selected, lastIndex) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        val downX = down.position.x
                        var dragging = false
                        var accumulated = 0f
                        var change = down
                        while (change.pressed) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            val dx = change.position.x - change.previousPosition.x
                            accumulated += dx
                            if (!dragging && abs(accumulated) > viewConfiguration.touchSlop) {
                                dragging = true
                                press()
                            }
                            if (dragging) {
                                change.consume()
                                scope.launch {
                                    position.snapTo(
                                        (position.value + dx / tabWidthPx)
                                            .coerceIn(0f, lastIndex.toFloat()),
                                    )
                                }
                                scope.launch {
                                    velocity.animateTo(dx * 60f, spring(0.5f, 300f, 0.05f))
                                }
                            }
                        }
                        if (dragging) {
                            val target = position.value.roundToInt().coerceIn(0, lastIndex)
                            if (target != selected) onSelect(target) else glideTo(target.toFloat())
                        } else {
                            val index = floor((downX - insetPx) / tabWidthPx)
                                .toInt()
                                .coerceIn(0, lastIndex)
                            if (index != selected) onSelect(index)
                        }
                    }
                },
        )
    }
}

@Composable
private fun TabRow(
    tabs: List<LiquidTabSpec>,
    selected: Int,
    accent: Color,
    ink: Color,
    positionValue: Float,
    fadeUnderChip: Boolean,
) {
    Row(Modifier.fillMaxSize()) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selected
            val fade = if (fadeUnderChip) {
                ((abs(positionValue - index) - 0.15f) / 0.35f).coerceIn(0f, 1f)
            } else {
                1f
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .alpha(fade),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    tab.icon,
                    contentDescription = tab.label,
                    tint = if (isSelected) accent else ink,
                    modifier = Modifier.size(23.dp),
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    tab.label,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                    color = if (isSelected) accent else ink,
                )
            }
        }
    }
}
