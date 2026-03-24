package com.example.repairme.utils

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Usuario
import com.example.repairme.ui.theme.naranjaLetras
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter

import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPTable

import java.io.File
import java.io.FileOutputStream

//Esto me tiene que devolver un archivo. Neceisto un File
//PROBLEMA: Necesito compartir los archivos con el visor

fun generarPdf(
    context: Context,//Necesito acceder a la carpeta context.cacheDir, así que necesita acceso al sisema
    averia: Averia,
    cliente: Usuario,
    tecnico:Usuario
): File//Tengo que devolver el archivo
{
    val archivo = File(context.cacheDir, "presupuesto_${averia.id}.pdf")
    //Este es el objeto en sí y le defino dónde se guarda
    //Como nombre del archivo del presupuesto le pongo presupuesto más el id de la avería
    val outputStream = FileOutputStream(archivo)
    //FileOit va a ser como el canal por el que va a llegar la escritura al archivo
    val document= Document()
    PdfWriter.getInstance(document, outputStream)
    document.open()
    document.add(Paragraph("PRESUPUESTO"))
    document.add(Paragraph("Cliente: ${cliente.name} ${cliente.apellidos}"))
    document.add(Paragraph("DNI: ${cliente.dni}"))
    document.add(Paragraph("Técnico: ${tecnico.name}"))

    var tabla= PdfPTable(4)
    tabla.addCell("Concepto")
    tabla.addCell("Cantidad")
    tabla.addCell("Precio unitario")
    tabla.addCell("Total")

    averia.lineasPresupuesto.forEach {
            linea->
        tabla.addCell(linea.concepto)
        tabla.addCell("${linea.cantidad}")
        tabla.addCell("${linea.precioUnitario}")
        tabla.addCell("${linea.cantidad* linea.precioUnitario}")



        }
    document.add(tabla)
    document.close()


    return archivo //Necesitamos retornar el archivo







}