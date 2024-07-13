import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Visibility
import by.eapp.musicroom.screens.login.LogInButton
import by.eapp.musicroom.screens.login.TextInputField

@Composable
fun RegistrationScreen() {
    var nickname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showRepeatPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(70.dp))
        Text(
            text = "Sign Up",
            fontSize = 50.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(70.dp))

        Text(
            text = "Nickname",
            modifier = Modifier.padding(bottom = 5.dp),
            color = Color.White
        )
        TextInputField(
            value = nickname,
            onValueChange = { nickname = it },
            placeholderText = "nastix123",
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Email",
            modifier = Modifier.padding(bottom = 5.dp),
            color = Color.White
        )
        TextInputField(
            value = email,
            onValueChange = { email = it },
            placeholderText = "anastasizzzs10@gmail.com"
        )
        //passwords
        Spacer(modifier = Modifier.height(40.dp))
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
            text = "Repeat password",
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
            enabled = true,
            onClick = {}
        )
    }
}

@Composable
fun PasswordTextInputField(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "Type password here",
    showPasswordIcon: ImageVector = Icons.Filled.Visibility,
    hidePasswordIcon: ImageVector = Icons.Filled.VisibilityOff,
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        shape = RoundedCornerShape(percent = 20),
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { onShowPasswordChange(!showPassword) }) {
                Icon(
                    imageVector = if (showPassword) showPasswordIcon else hidePasswordIcon,
                    contentDescription = if (showPassword) "Hide password" else "Show password"
                )
            }
        }
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

@Preview
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen()
}
