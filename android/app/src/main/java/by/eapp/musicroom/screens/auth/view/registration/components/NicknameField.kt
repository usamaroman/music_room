package by.eapp.musicroom.screens.auth.view.registration.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import by.eapp.musicroom.R
import by.eapp.musicroom.screens.auth.components.TextInputField

@Composable
fun NicknameField(
    nickname: String,
    onNicknameChange: (String) -> Unit
) {
    Text(
        text = stringResource(R.string.mr_nickname),
        modifier = Modifier.padding(bottom = 5.dp),
        color = Color.White
    )
    TextInputField(
        value = nickname,
        onValueChange = onNicknameChange,
        placeholderText = stringResource(R.string.mr_placeholder_nickname),
    )
}
