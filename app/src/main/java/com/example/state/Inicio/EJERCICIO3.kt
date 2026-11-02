package com.example.state

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Morado = Color(0xFF6750A4)
private val Gris = Color(0xFF49454F)
private val Rojo = Color(0xFFB3261E)

val CUPOS = mapOf("Pintura" to 8, "Fotografía" to 6, "Guitarra" to 4)
val TALLERES = CUPOS.keys.toList()

fun validarNombre(nombre: String): String? =
    if (nombre.trim().length < 3) "Ingresa tu nombre completo (mínimo 3 letras)" else null

fun validarCorreo(correo: String): String? {
    val arroba = correo.indexOf('@')
    val hayPunto = arroba > 0 && correo.indexOf('.', arroba) > arroba + 1
    return if (hayPunto) null else "Ingresa un correo válido"
}

fun validarEdad(edad: String): String? {
    val n = edad.toIntOrNull()
    return if (n == null || n !in 12..99) "La edad debe estar entre 12 y 99" else null
}

@Composable
fun InscripcionScreen() {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var taller by remember { mutableStateOf(TALLERES[0]) }
    var aceptaAviso by remember { mutableStateOf(false) }
    var intentoEnviar by remember { mutableStateOf(false) }
    var inscrito by remember { mutableStateOf(false) }

    val errorNombre = validarNombre(nombre)
    val errorCorreo = validarCorreo(correo)
    val errorEdad = validarEdad(edad)
    val errorAviso = if (!aceptaAviso) "Debes aceptar el aviso de privacidad" else null
    val todoValido = errorNombre == null && errorCorreo == null &&
            errorEdad == null && errorAviso == null

    if (inscrito) {
        ConfirmacionScreen(
            nombre = nombre,
            correo = correo,
            taller = taller,
            onNueva = {
                nombre = ""
                correo = ""
                edad = ""
                taller = TALLERES[0]
                aceptaAviso = false
                intentoEnviar = false
                inscrito = false
            }
        )
    } else {
        InscripcionForm(
            nombre = nombre, onNombreChange = { nombre = it },
            correo = correo, onCorreoChange = { correo = it },
            edad = edad,
            onEdadChange = { nuevo -> edad = nuevo.filter { it.isDigit() }.take(3) },
            taller = taller, onTallerChange = { taller = it },
            aceptaAviso = aceptaAviso, onAceptaAvisoChange = { aceptaAviso = it },
            errorNombre = if (intentoEnviar) errorNombre else null,
            errorCorreo = if (intentoEnviar) errorCorreo else null,
            errorEdad = if (intentoEnviar) errorEdad else null,
            errorAviso = if (intentoEnviar) errorAviso else null,
            lugares = CUPOS[taller] ?: 0,
            onEnviar = {
                intentoEnviar = true
                if (todoValido) inscrito = true
            }
        )
    }
}

@Composable
fun InscripcionForm(
    nombre: String, onNombreChange: (String) -> Unit,
    correo: String, onCorreoChange: (String) -> Unit,
    edad: String, onEdadChange: (String) -> Unit,
    taller: String, onTallerChange: (String) -> Unit,
    aceptaAviso: Boolean, onAceptaAvisoChange: (Boolean) -> Unit,
    errorNombre: String?, errorCorreo: String?, errorEdad: String?, errorAviso: String?,
    lugares: Int,
    onEnviar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Inscripción a talleres", fontSize = 28.sp)
        Text(
            "Casa del Lago · cupo limitado",
            fontSize = 16.sp,
            color = Gris,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )

        CampoTexto("Nombre completo", nombre, onNombreChange, errorNombre)
        CampoTexto("Correo", correo, onCorreoChange, errorCorreo, KeyboardType.Email)
        CampoTexto("Edad", edad, onEdadChange, errorEdad, KeyboardType.Number)

        SelectorTaller(taller = taller, onTallerChange = onTallerChange)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Checkbox(
                checked = aceptaAviso,
                onCheckedChange = onAceptaAvisoChange,
                colors = CheckboxDefaults.colors(checkedColor = Morado)
            )
            Text("Acepto el aviso de privacidad", fontSize = 16.sp)
        }
        if (errorAviso != null) {
            Text(errorAviso, color = Rojo, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
        }

        Button(
            onClick = onEnviar,
            colors = ButtonDefaults.buttonColors(containerColor = Morado),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(56.dp)
        ) { Text("Inscribirme", fontSize = 17.sp, fontWeight = FontWeight.Bold) }

        Text(
            "Quedan $lugares lugares en este taller",
            fontSize = 14.sp,
            color = Gris,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SelectorTaller(taller: String, onTallerChange: (String) -> Unit) {
    var menuAbierto by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = taller,
            onValueChange = {},
            readOnly = true,
            label = { Text("Taller") },
            trailingIcon = { Text("▼", color = Gris, fontSize = 14.sp) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { menuAbierto = true }
        )
        DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
            TALLERES.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onTallerChange(opcion)
                        menuAbierto = false
                    }
                )
            }
        }
    }
}

@Composable
fun ConfirmacionScreen(nombre: String, correo: String, taller: String, onNueva: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("¡Inscripción confirmada!", fontSize = 28.sp)
        Text("Casa del Lago · te esperamos", fontSize = 16.sp, color = Gris)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF1EDF7))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DatoResumen("Nombre", nombre)
            DatoResumen("Correo", correo)
            DatoResumen("Taller", taller)
        }

        Button(
            onClick = onNueva,
            colors = ButtonDefaults.buttonColors(containerColor = Morado),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) { Text("Nueva inscripción", fontSize = 17.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun DatoResumen(etiqueta: String, valor: String) {
    Column {
        Text(etiqueta, fontSize = 12.sp, color = Gris)
        Text(valor, fontSize = 17.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmacionPreview() {
    ConfirmacionScreen("Ana López", "ana.lopez@correo.com", "Fotografía", onNueva = {})
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun InscripcionScreenPreview() {
    InscripcionScreen()
}