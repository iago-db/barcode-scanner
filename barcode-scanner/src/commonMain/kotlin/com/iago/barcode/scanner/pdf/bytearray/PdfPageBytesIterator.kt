package com.iago.barcode.scanner.pdf.bytearray

import com.iago.barcode.scanner.pdf.bytearray.page_rasterizer.PageRasterizer
import com.iago.app.profiler.profiling.Profiler
import io.github.yuroyami.kitepdf.PdfDocument

class PdfPageBytesIterator: Profiler  {
    private val rasterizer: PageRasterizer
    private val pdf: PdfDocument
    private var index = 0

    var resolution = 1600.0
    var retryMultiplier = 1.5

    constructor(rasterizer: PageRasterizer, bytes: ByteArray, password: String) : super("PdfPageBytesIterator") {
        this.rasterizer = rasterizer
        pdf = allocateErasure { PdfDocument.open(bytes, password) }
    }

    fun next(): ByteArray? {
        check(hasNext())
        val page = allocateErasure { pdf.pages[index++] }

        return runErasure("$name.next(): rasterizer.renderToByteArray()") {
            rasterizer.renderToByteArray(page, resolution)
        }
    }

    fun retry(): ByteArray? {
        index--
        resolution *= retryMultiplier
        return next()
    }

    fun hasNext(): Boolean = runErasure("$name.hasNext()") {
        pdf.pages.size > index
    }
}