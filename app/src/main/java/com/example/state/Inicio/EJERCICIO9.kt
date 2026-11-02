package com.example.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class Categoria { Comida, Transporte, Renta, Ocio, Otros }

data class Gasto(
    val id: Int,
    val concepto: String,
    val monto: Double,
    val categoria: Categoria
)

@Composable
fun PresupuestoScreen() {
    var ingresoTexto by remember { mutableStateOf("4500") }
    val gastos = remember { mutableStateListOf<Gasto>() }
    var siguienteId by remember { mutableStateOf(1) }

    val ingreso = ingresoTexto.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Presupuesto mensual", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = ingresoTexto,
            onValueChange = { ingresoTexto = it },
            label = { Text("Ingreso mensual") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        FormularioGasto(
            onAgregar = { concepto, monto, categoria ->
                gastos.add(Gasto(siguienteId, concepto, monto, categoria))
                siguienteId++
            }
        )

        ResumenPresupuesto(ingreso = ingreso, gastos = gastos)

        ListaGastos(
            gastos = gastos,
            onEliminar = { gastos.remove(it) }
        )
    }
}

@Composable
fun FormularioGasto(onAgregar: (String, Double, Categoria) -> Unit) {
    var concepto by remember { mutableStateOf("") }
    var montoTexto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(Categoria.Comida) }
    var menuAbierto by remember { mutableStateOf(false) }

    val monto = montoTexto.toDoubleOrNull()
    val valido = concepto.isNotBlank() && monto != null && monto > 0

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = concepto,
            onValueChange = { concepto = it },
            label = { Text("Concepto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = montoTexto,
            onValueChange = { montoTexto = it },
            label = { Text("Monto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Box {
            OutlinedButton(onClick = { menuAbierto = true }) {
                Text("Categoría: ${categoria.name}")
            }
            DropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false }
            ) {
                for (c in Categoria.values()) {
                    DropdownMenuItem(
                        text = { Text(c.name) },
                        onClick = {
                            categoria = c
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                onAgregar(concepto, monto!!, categoria)
                concepto = ""
                montoTexto = ""
                categoria = Categoria.Comida
            },
            enabled = valido,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar gasto")
        }
    }
}

@Composable
fun ListaGastos(gastos: List<Gasto>, onEliminar: (Gasto) -> Unit) {
    Column {
        Text(text = "Gastos", style = MaterialTheme.typography.titleMedium)
        if (gastos.isEmpty()) {
            Text(text = "Todavía no hay gastos")
        }
        for (g in gastos) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(text = "${g.concepto} (${g.categoria.name}) - $${"%.2f".format(g.monto)}")
                TextButton(onClick = { onEliminar(g) }) {
                    Text("X")
                }
            }
        }
    }
}

@Composable
fun ResumenPresupuesto(ingreso: Double, gastos: List<Gasto>) {
    val total = gastos.sumOf { it.monto }
    val saldo = ingreso - total
    val porcentaje = if (ingreso > 0) total / ingreso else 0.0
    val porCategoria = gastos.groupBy { it.categoria }.mapValues { entrada -> entrada.value.sumOf { it.monto } }
    val top = porCategoria.maxByOrNull { it.value }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "Resumen", style = MaterialTheme.typography.titleMedium)
        Text(text = "Total gastado: $${"%.2f".format(total)}")
        Text(text = "Saldo disponible: $${"%.2f".format(saldo)}")
        Text(text = "Porcentaje usado: ${"%.0f".format(porcentaje * 100)}%")
        LinearProgressIndicator(
            progress = porcentaje.toFloat().coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth()
        )
        if (top != null) {
            Text(text = "Categoría con más gasto: ${top.key.name} ($${"%.2f".format(top.value)})")
        }

        if (porcentaje > 1.0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Excediste el presupuesto por $${"%.2f".format(total - ingreso)}",
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else if (porcentaje >= 0.8) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Yellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ya usaste el 80% o más de tu presupuesto",
                    color = Color.Black,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormularioGastoPreview() {
    FormularioGasto(onAgregar = { _, _, _ -> })
}

@Preview(showBackground = true)
@Composable
fun ListaGastosPreview() {
    ListaGastos(
        gastos = listOf(
            Gasto(1, "Renta", 2000.0, Categoria.Renta),
            Gasto(2, "Tacos", 80.0, Categoria.Comida)
        ),
        onEliminar = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ResumenPresupuestoPreview() {
    ResumenPresupuesto(
        ingreso = 4500.0,
        gastos = listOf(
            Gasto(1, "Renta", 3000.0, Categoria.Renta),
            Gasto(2, "Super", 900.0, Categoria.Comida)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PresupuestoScreenPreview() {
    PresupuestoScreen()
}