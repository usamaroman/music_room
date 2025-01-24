package by.eapp.musicroom.screens.auth.viewstate

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import by.eapp.musicroom.ui.theme.PrimaryColor

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    text: String,
    onDismiss: () -> Unit,
) {
    var shouldShowDialog by remember { mutableStateOf(true) }

    if (shouldShowDialog) {
        AlertDialog(
            onDismissRequest = { shouldShowDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        shouldShowDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(PrimaryColor)
                ) {
                    Text(
                        text = "Confirm",
                        color = Color.White
                    )
                }
            },
            text = { Text(text) },
            title = { Text("Error") }
        )
    }
}
@Composable
@Preview(showBackground = true)
fun previewErrorScreen() {
    ErrorScreen(text = "Тестовый тест тест тест тест тест тест тест", onDismiss = {})
}