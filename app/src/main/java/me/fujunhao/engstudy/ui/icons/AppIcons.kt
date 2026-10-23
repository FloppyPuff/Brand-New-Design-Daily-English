package me.fujunhao.engstudy.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Hand-drawn flat glyphs; kept tiny on purpose, tinted by Icon(). */
object AppIcons {

    val Words: ImageVector = vector("words") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 4.5f)
            curveTo(7f, 3f, 10f, 3f, 12f, 4.5f)
            curveTo(14f, 3f, 17f, 3f, 20f, 4.5f)
            lineTo(20f, 19f)
            curveTo(17f, 17.8f, 14f, 17.8f, 12f, 19f)
            curveTo(10f, 17.8f, 7f, 17.8f, 4f, 19f)
            close()
        }
    }

    val Translate: ImageVector = vector("translate") {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.EvenOdd) {
            moveTo(2f, 12f)
            arcToRelative(10f, 10f, 0f, false, true, 20f, 0f)
            arcToRelative(10f, 10f, 0f, false, true, -20f, 0f)
            close()
            moveTo(4f, 12f)
            arcToRelative(8f, 8f, 0f, false, true, 16f, 0f)
            arcToRelative(8f, 8f, 0f, false, true, -16f, 0f)
            close()
            moveTo(4f, 11f)
            lineTo(20f, 11f)
            lineTo(20f, 13f)
            lineTo(4f, 13f)
            close()
            moveTo(11f, 4f)
            lineTo(13f, 4f)
            lineTo(13f, 20f)
            lineTo(11f, 20f)
            close()
        }
    }

    val Papers: ImageVector = vector("papers") {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.EvenOdd) {
            moveTo(6f, 2f)
            lineTo(15f, 2f)
            lineTo(20f, 7f)
            lineTo(20f, 22f)
            lineTo(6f, 22f)
            close()
            moveTo(9f, 11f)
            lineTo(17f, 11f)
            lineTo(17f, 13f)
            lineTo(9f, 13f)
            close()
            moveTo(9f, 15f)
            lineTo(17f, 15f)
            lineTo(17f, 17f)
            lineTo(9f, 17f)
            close()
        }
    }

    val Profile: ImageVector = vector("profile") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 3.5f)
            curveTo(14.5f, 3.5f, 16.5f, 5.5f, 16.5f, 8f)
            curveTo(16.5f, 10.5f, 14.5f, 12.5f, 12f, 12.5f)
            curveTo(9.5f, 12.5f, 7.5f, 10.5f, 7.5f, 8f)
            curveTo(7.5f, 5.5f, 9.5f, 3.5f, 12f, 3.5f)
            close()
            moveTo(4.5f, 20.5f)
            curveTo(4.5f, 16.6f, 7.9f, 14.3f, 12f, 14.3f)
            curveTo(16.1f, 14.3f, 19.5f, 16.6f, 19.5f, 20.5f)
            lineTo(19.5f, 21f)
            lineTo(4.5f, 21f)
            close()
        }
    }

    val Plus: ImageVector = vector("plus") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(11f, 5f)
            lineTo(13f, 5f)
            lineTo(13f, 11f)
            lineTo(19f, 11f)
            lineTo(19f, 13f)
            lineTo(13f, 13f)
            lineTo(13f, 19f)
            lineTo(11f, 19f)
            lineTo(11f, 13f)
            lineTo(5f, 13f)
            lineTo(5f, 11f)
            lineTo(11f, 11f)
            close()
        }
    }

    val ArrowRight: ImageVector = vector("arrowRight") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(13f, 5f)
            lineTo(20f, 12f)
            lineTo(13f, 19f)
            lineTo(11.6f, 17.6f)
            lineTo(16.2f, 13f)
            lineTo(4f, 13f)
            lineTo(4f, 11f)
            lineTo(16.2f, 11f)
            lineTo(11.6f, 6.4f)
            close()
        }
    }

    val TranslateGlyph: ImageVector = vector("glyphA") {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.EvenOdd) {
            moveTo(11f, 4f)
            lineTo(13f, 4f)
            lineTo(19f, 20f)
            lineTo(17f, 20f)
            lineTo(15.4f, 15.5f)
            lineTo(8.6f, 15.5f)
            lineTo(7f, 20f)
            lineTo(5f, 20f)
            close()
            moveTo(12f, 8.5f)
            lineTo(9.4f, 13.5f)
            lineTo(14.6f, 13.5f)
            close()
        }
    }

    private fun vector(name: String, block: ImageVector.Builder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply(block).build()
}
