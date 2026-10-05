package com.iago.barcode.scanner.pdf.barcode

import cocoapods.MLKitBarcodeScanning.MLKBarcode
import cocoapods.MLKitBarcodeScanning.MLKBarcodeFormatCode128
import cocoapods.MLKitBarcodeScanning.MLKBarcodeFormatITF
import cocoapods.MLKitBarcodeScanning.MLKBarcodeFormatQRCode
import cocoapods.MLKitBarcodeScanning.MLKBarcodeScanner
import cocoapods.MLKitBarcodeScanning.MLKBarcodeScannerOptions
import cocoapods.MLKitVision.MLKVisionImage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import objcnames.protocols.MLKCompatibleImageProtocol
import platform.Foundation.NSError
import platform.UIKit.UIImage
import kotlin.collections.reduce
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NSErrorException(error: NSError): Throwable(error.localizedDescription)

actual class MLKitBarcodeDecoder: BarcodeDecoder("iOSLKitBarcodeDecoder") {

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun decode(bytes: ByteArray, formats: List<CodeFormat>, validator: (String) -> Boolean): String? {
        val options = allocateErasure { MLKBarcodeScannerOptions(formats.toMLKitFormat()) }
        val scanner = allocateErasure { MLKBarcodeScanner.barcodeScannerWithOptions(options) }
        val uiImage = allocateErasure { UIImage(bytes.toNSData()) }
        val image = allocateErasure { MLKVisionImage(uiImage) }
        val barcodes = launchErasure("$name.decode(): scanner.processImage()") { scanner.processImage(image) }
        return barcodes.mapNotNull { it.rawValue }.firstOrNull { validator(it) }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun List<CodeFormat>.toMLKitFormat(): Long {
        return this.map {
            when (it) {
                CodeFormat.ITF -> MLKBarcodeFormatITF
                CodeFormat.CODE_128 -> MLKBarcodeFormatCode128
                CodeFormat.QR_CODE -> MLKBarcodeFormatQRCode
            }
        }.reduce(Long::or)
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun MLKBarcodeScanner.processImage(image: MLKVisionImage): List<MLKBarcode> {
        return suspendCancellableCoroutine { emmiter ->
            processImage(image as MLKCompatibleImageProtocol) { barcodes, error ->
                error?.let { emmiter.resumeWithException(NSErrorException(error)) } ?:
                run { emmiter.resume(barcodes as? List<MLKBarcode> ?: emptyList()) }
            }
        }
    }
}