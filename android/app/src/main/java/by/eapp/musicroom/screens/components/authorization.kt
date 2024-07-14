package by.eapp.musicroom.screens.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        keyboardOptions = KeyboardOptions.Default,
        textStyle = TextStyle(color = Color.White)
    )
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
            text = "Sign In",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
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

