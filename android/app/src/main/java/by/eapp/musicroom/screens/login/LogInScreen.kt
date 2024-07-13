package by.eapp.musicroom.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogInScreen() {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(70.dp))
        Text(
            text = "Sign In",
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
            placeholderText = "anastasizzzs10@gmail.com"
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Password",
            modifier = Modifier.padding(bottom = 5.dp),
            color = Color.White
        )
        TextInputField(
            value = email,
            onValueChange = { email = it },
            placeholderText = "***********"
        )
        Spacer(modifier = Modifier.height(40.dp))
        LogInButton(
            enabled = true,
            onClick = {}
        )

    }
}

@Composable
fun LogInButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    onClick:() -> Unit,
) {
    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(20),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = if (enabled) Color.White else Color.LightGray,
            contentColor = Color.Black
        ),
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Register",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .height(50.dp),
        placeholder = {
            Text(
                text = placeholderText,
            )
        },
        shape = RoundedCornerShape(percent = 20),

        maxLines = 1,
        visualTransformation = VisualTransformation.None,
        keyboardOptions = KeyboardOptions.Default
    )
}


@Preview
@Composable
fun PreviewLogInScreen() {
    LogInScreen()
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