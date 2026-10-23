package me.fujunhao.engstudy.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.Shadow
import me.fujunhao.engstudy.ui.GlassDebug

/** Central glass parameters: tune the whole app's liquid-glass look here. */
object GlassSpec {
    val CardCorner: Dp = 24.dp
    val BarCorner: Dp = 34.dp
    val BlurRadius: Dp = 22.dp
    val BarBlurRadius: Dp = 30.dp
    val RefractionHeight: Dp = 3.dp
    val RefractionAmount: Float = 1.0f
}

@Composable
fun glassTint(): Color =
    MaterialTheme.colorScheme.surface.copy(alpha = if (isSystemInDarkTheme()) 0.60f else 0.52f)

@Composable
fun glassHighlight(): Highlight =
    Highlight(
        width = 0.6.dp,
        blurRadius = 0.5.dp,
        alpha = if (isSystemInDarkTheme()) 0.45f else 0.85f,
    )

@Composable
fun glassShadow(): Shadow =
    Shadow(
        radius = 18.dp,
        offset = DpOffset(0.dp, 6.dp),
        color = Color.Black.copy(alpha = if (isSystemInDarkTheme()) 0.45f else 0.10f),
    )

/**
 * The one glass surface modifier every component builds on.
 * Samples the content recorded into [backdrop] (see Modifier.layerBackdrop),
 * blurs + refracts it, then lays tint / highlight / shadow on top.
 *
 * IMPORTANT: a layer-sampling surface must NOT live inside the subtree that
 * Modifier.layerBackdrop records. On Android 17 (hwui/Vulkan) drawing a
 * GraphicsLayer while that same layer is being recorded creates a cyclic
 * RenderNode, and prepareTreeImpl recurses until RenderThread stack-overflows
 * (SIGSEGV). Hence pages render flat surfaces ([enabled] = false when
 * GlassDebug.flatPages) and only floating controls (tab bar) sample the layer.
 */
@Composable
fun Modifier.glass(
    backdrop: Backdrop,
    shape: Shape = RoundedCornerShape(GlassSpec.CardCorner),
    tint: Color = glassTint(),
    blurRadius: Dp = GlassSpec.BlurRadius,
    refractionAmount: Float = GlassSpec.RefractionAmount,
    highlight: Highlight? = glassHighlight(),
    shadow: Shadow? = glassShadow(),
    extraSurface: (DrawScope.() -> Unit)? = null,
    enabled: Boolean = true,
): Modifier {
    if (!enabled || GlassDebug.mode == GlassDebug.Mode.OFF) {
        return this.background(tint, shape)
    }
    return this.drawBackdrop(
        backdrop = backdrop,
        shape = { shape },
        effects = {
            if (GlassDebug.useBlur) {
                blur(blurRadius.toPx())
            }
            if (GlassDebug.useLens) {
                lens(GlassSpec.RefractionHeight.toPx(), refractionAmount)
            }
        },
        highlight = { highlight },
        shadow = { shadow },
        onDrawSurface = {
            drawRect(tint)
            extraSurface?.invoke(this)
        },
    )
}
