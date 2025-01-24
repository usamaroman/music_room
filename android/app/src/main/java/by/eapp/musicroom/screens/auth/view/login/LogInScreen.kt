package by.eapp.musicroom.screens.auth.view.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import by.eapp.musicroom.R
import by.eapp.musicroom.domain.model.auth.LoginData
import by.eapp.musicroom.navigation.Screens
import by.eapp.musicroom.screens.auth.AuthorizationViewModel
import by.eapp.musicroom.screens.auth.LoginScreenAction
import by.eapp.musicroom.screens.auth.LoginScreenState
import by.eapp.musicroom.screens.auth.components.LogInButton
import by.eapp.musicroom.screens.auth.components.PasswordTextInputField
import by.eapp.musicroom.screens.auth.components.TextInputField

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthorizationViewModel,
) {
    val state by viewModel.stateUi.collectAsState()

    LoginContent(
        state = state,
        onLoginClick = { email, password ->
            viewModel.dispatch(
                LoginScreenAction.LoginUser(
                    LoginData(email = email, password = password)
                )
            )
        },
        onSignUpClick = { navController.navigate(Screens.RegistrationScreen.route) }
    )
}

@Composable
fun LoginContent(
    state: LoginScreenState,
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(70.dp))
        Text(
            text = stringResource(R.string.mr_sign_in),
            fontSize = 50.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(70.dp))

        Text(
            text = "Email",
            modifier = Modifier.padding(bottom = 5.dp),
            color = Color.White
        )
        TextInputField(
            value = email,
            onValueChange = { email = it },
            placeholderText = stringResource(R.string.mr_email_placeholder)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.mr_password),
            modifier = Modifier.padding(bottom = 5.dp),
            color = Color.White
        )
        PasswordTextInputField(
            password = password,
            onPasswordChange = { password = it },
            showPassword = showPassword,
            onShowPasswordChange = { showPassword = it }
        )
        Spacer(modifier = Modifier.height(40.dp))
        LogInButton(onClick = { onLoginClick(email, password) })

        Spacer(modifier = Modifier.height(40.dp))
        Text(text = stringResource(R.string.mr_don_t_have_an_account), color = Color.White)
        Text(
            text = stringResource(R.string.mr_sign_up),
            color = Color.White,
            modifier = Modifier.clickable { onSignUpClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogInButton() {
    LogInButton(onClick = {})
}


@Preview(showBackground = true)
@Composable
fun PreviewTextInputField() {
    TextInputField(
        value = "",
        onValueChange = {},
        placeholderText = "placeholder"
    )
}

//@Composable
//@Preview(showBackground = true)
//fun previewLoginScreen() {
//    LoginContent(
//        state = LoginScreenState(
//
//        ),  //
//        onLoginClick = { _, _ -> },
//        onSignUpClick = {}
//    )
//}