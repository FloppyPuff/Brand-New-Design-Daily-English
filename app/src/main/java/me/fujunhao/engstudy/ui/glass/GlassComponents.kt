package me.fujunhao.engstudy.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.ui.GlassDebug
import me.fujunhao.engstudy.ui.icons.AppIcons

@Composable
fun GlassCard(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .glass(backdrop, enabled = !GlassDebug.flatPages)
            .padding(contentPadding),
        content = content,
    )
}

@Composable
fun GlassPillButton(
    text: String,
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    prominent: Boolean = false,
) {
    val tint = if (prominent) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.92f)
    } else {
        glassTint()
    }
    val contentColor = if (prominent) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = modifier
            .glass(
                backdrop = backdrop,
                shape = RoundedCornerShape(50),
                tint = tint,
                highlight = if (prominent) null else glassHighlight(),
                shadow = if (prominent) null else glassShadow(),
                enabled = !GlassDebug.flatPages,
            )
            .clickable(onClick = onClick)
            .height(44.dp)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = contentColor, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun GlassFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = AppIcons.Plus,
) {
    val dark = isSystemInDarkTheme()
    Box(
        modifier = modifier
            .size(58.dp)
            .background(if (dark) Color(0xFFE8EAED) else Color(0xFF141719), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = if (dark) Color(0xFF141719) else Color.White)
    }
}

@Composable
fun GlassPillField(
    backdrop: Backdrop,
    hint: String,
    modifier: Modifier = Modifier,
    onSend: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .glass(backdrop, shape = RoundedCornerShape(50), enabled = !GlassDebug.flatPages)
            .height(56.dp)
            .padding(start = 18.dp, end = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            AppIcons.TranslateGlyph,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            hint,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .clickable(onClick = onSend),
            contentAlignment = Alignment.Center,
        ) {
            Icon(AppIcons.ArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

data class TabSpec(val icon: ImageVector, val label: String)

@Composable
fun GlassTabBar(
    backdrop: Backdrop,
    tabs: List<TabSpec>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .glass(
                backdrop = backdrop,
                shape = RoundedCornerShape(GlassSpec.BarCorner),
                tint = glassTint().copy(alpha = if (isSystemInDarkTheme()) 0.68f else 0.62f),
                blurRadius = GlassSpec.BarBlurRadius,
            )
            .height(70.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEachIndexed { index, tab ->
            TabItem(
                tab = tab,
                selected = index == selected,
                onClick = { onSelect(index) },
            )
        }
    }
}

@Composable
private fun RowScope.TabItem(tab: TabSpec, selected: Boolean, onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    val idle = MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp)
            .background(
                if (selected) accent.copy(alpha = 0.10f) else Color.Transparent,
                RoundedCornerShape(50),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(tab.icon, contentDescription = tab.label, tint = if (selected) accent else idle)
        Text(
            tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) accent else idle,
        )
    }
}

@Composable
fun GlassChip(text: String, backdrop: Backdrop, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .glass(
                backdrop = backdrop,
                shape = RoundedCornerShape(50),
                blurRadius = 12.dp,
                highlight = null,
                shadow = null,
                enabled = !GlassDebug.flatPages,
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun GlassDividerRow(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().height(1.dp).background(Color.Transparent))
}
