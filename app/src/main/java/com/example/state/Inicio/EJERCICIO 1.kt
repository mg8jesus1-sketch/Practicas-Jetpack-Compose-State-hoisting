package com.example.state

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val AFORO_MAX = 50

private val Morado = Color(0xFF6750A4)
private val Verde = Color(0xFF2E7D32)
private val Amarillo = Color(0xFFF9A825)
private val Rojo = Color(0xFFC62828)
private val Gris = Color(0xFF49454F)

@Composable
fun AforoScreen() {
    var actuales by remember { mutableStateOf(0) }
    var visitasDelDia by remember { mutableStateOf(0) }

    val llena = actuales >= AFORO_MAX
    val vacia = actuales <= 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Biblioteca Central", fontSize = 26.sp)
            Text("Sala de estudio · capacidad $AFORO_MAX", fontSize = 13.sp, color = Gris)
        }

        AforoIndicator(actuales = actuales, maximo = AFORO_MAX)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { actuales-- },
                enabled = !vacia,
                border = BorderStroke(1.dp, if (vacia) Color.LightGray else Morado),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Morado),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) { Text("−  Sale", fontWeight = FontWeight.Bold) }

            Button(
                onClick = {
                    actuales++
                    visitasDelDia++
                },
                enabled = !llena,
                colors = ButtonDefaults.buttonColors(containerColor = Morado),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) { Text("+  Entra", fontWeight = FontWeight.Bold) }
        }

        VisitasCard(visitas = visitasDelDia)

        LeyendaAforo()

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                actuales = 0
                visitasDelDia = 0
            },
            border = BorderStroke(1.dp, Rojo),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Rojo),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) { Text("Cerrar día", fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun AforoIndicator(actuales: Int, maximo: Int) {
    val porcentaje = actuales / maximo.toFloat()

    val color = when {
        porcentaje >= 1f -> Rojo
        porcentaje >= 0.7f -> Amarillo
        else -> Verde
    }
    val estado = when {
        porcentaje >= 1f -> "Aforo completo"
        porcentaje >= 0.7f -> "Aforo casi lleno"
        else -> "Aforo disponible"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("$actuales / $maximo", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = color)
        Text("personas en la sala", fontSize = 14.sp, color = Gris)
        LinearProgressIndicator(
            progress = { porcentaje },
            color = color,
            trackColor = Color(0x1F000000),
            gapSize = 0.dp,
            drawStopIndicator = {},
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
        )
        Text(
            "${(porcentaje * 100).toInt()} % de ocupación",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(estado, fontSize = 12.sp, color = Gris)
    }
}

@Composable
fun VisitasCard(visitas: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF1EDF7))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Visitas del día", fontSize = 15.sp)
            Text("Solo suma las entradas", fontSize = 12.sp, color = Gris)
        }
        Text("$visitas", fontSize = 36.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LeyendaAforo() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemLeyenda(Verde, "Verde < 70 %")
        ItemLeyenda(Amarillo, "Amarillo 70–99 %")
        ItemLeyenda(Rojo, "Rojo 100 %")
    }
}

@Composable
fun ItemLeyenda(color: Color, texto: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(texto, fontSize = 11.sp, color = Gris)
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
fun AforoScreenPreview() {
    AforoScreen()
}