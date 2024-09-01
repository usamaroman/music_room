package by.eapp.musicroom.screens.view.submit


import HandleNavigation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import by.eapp.musicroom.R
import by.eapp.musicroom.domain.model.SubmitData
import by.eapp.musicroom.screens.AuthorizationViewModel

@Composable
fun SubmitCode(
    navController: NavHostController,
    viewModel: AuthorizationViewModel
) {

    val codeLength = 4
    val focusRequesters = remember { List(codeLength) { FocusRequester() } }
    var code by remember { mutableStateOf(List(codeLength) { "" }) }
    val focusManager = LocalFocusManager.current
    val state by viewModel.stateUi.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.mr_submit_code),
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            code.forEachIndexed { index, value ->
                CodeInputField(
                    value = value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1) {
                            code = code.toMutableList().apply { this[index] = newValue }
                            if (newValue.isNotEmpty() && index < codeLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            } else if (newValue.isNotEmpty() && index == codeLength - 1) {
                                focusManager.clearFocus()
                                val completeCode = code.joinToString("")
                                if (completeCode.length == codeLength) {

                                    viewModel.submitCode(completeCode)

                                }
                            }
                        }
                    },
                    focusRequester = focusRequesters[index]
                )
                HandleNavigation(stateUi = state, navController = navController)
            }
        }
    }
}

@Composable
fun CodeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = if (isFocused) Color.White else Color.LightGray

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .size(60.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused
            },
        keyboardActions = KeyboardActions(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor
        )
    )
}

//
//@[Preview(showBackground = true) Composable]
//fun SubmitCodePreview() {
//    SubmitCode()
//}