package com.iago.barcode.scanner.pdf.text

import com.iago.app.profiler.profiling.Profiler
import io.github.yuroyami.kitepdf.PdfDocument

class PdfPageTextIterator: Profiler {
    private val pdf: PdfDocument
    private var index = 0

    constructor(bytes: ByteArray, password: String) : super("PdfPageTextIterator") {
        pdf = allocateErasure { PdfDocument.open(bytes, password) }
    }

    fun next(): String {
        check(hasNext())
        val page = allocateErasure { pdf.pages[index++] }

        return runErasure("$name.next(): page.extractText()") {
            page.extractText()
        }
    }

    fun hasNext(): Boolean = runErasure("$name.hasNext()") {
        pdf.pages.size >= index
    }
}