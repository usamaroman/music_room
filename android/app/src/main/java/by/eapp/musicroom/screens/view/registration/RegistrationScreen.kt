import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import by.eapp.musicroom.domain.model.RegistrationData
import by.eapp.musicroom.navigation.Screens
import by.eapp.musicroom.screens.AuthorizationViewModel
import by.eapp.musicroom.screens.LoginScreenAction
import by.eapp.musicroom.screens.LoginScreenState
import by.eapp.musicroom.screens.components.LogInButton
import by.eapp.musicroom.screens.components.PasswordTextInputField
import by.eapp.musicroom.screens.components.TextInputField

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: AuthorizationViewModel,
) {
    val stateUi by viewModel.stateUi.collectAsState()


    LaunchedEffect(stateUi) {
        when (stateUi) {
            LoginScreenState.SubmitStart -> {
                navController.navigate(Screens.SubmitCode.route) {
                    popUpTo(Screens.LoadingScreen.route) { inclusive = true }
                }
            }

            LoginScreenState.SubmitComplete -> {
                navController.navigate(Screens.RegistrationScreen.route) {
                    popUpTo(Screens.LoadingScreen.route) { inclusive = true }
                }
            }

            LoginScreenState.Loading -> {
                navController.navigate(Screens.LoadingScreen.route) {
                    launchSingleTop = true
                }
            }

            LoginScreenState.Success -> {
                navController.navigate(Screens.RegistrationScreen.route) {
                    popUpTo(Screens.LoadingScreen.route) { inclusive = true }
                }
            }

            is LoginScreenState.Error -> {
//                viewModel.showToast(
//                    "Error: ${(stateUi as LoginScreenState.Error).error?.message}",
//                    LocalContext.current
//                )
            }
        }
    }
}


@Composable
fun RegistrationScreen(
    navController: NavHostController,
    viewModel: AuthorizationViewModel,
) {
    val state by viewModel.stateUi.collectAsState()

    RegistrationContent(
        onRegisterClick = { nickname, email, password ->
            viewModel.dispatch(
                LoginScreenAction.RegisterUser(
                    RegistrationData(
                        nickname = nickname,
                        email = email,
                        password = password
                    )
                )
            )
        },
        onLoginClick = { navController.navigate(Screens.LoginScreen.route) }
    )
}

@Composable
fun RegistrationContent(
    onRegisterClick: (String, String, String) -> Unit,
    onLoginClick: () -> Unit
) {
    var nickname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(70.dp))
        RegistrationHeader()
        Spacer(modifier = Modifier.height(70.dp))
        NicknameField(nickname = nickname, onNicknameChange = { nickname = it })
        Spacer(modifier = Modifier.height(20.dp))
        EmailField(email = email, onEmailChange = { email = it })
        Spacer(modifier = Modifier.height(20.dp))
        PasswordField(
            password = password,
            onPasswordChange = { password = it },
            showPassword = showPassword,
            onShowPasswordChange = { showPassword = it }
        )
        Spacer(modifier = Modifier.height(40.dp))
        LogInButton(onClick = { onRegisterClick(nickname, email, password) })
        Spacer(modifier = Modifier.height(40.dp))
        AlreadyHaveAccount(onLoginClick)
    }
}

@Composable
fun RegistrationHeader() {
    Text(
        text = stringResource(R.string.mr_sign_up),
        fontSize = 50.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

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

@Composable
fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit
) {
    Text(
        text = stringResource(R.string.mr_email),
        modifier = Modifier.padding(bottom = 5.dp),
        color = Color.White
    )
    TextInputField(
        value = email,
        onValueChange = onEmailChange,
        placeholderText = stringResource(R.string.mr_email_placeholder),
    )
}

@Composable
fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit
) {
    Text(
        text = stringResource(R.string.mr_password),
        modifier = Modifier.padding(bottom = 5.dp),
        color = Color.White
    )
    PasswordTextInputField(
        password = password,
        onPasswordChange = onPasswordChange,
        showPassword = showPassword,
        onShowPasswordChange = onShowPasswordChange
    )
}

@Composable
fun AlreadyHaveAccount(onLoginClick: () -> Unit) {
    Text(text = stringResource(R.string.mr_already_have_an_account), color = Color.White)
    Text(
        text = stringResource(R.string.mr_sign_up),
        modifier = Modifier
            .padding(5.dp)
            .clickable { onLoginClick() },
        color = Color.White
    )
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


@Composable
@Preview(showBackground = true)
fun RegistrationScreenPreview() {
    RegistrationContent(
        onRegisterClick = { _, _, _ -> }, onLoginClick = {}
    )
}
