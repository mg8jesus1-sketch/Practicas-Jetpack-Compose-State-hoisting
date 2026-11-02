package com.example.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Pregunta(
    val texto: String,
    val opciones: List<String>,
    val correcta: Int
)

enum class Pantalla { INICIO, QUIZ, RESULTADO }

val preguntas = listOf(
    Pregunta(
        "¿Qué tipo de dato guarda un número entero?",
        listOf("String", "Int", "Boolean", "Double"),
        1
    ),
    Pregunta(
        "¿Cuál estructura repite código varias veces?",
        listOf("if", "when", "for", "val"),
        2
    ),
    Pregunta(
        "¿Qué valor puede tener un Boolean?",
        listOf("true o false", "Solo números", "Solo texto", "Cualquier cosa"),
        0
    ),
    Pregunta(
        "¿Qué palabra se usa para crear una función en Kotlin?",
        listOf("def", "function", "fun", "void"),
        2
    ),
    Pregunta(
        "¿Cuál es el resultado de 7 % 2?",
        listOf("3", "1", "0", "2"),
        1
    )
)

@Composable
fun QuizApp() {
    var pantalla by remember { mutableStateOf(Pantalla.INICIO) }
    var nombre by remember { mutableStateOf("") }
    var indicePregunta by remember { mutableStateOf(0) }
    var seleccionada by remember { mutableStateOf<Int?>(null) }
    var respondida by remember { mutableStateOf(false) }
    var puntaje by remember { mutableStateOf(0) }

    when (pantalla) {
        Pantalla.INICIO -> {
            InicioScreen(
                nombre = nombre,
                onNombreChange = { nombre = it },
                onComenzar = {
                    indicePregunta = 0
                    seleccionada = null
                    respondida = false
                    puntaje = 0
                    pantalla = Pantalla.QUIZ
                }
            )
        }

        Pantalla.QUIZ -> {
            PreguntaScreen(
                pregunta = preguntas[indicePregunta],
                numero = indicePregunta + 1,
                total = preguntas.size,
                seleccionada = seleccionada,
                respondida = respondida,
                onSeleccionar = { seleccionada = it },
                onConfirmar = {
                    respondida = true
                    if (seleccionada == preguntas[indicePregunta].correcta) {
                        puntaje++
                    }
                },
                onSiguiente = {
                    if (indicePregunta < preguntas.size - 1) {
                        indicePregunta++
                        seleccionada = null
                        respondida = false
                    } else {
                        pantalla = Pantalla.RESULTADO
                    }
                }
            )
        }

        Pantalla.RESULTADO -> {
            ResultadoScreen(
                nombre = nombre,
                puntaje = puntaje,
                total = preguntas.size,
                onReintentar = {
                    indicePregunta = 0
                    seleccionada = null
                    respondida = false
                    puntaje = 0
                    pantalla = Pantalla.QUIZ
                },
                onCambiarJugador = {
                    nombre = ""
                    indicePregunta = 0
                    seleccionada = null
                    respondida = false
                    puntaje = 0
                    pantalla = Pantalla.INICIO
                }
            )
        }
    }
}

@Composable
fun InicioScreen(
    nombre: String,
    onNombreChange: (String) -> Unit,
    onComenzar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Quiz de programación", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = { Text("Tu nombre") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Button(
            onClick = onComenzar,
            enabled = nombre.trim().length >= 2,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Comenzar")
        }
    }
}

@Composable
fun PreguntaScreen(
    pregunta: Pregunta,
    numero: Int,
    total: Int,
    seleccionada: Int?,
    respondida: Boolean,
    onSeleccionar: (Int) -> Unit,
    onConfirmar: () -> Unit,
    onSiguiente: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Pregunta $numero de $total")
        LinearProgressIndicator(
            progress = numero / total.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = pregunta.texto,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        for (i in pregunta.opciones.indices) {
            var color = Color.Unspecified
            if (respondida && i == pregunta.correcta) {
                color = Color(0xFF2E7D32)
            } else if (respondida && i == seleccionada) {
                color = Color.Red
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = seleccionada == i,
                    onClick = { onSeleccionar(i) },
                    enabled = !respondida
                )
                Text(text = pregunta.opciones[i], color = color)
            }
        }

        if (!respondida) {
            Button(
                onClick = onConfirmar,
                enabled = seleccionada != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar")
            }
        } else {
            Button(
                onClick = onSiguiente,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (numero == total) "Ver resultados" else "Siguiente")
            }
        }
    }
}

@Composable
fun ResultadoScreen(
    nombre: String,
    puntaje: Int,
    total: Int,
    onReintentar: () -> Unit,
    onCambiarJugador: () -> Unit
) {
    var mensaje = "Sigue practicando"
    if (puntaje == 5) {
        mensaje = "Perfecto"
    } else if (puntaje >= 3) {
        mensaje = "Buen trabajo"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "¡Muy bien, $nombre!", style = MaterialTheme.typography.headlineMedium)
        Text(text = "$puntaje de $total", style = MaterialTheme.typography.titleLarge)
        Text(text = mensaje, modifier = Modifier.padding(vertical = 16.dp))

        Button(onClick = onReintentar) {
            Text("Reintentar")
        }
        OutlinedButton(onClick = onCambiarJugador) {
            Text("Cambiar jugador")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InicioScreenPreview() {
    InicioScreen(nombre = "Ana", onNombreChange = {}, onComenzar = {})
}

@Preview(showBackground = true)
@Composable
fun PreguntaScreenPreview() {
    PreguntaScreen(
        pregunta = preguntas[0],
        numero = 1,
        total = 5,
        seleccionada = 2,
        respondida = true,
        onSeleccionar = {},
        onConfirmar = {},
        onSiguiente = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ResultadoScreenPreview() {
    ResultadoScreen(
        nombre = "Ana",
        puntaje = 4,
        total = 5,
        onReintentar = {},
        onCambiarJugador = {}
    )
}

@Preview(showBackground = true)
@Composable
fun QuizAppPreview() {
    QuizApp()
}