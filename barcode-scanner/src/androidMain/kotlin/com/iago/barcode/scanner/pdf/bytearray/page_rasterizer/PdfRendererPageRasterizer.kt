package com.iago.barcode.scanner.pdf.bytearray.page_rasterizer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import com.iago.barcode.scanner.pdf.bytearray.PdfBackground
import io.github.yuroyami.kitepdf.PdfPage
import io.github.yuroyami.kitepdf.core.KiteRawApi
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.io.path.createTempFile
import kotlin.io.path.name
import kotlin.io.path.writeBytes
import kotlin.math.roundToInt

class PdfRendererPageRasterizer: PageRasterizer("PdfRendererPageRasterizer") {
    @OptIn(KiteRawApi::class)
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun renderToByteArray(
        kitePage: PdfPage,
        resolution: Double,
        background: PdfBackground
    ): ByteArray? {
        val kiteDocument = kitePage.document
        val bytes = kiteDocument.rawBytes
        val path = createTempFile().apply { writeBytes(bytes) }
        val file = File(path.name)
        val descriptor = runErasure("ParcelFileDescriptor.open") { ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY) }
        val renderer = allocateErasure { PdfRenderer(descriptor) }

        val index = kiteDocument.pages.indexOf(kitePage)
        val page = runErasure("PdfRenderer.openPage") { renderer.openPage(index) }

        val bitmap = runErasure("createBitmap") {
            val scale = kitePage.scale(resolution, 40000000.0)

            createBitmap((page.width * scale).roundToInt(), (page.height * scale).roundToInt(), Bitmap.Config.ARGB_8888).apply {
                val canvas = allocateErasure { Canvas(this) }
                runErasure("Canvas.drawColor") { canvas.drawColor(Color.WHITE) }

                runErasure("PdfRenderer.Page.render") {
                    page.render(this, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                }

                runErasure("PdfRenderer.Page.close") {
                    page.close()
                }
            }
        }

        runErasure("PdfRenderer.close()") { renderer.close() }
        runErasure("ParcelFileDescriptor.close()") { descriptor.close() }

        val stream = allocateErasure { ByteArrayOutputStream() }
        runErasure("$name.renderToByteArray(): Bitmap.compress") {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }

        return stream.toByteArray()
    }
}