package me.fujunhao.engstudy

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat
import me.fujunhao.engstudy.ui.AppScaffold
import me.fujunhao.engstudy.ui.GlassDebug
import me.fujunhao.engstudy.ui.theme.EngStudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requested = intent?.getStringExtra("glass_mode")
        GlassDebug.mode = requested?.let { name ->
            runCatching { GlassDebug.Mode.valueOf(name.uppercase()) }.getOrNull()
        } ?: GlassDebug.Mode.FULL_OUT
        Log.i("GlassDebug", "glass_mode=${GlassDebug.mode}")
        enableEdgeToEdge()
        val night = resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !night
            isAppearanceLightNavigationBars = !night
        }
        setContent {
            EngStudyTheme {
                AppScaffold()
            }
        }
    }
}
