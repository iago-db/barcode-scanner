package com.iago.barcode.scanner.pdf.barcode

expect class MLKitBarcodeDecoder: BarcodeDecoder {
    override suspend fun decode(bytes: ByteArray, formats: List<CodeFormat>, validator: (String) -> Boolean): String?
}