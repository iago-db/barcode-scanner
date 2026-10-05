package com.iago.barcode.scanner.pdf.barcode

import android.graphics.BitmapFactory
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.tasks.await

actual class MLKitBarcodeDecoder: BarcodeDecoder("AndroidMLKitBarcodeDecoder") {

    actual override suspend fun decode(bytes: ByteArray, formats: List<CodeFormat>, validator: (String) -> Boolean): String? {
        val options = allocateErasure { BarcodeScannerOptions.Builder().setBarcodeFormats(formats.toMLKitFormats()).build() }
        val bitmap = runErasure("$name.decode(): decodeByteArray()") { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }
        val image = allocateErasure { InputImage.fromBitmap(bitmap, 0) }
        val scanner = allocateErasure { BarcodeScanning.getClient(options) }

        return launchErasure("$name.decode(): scanner.process()") {
            val codes = scanner.process(image).await()
            codes.map { it.rawValue ?: "" }.firstOrNull { validator(it) }
        }
    }

    private fun List<CodeFormat>.toMLKitFormats(): Int {
        return map {
            when (it) {
                CodeFormat.ITF -> Barcode.FORMAT_ITF
                CodeFormat.CODE_128 -> Barcode.FORMAT_CODE_128
                CodeFormat.QR_CODE -> Barcode.FORMAT_QR_CODE
            }
        }.reduce(Int::or)
    }
}