package com.example.state

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Morado = Color(0xFF6750A4)
private val Lavanda = Color(0xFFE8DEF8)
private val Gris = Color(0xFF49454F)
private val Linea = Color(0xFFCAC4D0)

enum class Prioridad(val etiqueta: String, val fondo: Color, val texto: Color) {
    ALTA("Alta", Color(0xFFF9DEDC), Color(0xFF8C1D18)),
    MEDIA("Media", Color(0xFFFFECB3), Color(0xFF6B4F00)),
    BAJA("Baja", Color(0xFFD8EDD0), Color(0xFF2E6B2E))
}

enum class Filtro(val etiqueta: String) {
    TODAS("Todas"), PENDIENTES("Pendientes"), HECHAS("Hechas")
}

data class Tarea(
    val id: Int,
    val titulo: String,
    val prioridad: Prioridad,
    val hecha: Boolean = false
)

@Composable
fun TareasScreen() {
    val tareas = remember {
        mutableStateListOf(
            Tarea(1, "Entregar proyecto de Ana", Prioridad.ALTA),
            Tarea(2, "Hacer las activiidades de Ramiro", Prioridad.BAJA, hecha = true),
            Tarea(3, "Hacer una pagina web", Prioridad.MEDIA),
            Tarea(4, "Preparar exposición", Prioridad.MEDIA)
        )
    }
    var siguienteId by remember { mutableStateOf(5) }
    var textoNueva by remember { mutableStateOf("") }
    var prioridadNueva by remember { mutableStateOf(Prioridad.ALTA) }
    var filtro by remember { mutableStateOf(Filtro.TODAS) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val tareasVisibles = when (filtro) {
        Filtro.TODAS -> tareas.toList()
        Filtro.PENDIENTES -> tareas.filter { !it.hecha }
        Filtro.HECHAS -> tareas.filter { it.hecha }
    }
    val pendientes = tareas.count { !it.hecha }
    val hechas = tareas.size - pendientes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Mis pendientes", fontSize = 28.sp)

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textoNueva,
                onValueChange = { textoNueva = it },
                placeholder = { Text("Nueva tarea") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    tareas.add(Tarea(siguienteId++, textoNueva.trim(), prioridadNueva))
                    textoNueva = ""
                },
                enabled = textoNueva.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morado),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(56.dp)
            ) { Text("+", fontSize = 32.sp) }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Prioridad", fontSize = 16.sp, color = Gris, modifier = Modifier.padding(end = 4.dp))
            Prioridad.entries.forEach { p ->
                OpcionChip(p.etiqueta, prioridadNueva == p) { prioridadNueva = p }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Filtro.entries.forEach { f ->
                OpcionChip(f.etiqueta, filtro == f) { filtro = f }
            }
        }

        HorizontalDivider(color = Linea)

        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
            items(tareasVisibles, key = { it.id }) { tarea ->
                TareaItem(
                    tarea = tarea,
                    onCheckedChange = { nuevo ->
                        val index = tareas.indexOfFirst { it.id == tarea.id }
                        if (index >= 0) tareas[index] = tareas[index].copy(hecha = nuevo)
                    },
                    onDelete = { tareas.removeAll { it.id == tarea.id } }
                )
                HorizontalDivider(color = Linea)
            }
        }

        if (tareasVisibles.isEmpty()) {
            Text("No hay tareas en esta lista", fontSize = 16.sp, color = Gris)
        }

        Text("$pendientes pendientes de ${tareas.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { mostrarDialogo = true },
            enabled = hechas > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) { Text("Borrar completadas", color = if (hechas > 0) Morado else Gris, fontWeight = FontWeight.Bold) }
    }

    if (mostrarDialogo) {
        ConfirmarBorradoDialog(
            cantidad = hechas,
            onConfirm = {
                tareas.removeAll { it.hecha }
                mostrarDialogo = false
            },
            onDismiss = { mostrarDialogo = false }
        )
    }
}

@Composable
fun OpcionChip(texto: String, seleccionada: Boolean, onClick: () -> Unit) {
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
fun TareaItem(tarea: Tarea, onCheckedChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = tarea.hecha,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Morado)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = tarea.titulo,
                fontSize = 20.sp,
                color = if (tarea.hecha) Gris else Color(0xFF1C1B1F),
                textDecoration = if (tarea.hecha) TextDecoration.LineThrough else TextDecoration.None
            )
            EtiquetaPrioridad(tarea.prioridad)
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            Text("✕", fontSize = 20.sp, color = Gris)
        }
    }
}

@Composable
fun EtiquetaPrioridad(prioridad: Prioridad) {
    Text(
        text = prioridad.etiqueta,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = prioridad.texto,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(prioridad.fondo)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
fun ConfirmarBorradoDialog(cantidad: Int, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Borrar completadas") },
        text = {
            Text(if (cantidad == 1) "Se eliminará 1 tarea hecha. ¿Continuar?" else "Se eliminarán $cantidad tareas hechas. ¿Continuar?")
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Borrar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Preview(showBackground = true)
@Composable
fun TareaItemPreview() {
    TareaItem(Tarea(1, "Hacer las activiidades de Ramiro", Prioridad.BAJA, hecha = true), {}, {})
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun TareasScreenPreview() {
    TareasScreen()
}