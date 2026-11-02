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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Morado = Color(0xFF6750A4)
private val Verde = Color(0xFF2E7D32)
private val Gris = Color(0xFF49454F)
private val Linea = Color(0xFFCAC4D0)

data class Producto(val nombre: String, val precio: Double)

fun dinero(valor: Double): String = "\$%.2f".format(valor)

@Composable
fun CarritoScreen() {
    val catalogo = remember {
        listOf(
            Producto("Tortillas 1 kg", 24.00),
            Producto("Leche 1 L", 27.50),
            Producto("Huevo 12 pz", 52.00),
            Producto("Frijol 1 kg", 38.00)
        )
    }
    val cantidades = remember { mutableStateListOf(0, 0, 0, 0) }

    val articulos = cantidades.sum()
    val subtotal = catalogo.indices.sumOf { catalogo[it].precio * cantidades[it] }
    val descuento = if (subtotal >= 200) subtotal * 0.10 else 0.0
    val subtotalConDescuento = subtotal - descuento
    val envio = when {
        articulos == 0 -> 0.0
        subtotalConDescuento >= 150 -> 0.0
        else -> 25.0
    }
    val total = subtotalConDescuento + envio

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tienda Don Pepe", fontSize = 28.sp, modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Morado),
                contentAlignment = Alignment.Center
            ) {
                Text("$articulos", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        Column(modifier = Modifier.padding(top = 12.dp)) {
            catalogo.forEachIndexed { i, producto ->
                ProductoItem(
                    producto = producto,
                    cantidad = cantidades[i],
                    onCantidadChange = { nueva -> cantidades[i] = nueva }
                )
                HorizontalDivider(color = Linea)
            }
        }

        ResumenCarrito(
            articulos = articulos,
            subtotal = subtotal,
            descuento = descuento,
            envio = envio,
            total = total
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { for (i in cantidades.indices) cantidades[i] = 0 },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) { Text("Vaciar", color = Morado, fontWeight = FontWeight.Bold) }

            Button(
                onClick = { },
                enabled = articulos > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Morado),
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
            ) { Text("Confirmar pedido", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun ProductoItem(producto: Producto, cantidad: Int, onCantidadChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(producto.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("${dinero(producto.precio)} c/u", fontSize = 16.sp, color = Gris)
        }
        BotonCircular("−", enabled = cantidad > 0) {
            onCantidadChange((cantidad - 1).coerceAtLeast(0))
        }
        Text(
            "$cantidad",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(36.dp)
        )
        BotonCircular("+", enabled = cantidad < 10) {
            onCantidadChange((cantidad + 1).coerceAtMost(10))
        }
    }
}

@Composable
fun BotonCircular(texto: String, enabled: Boolean, onClick: () -> Unit) {
    val color = if (enabled) Color(0xFF1C1B1F) else Color(0xFFB0AAB4)
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .border(1.dp, if (enabled) Gris else Linea, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, fontSize = 22.sp, color = color)
    }
}

@Composable
fun ResumenCarrito(articulos: Int, subtotal: Double, descuento: Double, envio: Double, total: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF1ECF4))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FilaResumen("Artículos", "$articulos")
        FilaResumen("Subtotal", dinero(subtotal))
        if (descuento > 0) {
            FilaResumen("Descuento 10 %", "−${dinero(descuento)}", Verde)
        } else {
            FilaResumen("Descuento", dinero(0.0))
        }
        when {
            articulos == 0 -> FilaResumen("Envío", dinero(0.0))
            envio == 0.0 -> FilaResumen("Envío", "GRATIS", Verde)
            else -> FilaResumen("Envío", dinero(envio))
        }

        HorizontalDivider(color = Linea, modifier = Modifier.padding(vertical = 4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("TOTAL", fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(dinero(total), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Morado)
        }
    }
}

@Composable
fun FilaResumen(etiqueta: String, valor: String, colorValor: Color = Color(0xFF1C1B1F)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, fontSize = 17.sp, color = Gris, modifier = Modifier.weight(1f))
        Text(valor, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = colorValor)
    }
}

@Preview(showBackground = true)
@Composable
fun ResumenCarritoPreview() {
    ResumenCarrito(articulos = 6, subtotal = 231.50, descuento = 23.15, envio = 0.0, total = 208.35)
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun CarritoScreenPreview() {
    CarritoScreen()
}