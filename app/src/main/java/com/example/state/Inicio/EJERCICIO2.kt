package com.example.state

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Morado = Color(0xFF6750A4)
private val Lavanda = Color(0xFFE8DEF8)
private val Gris = Color(0xFF49454F)
private val Contorno = Color(0xFFCAC4D0)

enum class Interlineado(val factor: Float, val etiqueta: String) {
    COMPACTO(1.0f, "Compacto"),
    NORMAL(1.3f, "Normal"),
    AMPLIO(1.6f, "Amplio")
}

@Composable
fun LecturaScreen() {
    var tamano by remember { mutableStateOf(18f) }
    var interlineado by remember { mutableStateOf(Interlineado.NORMAL) }
    var modoOscuro by remember { mutableStateOf(false) }
    var usaSerif by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
            }
            Text("Ajustes de lectura", fontSize = 24.sp, modifier = Modifier.padding(start = 8.dp))
        }

        ControlesLectura(
            tamano = tamano, onTamanoChange = { tamano = it },
            interlineado = interlineado, onInterlineadoChange = { interlineado = it },
            modoOscuro = modoOscuro, onModoOscuroChange = { modoOscuro = it },
            usaSerif = usaSerif, onUsaSerifChange = { usaSerif = it }
        )

        HorizontalDivider(color = Contorno)

        Text("VISTA PREVIA", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Gris)

        VistaPrevia(
            texto = "Era una noche tranquila en el pueblo cuando las campanas comenzaron a sonar, y nadie supo por qué.",
            tamano = tamano,
            interlineado = interlineado,
            modoOscuro = modoOscuro,
            usaSerif = usaSerif
        )

        OutlinedButton(
            onClick = {
                tamano = 18f
                interlineado = Interlineado.NORMAL
                modoOscuro = false
                usaSerif = false
            },
            border = BorderStroke(1.dp, Morado),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) { Text("Restablecer", color = Morado, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun ControlesLectura(
    tamano: Float, onTamanoChange: (Float) -> Unit,
    interlineado: Interlineado, onInterlineadoChange: (Interlineado) -> Unit,
    modoOscuro: Boolean, onModoOscuroChange: (Boolean) -> Unit,
    usaSerif: Boolean, onUsaSerifChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tamaño de letra", fontSize = 17.sp, modifier = Modifier.weight(1f))
            Text("${tamano.toInt()} sp", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Morado)
        }
        Column {
            Slider(
                value = tamano,
                onValueChange = onTamanoChange,
                valueRange = 14f..32f,
                colors = SliderDefaults.colors(
                    thumbColor = Morado,
                    activeTrackColor = Morado,
                    inactiveTrackColor = Color(0xFFE7E0EC)
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("A", fontSize = 13.sp, color = Gris)
                Text("A", fontSize = 26.sp, color = Gris)
            }
        }

        Text("Interlineado", fontSize = 17.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Interlineado.entries.forEach { opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = opcion == interlineado,
                        onClick = { onInterlineadoChange(opcion) },
                        colors = RadioButtonDefaults.colors(selectedColor = Morado)
                    )
                    Text(opcion.etiqueta, fontSize = 15.sp)
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Modo oscuro", fontSize = 17.sp, modifier = Modifier.weight(1f))
            Switch(
                checked = modoOscuro,
                onCheckedChange = onModoOscuroChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Morado,
                    checkedThumbColor = Color.White
                )
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Fuente", fontSize = 17.sp, modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OpcionFuente("Serif", seleccionada = usaSerif, onClick = { onUsaSerifChange(true) })
                OpcionFuente("Sans", seleccionada = !usaSerif, onClick = { onUsaSerifChange(false) })
            }
        }
    }
}

@Composable
fun OpcionFuente(texto: String, seleccionada: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionada,
        onClick = onClick,
        label = { Text(texto, fontSize = 15.sp) },
        leadingIcon = if (seleccionada) {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Lavanda)
    )
}

@Composable
fun VistaPrevia(
    texto: String,
    tamano: Float,
    interlineado: Interlineado,
    modoOscuro: Boolean,
    usaSerif: Boolean
) {
    val fondo = if (modoOscuro) Color(0xFF1C1B1F) else Color.White
    val colorTexto = if (modoOscuro) Color(0xFFE6E1E5) else Color(0xFF1C1B1F)
    val forma = RoundedCornerShape(24.dp)

    Text(
        text = texto,
        color = colorTexto,
        fontSize = tamano.sp,
        lineHeight = (tamano * interlineado.factor).sp,
        fontFamily = if (usaSerif) FontFamily.Serif else FontFamily.SansSerif,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp)
            .clip(forma)
            .background(fondo)
            .border(1.dp, if (modoOscuro) fondo else Contorno, forma)
            .padding(24.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun VistaPreviaPreview() {
    VistaPrevia(
        texto = "Era una noche tranquila en el pueblo cuando las campanas comenzaron a sonar.",
        tamano = 22f,
        interlineado = Interlineado.NORMAL,
        modoOscuro = true,
        usaSerif = true
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun LecturaScreenPreview() {
    LecturaScreen()
}