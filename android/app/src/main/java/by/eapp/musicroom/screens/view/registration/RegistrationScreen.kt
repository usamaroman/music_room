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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import by.eapp.musicroom.R
import by.eapp.musicroom.domain.model.RegistrationData
import by.eapp.musicroom.navigation.Screens
import by.eapp.musicroom.screens.AuthorizationViewModel
import by.eapp.musicroom.screens.LoginScreenAction
import by.eapp.musicroom.screens.LoginScreenState
import by.eapp.musicroom.screens.components.LoadingScreen
import by.eapp.musicroom.screens.components.LogInButton
import by.eapp.musicroom.screens.components.PasswordTextInputField
import by.eapp.musicroom.screens.components.TextInputField

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: AuthorizationViewModel,
) {
    val stateUi by viewModel.stateUi.collectAsState()
    when (stateUi) {
        LoginScreenState.Init -> LoadingScreen()
        LoginScreenState.Loading -> LoadingScreen()
        LoginScreenState.Success -> RegistrationScreen(navController, viewModel)
        is LoginScreenState.Error -> {
            viewModel.showToast(
                "Error: ${(stateUi as LoginScreenState.Error).error?.message}",
                LocalContext.current
            )
        }
    }
}


@Composable
fun RegistrationScreen(
    navController: NavHostController,
    viewModel: AuthorizationViewModel,
) {
    var nickname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    var password by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showRepeatPassword by rememberSaveable { mutableStateOf(false) }

    val state by viewModel.stateUi.collectAsState()

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(70.dp))
        Text(
            text = stringResource(R.string.mr_sign_up),
            fontSize = 50.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(70.dp))

        Text(
            text = stringResource(R.string.mr_nickname),
            modifier = Modifier.padding(bottom = 5.dp),
            color = Color.White
        )
        TextInputField(
            value = nickname,
            onValueChange = { nickname = it },
            placeholderText = stringResource(R.string.mr_placeholder_nickname),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Email",
            modifier = Modifier.padding(bottom = 5.dp),
            color = Color.White
        )
        TextInputField(
            value = email,
            onValueChange = { email = it },
            placeholderText = stringResource(R.string.mr_email_placeholder),
        )
        //passwords
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Password",
            modifier = Modifier.padding(bottom = 5.dp),
            color = Color.White
        )

        PasswordTextInputField(
            password = password,
            onPasswordChange = { password = it },
            showPassword = showPassword,
            onShowPasswordChange = { showPassword = it }
        )

        // Repeat password field
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.mr_repeat_password),
            modifier = Modifier.padding(bottom = 5.dp),
            color = Color.White
        )

        PasswordTextInputField(
            password = repeatPassword,
            onPasswordChange = { repeatPassword = it },
            showPassword = showRepeatPassword,
            onShowPasswordChange = { showRepeatPassword = it }
        )

        Spacer(modifier = Modifier.height(40.dp))

        LogInButton(
            enabled = viewModel.enableButton(
                nickname = nickname,
                email = email,
                password = password,
                repeatPassword = repeatPassword
            ),
            onClick = {
                if (password == repeatPassword) {
                    viewModel.dispatch(
                        LoginScreenAction.RegisterUser(
                            RegistrationData(
                                nickname = nickname,
                                email = email,
                                password = password
                            )
                        )
                    )
                } else {
                    viewModel.dispatch(
                        LoginScreenAction.ShowError(
                            context.getString(R.string.mr_passwords_dont_match),
                            context
                        )
                    )
                }

            }
        )

        Spacer(modifier = Modifier.height(40.dp))
        Text(text = stringResource(R.string.mr_already_have_an_account), color = Color.White)
        Text(
            text = stringResource(R.string.mr_sign_up),
            modifier = Modifier
                .padding(5.dp)
                .clickable {
                    navController.navigate(Screens.LoginScreen.route)
                },
            color = Color.White
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PasswordTextInputFieldPreview() {

    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    PasswordTextInputField(
        password = password,
        onPasswordChange = { password = it },
        showPassword = showPassword,
        onShowPasswordChange = { showPassword = it }
    )
}

@Preview
@Composable
fun RegistrationScreenPreview() {
    // RegistrationScreen(navController = NavHostController(context = LocalContext.current))
}
