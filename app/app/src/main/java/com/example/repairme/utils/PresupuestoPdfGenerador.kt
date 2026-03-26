package com.example.repairme.utils

import android.content.Context
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Usuario
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.draw.LineSeparator
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
    val document= Document(PageSize.A4, 40f, 40f, 60f, 60f)
    //Fuentes t colores

    val fuenteTitulo    = Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD,   BaseColor(230, 100, 30))
    val fuenteSeccion   = Font(Font.FontFamily.HELVETICA, 11f, Font.BOLD,   BaseColor(50, 50, 50))
    val fuenteLabel     = Font(Font.FontFamily.HELVETICA, 9f,  Font.BOLD,   BaseColor(100, 100, 100))
    val fuenteValor     = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor(30, 30, 30))
    val fuenteTabCab    = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD,   BaseColor.WHITE)
    val fuenteTabCuerpo = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor(30, 30, 30))
    val fuenteTotal     = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD,   BaseColor(230, 100, 30))
    val fechaFont       = Font(Font.FontFamily.HELVETICA, 9f,  Font.NORMAL, BaseColor(150, 150, 150))
    val notaFont        = Font(Font.FontFamily.HELVETICA, 9f,  Font.ITALIC, BaseColor(160, 160, 160))

    PdfWriter.getInstance(document, outputStream)
    document.open()
    document.add(Paragraph("\n"))

    val titulo = Paragraph("Presupuesto de Reparación", fuenteTitulo)
    titulo.spacingAfter = 4f
    document.add(titulo)

    val fecha = Paragraph("Fecha de emisión: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date())}", fechaFont)
    fecha.spacingAfter = 16f
    document.add(fecha)

    val linea = LineSeparator(1f, 100f, BaseColor(230, 100, 30), Element.ALIGN_CENTER, -2f)
    document.add(Chunk(linea))
    document.add(Paragraph("\n"))
    val tablaInfo = PdfPTable(2)
    tablaInfo.widthPercentage = 100f
    tablaInfo.setWidths(floatArrayOf(1f, 1f))
    tablaInfo.spacingAfter = 16f

    fun celdaInfoTitulo(texto: String): PdfPCell {
        val cell = PdfPCell(Phrase(texto, fuenteSeccion))
        cell.backgroundColor = BaseColor(245, 245, 245)
        cell.setPadding(8f)
        cell.border = Rectangle.NO_BORDER
        cell.borderWidthBottom = 2f
        cell.borderColorBottom = BaseColor(230, 100, 30)
        return cell
    }

    fun celdaInfoContenido(label: String, valor: String): PdfPCell {
        val p = Paragraph()
        p.add(Chunk("$label\n", fuenteLabel))
        p.add(Chunk(valor, fuenteValor))
        val cell = PdfPCell(p)
        cell.backgroundColor = BaseColor(250, 250, 250)
        cell.setPadding(8f)
        cell.border = Rectangle.NO_BORDER
        return cell
    }

    tablaInfo.addCell(celdaInfoTitulo("Datos del Cliente"))
    tablaInfo.addCell(celdaInfoTitulo("Tecnico Asignado"))
    tablaInfo.addCell(celdaInfoContenido("Nombre completo", "${cliente.name} ${cliente.apellidos}"))
    tablaInfo.addCell(celdaInfoContenido("Tecnico", tecnico.name))
    tablaInfo.addCell(celdaInfoContenido("DNI", cliente.dni))
    tablaInfo.addCell(celdaInfoContenido("Averia", averia.id.toString()))
    document.add(tablaInfo)


    val tablaLineas = PdfPTable(4)
    tablaLineas.widthPercentage = 100f
    tablaLineas.setWidths(floatArrayOf(4f, 1.5f, 2f, 2f))
    tablaLineas.spacingAfter = 8f

    fun celdaCabecera(texto: String): PdfPCell {
        val cell = PdfPCell(Phrase(texto, fuenteTabCab))
        cell.backgroundColor = BaseColor(230, 100, 30)
        cell.setPadding(8f)
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.border = Rectangle.NO_BORDER
        return cell
    }

    tablaLineas.addCell(celdaCabecera("Concepto"))
    tablaLineas.addCell(celdaCabecera("Cantidad"))
    tablaLineas.addCell(celdaCabecera("Precio unitario"))
    tablaLineas.addCell(celdaCabecera("Total"))
    var totalGeneral = 0.0

    averia.lineasPresupuesto.forEachIndexed { index, linea ->
        val bgColor = if (index % 2 == 0) BaseColor.WHITE else BaseColor(248, 248, 248)
        val totalLinea = linea.cantidad * linea.precioUnitario
        totalGeneral += totalLinea

        fun celdaFila(texto: String, alineacion: Int = Element.ALIGN_LEFT): PdfPCell {
            val cell = PdfPCell(Phrase(texto, fuenteTabCuerpo))
            cell.backgroundColor = bgColor
            cell.setPadding(7f)
            cell.horizontalAlignment = alineacion
            cell.border = Rectangle.NO_BORDER
            cell.borderWidthBottom = 0.5f
            cell.borderColorBottom = BaseColor(220, 220, 220)
            return cell
        }

        tablaLineas.addCell(celdaFila(linea.concepto))
        tablaLineas.addCell(celdaFila("${linea.cantidad}", Element.ALIGN_CENTER))
        tablaLineas.addCell(celdaFila("%.2f €".format(linea.precioUnitario), Element.ALIGN_RIGHT))
        tablaLineas.addCell(celdaFila("%.2f €".format(totalLinea), Element.ALIGN_RIGHT))
    }

    document.add(tablaLineas)
    val tablaTotal = PdfPTable(2)
    tablaTotal.widthPercentage = 40f
    tablaTotal.horizontalAlignment = Element.ALIGN_RIGHT
    tablaTotal.spacingAfter = 24f

    val iva = totalGeneral * 0.21
    val totalConIva = totalGeneral + iva

    fun filaTotalFila(label: String, valor: String, esTotal: Boolean = false): Array<PdfPCell> {
        val f = if (esTotal) fuenteTotal else fuenteValor
        val bg = if (esTotal) BaseColor(255, 240, 225) else BaseColor.WHITE
        val c1 = PdfPCell(Phrase(label, if (esTotal) fuenteTotal else fuenteLabel))
        val c2 = PdfPCell(Phrase(valor, f))
        for (c in arrayOf(c1, c2)) {
            c.backgroundColor = bg
            c.setPadding(6f)
            c.border = Rectangle.NO_BORDER
            c.borderWidthTop = if (esTotal) 2f else 0f
            c.borderColorTop = BaseColor(230, 100, 30)
        }
        c2.horizontalAlignment = Element.ALIGN_RIGHT
        return arrayOf(c1, c2)
    }

    filaTotalFila("Subtotal", "%.2f €".format(totalGeneral)).forEach { tablaTotal.addCell(it) }
    filaTotalFila("IVA (21%)", "%.2f €".format(iva)).forEach { tablaTotal.addCell(it) }
    filaTotalFila("TOTAL", "%.2f €".format(totalConIva), esTotal = true).forEach { tablaTotal.addCell(it) }

    document.add(tablaTotal)
    val nota = Paragraph(
        "Este presupuesto tiene una validez de 30 días desde la fecha de emisión. " +
                "Los precios indicados incluyen mano de obra y materiales. IVA incluido en el total.",
        notaFont
    )
    nota.alignment = Element.ALIGN_CENTER
    document.add(nota)
    document.close()








    return archivo //Necesitamos retornar el archivo







}