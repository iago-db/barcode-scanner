package com.iago.barcode.scanner.pdf.bytearray.page_rasterizer

import com.iago.barcode.scanner.pdf.bytearray.PdfBackground
import com.iago.app.profiler.profiling.Profiler
import io.github.yuroyami.kitepdf.PdfPage
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

abstract class PageRasterizer(name: String): Profiler(name) {
    abstract fun renderToByteArray(page: PdfPage, resolution: Double, background: PdfBackground = PdfBackground.WHITE): ByteArray?

    protected fun PdfPage.scale(resolution: Double, maxValue: Double = Double.MAX_VALUE): Double {
        return min(resolution, sqrt(maxValue)) / max(width, height)
    }
}