package com.iago.barcode.scanner.pdf.bytearray.page_rasterizer

import com.iago.barcode.scanner.pdf.bytearray.PdfBackground
import io.github.yuroyami.kitepdf.PdfPage

expect class NativePageRasterizer: PageRasterizer {
    override fun renderToByteArray(
        page: PdfPage,
        resolution: Double,
        background: PdfBackground
    ): ByteArray?
}