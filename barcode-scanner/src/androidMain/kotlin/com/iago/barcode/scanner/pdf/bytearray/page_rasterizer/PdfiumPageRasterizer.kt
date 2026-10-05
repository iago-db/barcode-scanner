package com.iago.barcode.scanner.pdf.bytearray.page_rasterizer

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import com.iago.barcode.scanner.pdf.bytearray.PdfBackground
import com.shockwave.pdfium.PdfiumCore
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.context

import io.github.yuroyami.kitepdf.PdfPage
import io.github.yuroyami.kitepdf.core.KiteRawApi
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.io.path.createTempFile
import kotlin.io.path.name
import kotlin.io.path.writeBytes
import kotlin.math.roundToInt

class PdfiumPageRasterizer: PageRasterizer("PdfiumPageRasterizer") {
    @OptIn(KiteRawApi::class)
    override fun renderToByteArray(
        page: PdfPage,
        resolution: Double,
        background: PdfBackground
    ): ByteArray? {
        val kiteDocument = page.document
        val bytes = kiteDocument.rawBytes
        val path = createTempFile().apply { writeBytes(bytes) }
        val file = File(path.name)
        val descriptor = runErasure("ParcelFileDescriptor.open") { ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY) }

        val core = allocateErasure { PdfiumCore(FileKit.context.applicationContext) }
        val document = runErasure("PdfiumCore.newDocument") { core.newDocument(descriptor) }

        val index = kiteDocument.pages.indexOf(page)
        val scale = page.scale(resolution, 40000000.0)
        runErasure("PdfiumCore.openPage") { core.openPage(document, index) }

        val bitmap = runErasure("createBitmap") {
            val width = (runErasure("PdfiumCore.getPageWidth") { core.getPageWidth(document, index) } * scale).roundToInt()
            val height = (runErasure("PdfiumCore.getPageHeight") { core.getPageHeight(document, index) } * scale).roundToInt()

            createBitmap(width, height, Bitmap.Config.RGB_565).apply {
                runErasure("PdfiumCore.renderPageBitmap") {
                    core.renderPageBitmap(document, this, index, 0, 0, width, height)
                }
            }
        }

        runErasure("PdfiumCore.closeDocument") { core.closeDocument(document) }
        runErasure("ParcelFileDescriptor.close") { descriptor.close() }

        val stream = allocateErasure { ByteArrayOutputStream() }
        runErasure("$name.renderToByteArray(): Bitmap.compress") {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }

        return stream.toByteArray()
    }
}