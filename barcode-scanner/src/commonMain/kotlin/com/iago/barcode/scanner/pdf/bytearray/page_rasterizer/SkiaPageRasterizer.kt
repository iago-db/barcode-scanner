package com.iago.barcode.scanner.pdf.bytearray.page_rasterizer

import com.iago.barcode.scanner.pdf.bytearray.PdfBackground
import io.github.yuroyami.kitepdf.PdfPage
import io.github.yuroyami.kitepdf.skia.PdfPageRasterizer
import org.jetbrains.skia.Color

class SkiaPageRasterizer: PageRasterizer("SkiaPageRasterizer") {
    override fun renderToByteArray(page: PdfPage, resolution: Double, background: PdfBackground): ByteArray {
        val scale = page.scale(resolution, 40000000.0)

        val bytes = runErasure("$name.renderToByteArray(): PdfPageRasterizer.encodeToPng") {
            PdfPageRasterizer.encodeToPng(page, scale, background.toColor())
        }

        return bytes
    }

    private fun PdfBackground.toColor(): Int {
        return when(this) {
            PdfBackground.WHITE -> Color.WHITE
        }
    }
}