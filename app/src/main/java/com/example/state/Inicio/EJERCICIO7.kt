package com.example.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

enum class LoginEstado { INACTIVO, ERROR, BLOQUEADO, EXITO }

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var recordarme by remember { mutableStateOf(false) }
    var intentosFallidos by remember { mutableStateOf(0) }
    var estado by remember { mutableStateOf(LoginEstado.INACTIVO) }

    var mensajeError: String? = null
    if (estado == LoginEstado.ERROR) {
        mensajeError = "Correo o contraseña incorrectos (intento $intentosFallidos de 3)"
    }
    if (estado == LoginEstado.BLOQUEADO) {
        mensajeError = "Cuenta bloqueada. Intenta de nuevo más tarde"
    }

    if (estado == LoginEstado.EXITO) {
        HomeScreen(
            email = email,
            onLogout = {
                email = ""
                password = ""
                recordarme = false
                intentosFallidos = 0
                estado = LoginEstado.INACTIVO
            }
        )
    } else {
        LoginForm(
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            recordarme = recordarme,
            onRecordarmeChange = { recordarme = it },
            mensajeError = mensajeError,
            bloqueado = estado == LoginEstado.BLOQUEADO,
            onLogin = {
                if (email == "paciente@vida.mx" && password == "Vida2025") {
                    estado = LoginEstado.EXITO
                } else {
                    intentosFallidos++
                    if (intentosFallidos >= 3) {
                        estado = LoginEstado.BLOQUEADO
                    } else {
                        estado = LoginEstado.ERROR
                    }
                }
            }
        )
    }
}

@Composable
fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    recordarme: Boolean,
    onRecordarmeChange: (Boolean) -> Unit,
    mensajeError: String?,
    bloqueado: Boolean,
    onLogin: () -> Unit
) {
    var mostrarPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Clínica Vida", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Correo") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (mostrarPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(onClick = { mostrarPassword = !mostrarPassword }) {
                    Text(if (mostrarPassword) "Ocultar" else "Mostrar")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = recordarme, onCheckedChange = onRecordarmeChange)
            Text("Recordarme")
        }

        if (mensajeError != null) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = onLogin,
            enabled = !bloqueado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
    }
}

@Composable
fun HomeScreen(email: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido", style = MaterialTheme.typography.headlineMedium)
        Text(text = email, modifier = Modifier.padding(vertical = 16.dp))
        Button(onClick = onLogout) {
            Text("Cerrar sesión")
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}