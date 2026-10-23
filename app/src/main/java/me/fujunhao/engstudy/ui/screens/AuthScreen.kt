package me.fujunhao.engstudy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyant.backdrop.Backdrop
import me.fujunhao.engstudy.data.repository.AuthState
import me.fujunhao.engstudy.feature.auth.AuthMode
import me.fujunhao.engstudy.feature.auth.AuthViewModel
import me.fujunhao.engstudy.feature.settings.SettingsViewModel
import me.fujunhao.engstudy.ui.glass.GlassCard
import me.fujunhao.engstudy.ui.glass.GlassPillButton

@Composable
fun AuthScreen(
    backdrop: Backdrop,
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    val settings by settingsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(settings.auth) {
        if (settings.auth is AuthState.Member) onDone()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassPillButton("返回", onBack, backdrop)
        }
        Column {
            Text("登录或注册", style = MaterialTheme.typography.headlineSmall)
            Text(
                "学习进度会随账号同步到其他设备",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        GlassCard(backdrop) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassPillButton(
                    "注册",
                    { viewModel.setMode(AuthMode.REGISTER) },
                    backdrop,
                    prominent = ui.mode == AuthMode.REGISTER,
                )
                GlassPillButton(
                    "登录",
                    { viewModel.setMode(AuthMode.LOGIN) },
                    backdrop,
                    prominent = ui.mode == AuthMode.LOGIN,
                )
            }
            OutlinedTextField(
                value = ui.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("邮箱") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
            OutlinedTextField(
                value = ui.password,
                onValueChange = viewModel::updatePassword,
                label = { Text("密码") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
            if (ui.error.isNotBlank()) {
                Text(
                    ui.error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            if (settings.message.isNotBlank()) {
                Text(
                    settings.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            GlassPillButton(
                text = if (ui.busy) "请稍候…" else if (ui.mode == AuthMode.REGISTER) "注册" else "登录",
                onClick = viewModel::submit,
                backdrop = backdrop,
                prominent = true,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}
