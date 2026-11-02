package com.example.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            TextButton(onClick = { onRatingChange(i) }) {
                Text(text = if (i <= rating) "★" else "☆")
            }
        }
    }
}

@Composable
fun FilaCalificacion(
    titulo: String,
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    val descripciones = listOf("Malo", "Regular", "Bueno", "Muy bueno", "Excelente")

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = titulo)
        RatingBar(rating = rating, onRatingChange = onRatingChange)
        if (rating == 0) {
            Text(text = "Sin calificar")
        } else {
            Text(text = descripciones[rating - 1])
        }
    }
}

@Composable
fun ComentarioField(
    texto: String,
    onChange: (String) -> Unit,
    maximo: Int = 200
) {
    Column {
        OutlinedTextField(
            value = texto,
            onValueChange = {
                if (it.length <= maximo) {
                    onChange(it)
                }
            },
            label = { Text("Comentario") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${texto.length}/$maximo",
            color = if (texto.length >= 180) Color.Red else Color.Unspecified
        )
    }
}

@Composable
fun ResumenResena(
    general: Double,
    comentario: String,
    etiquetas: Set<String>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Reseña publicada", style = MaterialTheme.typography.titleMedium)
            Text(text = "Calificación general: ${"%.1f".format(general)}")
            Text(text = "Etiquetas: ${etiquetas.joinToString(", ")}")
            Text(text = comentario)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResenaScreen() {
    var comida by remember { mutableStateOf(0) }
    var servicio by remember { mutableStateOf(0) }
    var ambiente by remember { mutableStateOf(0) }
    var comentario by remember { mutableStateOf("") }
    var etiquetas by remember { mutableStateOf(setOf<String>()) }
    var publicada by remember { mutableStateOf(false) }

    val listaEtiquetas = listOf("Familiar", "Romántico", "Económico", "Vegetariano", "Rápido")
    val tieneTodas = comida > 0 && servicio > 0 && ambiente > 0
    val general = (comida + servicio + ambiente) / 3.0
    val puedePublicar = tieneTodas && comentario.length >= 20

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Reseña del restaurante", style = MaterialTheme.typography.headlineSmall)

        if (publicada) {
            ResumenResena(general = general, comentario = comentario, etiquetas = etiquetas)
            Button(
                onClick = {
                    comida = 0
                    servicio = 0
                    ambiente = 0
                    comentario = ""
                    etiquetas = setOf()
                    publicada = false
                }
            ) {
                Text("Nueva reseña")
            }
        } else {
            FilaCalificacion(titulo = "Comida", rating = comida, onRatingChange = { comida = it })
            FilaCalificacion(titulo = "Servicio", rating = servicio, onRatingChange = { servicio = it })
            FilaCalificacion(titulo = "Ambiente", rating = ambiente, onRatingChange = { ambiente = it })

            if (tieneTodas) {
                Text(text = "Calificación general: ${"%.1f".format(general)}")
            }

            ComentarioField(texto = comentario, onChange = { comentario = it })

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (e in listaEtiquetas.take(3)) {
                    FilterChip(
                        selected = e in etiquetas,
                        onClick = {
                            etiquetas = if (e in etiquetas) etiquetas - e else etiquetas + e
                        },
                        label = { Text(e) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (e in listaEtiquetas.drop(3)) {
                    FilterChip(
                        selected = e in etiquetas,
                        onClick = {
                            etiquetas = if (e in etiquetas) etiquetas - e else etiquetas + e
                        },
                        label = { Text(e) }
                    )
                }
            }

            Button(
                onClick = { publicada = true },
                enabled = puedePublicar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBarPreview() {
    RatingBar(rating = 3, onRatingChange = {})
}

@Preview(showBackground = true)
@Composable
fun ComentarioFieldPreview() {
    ComentarioField(texto = "Muy buena comida", onChange = {})
}

@Preview(showBackground = true)
@Composable
fun ResumenResenaPreview() {
    ResumenResena(
        general = 4.3,
        comentario = "Muy buena comida y buen servicio",
        etiquetas = setOf("Familiar", "Rápido")
    )
}

@Preview(showBackground = true)
@Composable
fun ResenaScreenPreview() {
    ResenaScreen()
}