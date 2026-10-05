package com.iago.barcode.scanner.pdf.barcode

import com.iago.app.profiler.profiling.Profiler

abstract class BarcodeDecoder(name: String): Profiler(name) {

    abstract suspend fun decode(bytes: ByteArray, formats: List<CodeFormat>, validator: (String) -> Boolean): String?

    enum class CodeFormat {
        ITF,
        CODE_128,
        QR_CODE
    }
}