package com.iago.barcode.scanner.pdf.bytearray.page_rasterizer

import android.graphics.Bitmap
import android.graphics.Color
import com.iago.barcode.scanner.pdf.bytearray.PdfBackground
import io.github.yuroyami.kitepdf.PdfPage
import io.github.yuroyami.kitepdf.nativerenderer.AndroidPdfBitmapRenderer
import java.io.ByteArrayOutputStream

actual class NativePageRasterizer: PageRasterizer("AndroidNativePageRasterizer") {
    actual override fun renderToByteArray(page: PdfPage, resolution: Double, background: PdfBackground): ByteArray? {
        val scale = allocateErasure { page.scale(resolution, 40000000.0) }
        val bitmap = runErasure("$name.renderToByteArray: renderToBitmap()") { AndroidPdfBitmapRenderer.renderToBitmap(page, scale, background.toColor()) }
        val stream = allocateErasure { ByteArrayOutputStream() }
        runErasure("$name.renderToByteArray(): Bitmap.compress") { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) }
        return allocateErasure { stream.toByteArray() }
    }

    private fun PdfBackground.toColor(): Int {
        return when (this) {
            PdfBackground.WHITE -> Color.WHITE
        }
    }
}