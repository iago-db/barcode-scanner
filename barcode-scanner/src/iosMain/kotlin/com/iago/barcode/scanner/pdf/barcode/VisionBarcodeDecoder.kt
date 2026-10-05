package com.iago.barcode.scanner.pdf.barcode

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSError
import platform.Vision.VNBarcodeObservation
import platform.Vision.VNBarcodeSymbology
import platform.Vision.VNBarcodeSymbologyCode128
import platform.Vision.VNBarcodeSymbologyI2of5
import platform.Vision.VNBarcodeSymbologyQR
import platform.Vision.VNDetectBarcodesRequest
import platform.Vision.VNImageRequestHandler
import kotlin.native.runtime.NativeRuntimeApi

class VisionBarcodeDecoder: BarcodeDecoder("VisionBarcodeDecoder") {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class, NativeRuntimeApi::class)
    override suspend fun decode(bytes: ByteArray, formats: List<CodeFormat>, validator: (String) -> Boolean): String? {
        val data = allocateErasure { bytes.toNSData() }
        val requestHandler = allocateErasure { VNImageRequestHandler(data, emptyMap<Any?, Any>()) }
        val request = allocateErasure { VNDetectBarcodesRequest() }
        request.symbologies = formats.toSymbologies()

        memScoped {
            val error: ObjCObjectVar<NSError?> = alloc()

            launchErasure("$name.decode(): requestHandler.performRequests()") {
                requestHandler.performRequests(listOf(request), error.ptr)
            }

            error.value?.let {
                println("[IAGO] error: $it")
            }
        }

        val results = request.results.orEmpty() as List<VNBarcodeObservation>
        return results.mapNotNull { it.payloadStringValue }.firstOrNull { validator(it) }
    }

    private fun List<CodeFormat>.toSymbologies(): List<VNBarcodeSymbology> {
        return map {
            when (it) {
                CodeFormat.ITF -> VNBarcodeSymbologyI2of5
                CodeFormat.CODE_128 -> VNBarcodeSymbologyCode128
                CodeFormat.QR_CODE -> VNBarcodeSymbologyQR
            }
        }
    }
}