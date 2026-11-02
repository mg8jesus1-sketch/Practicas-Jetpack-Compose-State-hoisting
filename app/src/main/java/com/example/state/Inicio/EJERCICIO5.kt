package com.example.state

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.ceil

private val Morado = Color(0xFF6750A4)
private val Lavanda = Color(0xFFE8DEF8)
private val Gris = Color(0xFF49454F)
private val Linea = Color(0xFFCAC4D0)

fun dinero(valor: Double): String = String.format(Locale.US, "\$%,.2f", valor)

@Composable
fun CuentaScreen() {
    var montoTexto by remember { mutableStateOf("") }
    var propinaPct by remember { mutableStateOf(10) }
    var usaOtro by remember { mutableStateOf(false) }
    var personas by remember { mutableStateOf(2) }
    var redondear by remember { mutableStateOf(false) }

    val montoValido = montoTexto.toDoubleOrNull()
    val monto = montoValido ?: 0.0
    val propina = monto * propinaPct / 100.0
    val total = monto + propina
    val porPersonaExacto = total / personas
    val porPersona = if (redondear) ceil(porPersonaExacto - 0.000001) else porPersonaExacto
    val sobrante: Double? = if (redondear) porPersona * personas - total else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Cuenta dividida", fontSize = 28.sp)

        OutlinedTextField(
            value = montoTexto,
            onValueChange = { montoTexto = it },
            label = { Text("Monto de la cuenta") },
            prefix = { Text("\$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = montoTexto.isNotEmpty() && montoValido == null,
            supportingText = {
                if (montoValido == null) {
                    Text(if (montoTexto.isEmpty()) "Sin monto: se usa \$0.00" else "Monto no válido: se usa \$0.00")
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        SelectorPropina(
            porcentaje = propinaPct,
            usaOtro = usaOtro,
            onPorcentajeChange = { propinaPct = it },
            onOtroChange = { usaOtro = it }
        )

        StepperPersonas(personas = personas, onChange = { personas = it })

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Redondear por persona", fontSize = 18.sp, modifier = Modifier.weight(1f))
            Switch(
                checked = redondear,
                onCheckedChange = { redondear = it },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Morado,
                    checkedThumbColor = Color.White
                )
            )
        }

        HorizontalDivider(color = Linea)

        ResultadoCuenta(
            propinaPct = propinaPct,
            propina = propina,
            total = total,
            porPersona = porPersona,
            personas = personas,
            sobrante = sobrante
        )
    }
}

@Composable
fun SelectorPropina(
    porcentaje: Int,
    usaOtro: Boolean,
    onPorcentajeChange: (Int) -> Unit,
    onOtroChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Propina", fontSize = 20.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(10, 15, 20).forEach { pct ->
                OpcionPropina(
                    texto = "$pct %",
                    seleccionada = !usaOtro && porcentaje == pct,
                    onClick = {
                        onOtroChange(false)
                        onPorcentajeChange(pct)
                    }
                )
            }
            OpcionPropina(
                texto = "Otro",
                seleccionada = usaOtro,
                onClick = { onOtroChange(true) }
            )
        }
        if (usaOtro) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Otro porcentaje", fontSize = 16.sp, color = Gris, modifier = Modifier.weight(1f))
                Text("$porcentaje %", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Morado)
            }
            Slider(
                value = porcentaje.toFloat(),
                onValueChange = { onPorcentajeChange(it.toInt()) },
                valueRange = 0f..30f,
                colors = SliderDefaults.colors(
                    thumbColor = Morado,
                    activeTrackColor = Morado,
                    inactiveTrackColor = Color(0xFFE7E0EC)
                )
            )
        }
    }
}

@Composable
fun OpcionPropina(texto: String, seleccionada: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionada,
        onClick = onClick,
        label = { Text(texto, fontSize = 16.sp) },
        leadingIcon = if (seleccionada) {
            { Text("✓") }
        } else null,
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Lavanda)
    )
}

@Composable
fun StepperPersonas(personas: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Personas", fontSize = 20.sp, modifier = Modifier.weight(1f))
        BotonCircular("−", enabled = personas > 1) { onChange(personas - 1) }
        Text(
            "$personas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(40.dp)
        )
        BotonCircular("+", enabled = personas < 20) { onChange(personas + 1) }
    }
}

@Composable
fun BotonCircular(texto: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .border(1.dp, if (enabled) Gris else Linea, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, fontSize = 22.sp, color = if (enabled) Color(0xFF1C1B1F) else Color(0xFFB0AAB4))
    }
}

@Composable
fun ResultadoCuenta(
    propinaPct: Int,
    propina: Double,
    total: Double,
    porPersona: Double,
    personas: Int,
    sobrante: Double?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF1ECF4))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilaResultado("Propina ($propinaPct %)", dinero(propina))
        FilaResultado("Total con propina", dinero(total))

        HorizontalDivider(color = Linea, modifier = Modifier.padding(vertical = 8.dp))

        Text("Cada quien paga", fontSize = 18.sp, color = Gris)
        Text(dinero(porPersona), fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Morado)
        Text(
            if (personas == 1) "entre 1 persona" else "entre $personas personas",
            fontSize = 18.sp,
            color = Gris
        )
        if (sobrante != null) {
            Text("Sobrante por redondeo: ${dinero(sobrante)}", fontSize = 14.sp, color = Gris)
        }
    }
}

@Composable
fun FilaResultado(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(etiqueta, fontSize = 18.sp, color = Gris, modifier = Modifier.weight(1f))
        Text(valor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun ResultadoCuentaPreview() {
    ResultadoCuenta(
        propinaPct = 20,
        propina = 250.0,
        total = 1500.0,
        porPersona = 300.0,
        personas = 5,
        sobrante = 0.0
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun CuentaScreenPreview() {
    CuentaScreen()
}