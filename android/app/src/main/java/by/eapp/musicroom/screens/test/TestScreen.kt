package by.eapp.musicroom.screens.test

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TestScreen(
    vm:  TestViewModel
) {
    val state by vm.state.collectAsState()

    Text(text = state, fontSize = 45.sp, color = Color.Black)
}