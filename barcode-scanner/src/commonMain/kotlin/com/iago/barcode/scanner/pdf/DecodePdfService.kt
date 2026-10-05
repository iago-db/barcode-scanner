package com.iago.barcode.scanner.pdf

import com.iago.barcode.scanner.pdf.barcode.BarcodeDecoder
import com.iago.barcode.scanner.pdf.bytearray.PdfPageBytesIterator
import com.iago.barcode.scanner.pdf.text.PdfPageTextIterator
import kotlin.math.ceil

class DecodePdfService(
    private val textIterator: PdfPageTextIterator,
    private val bytesIterator: PdfPageBytesIterator,
    private val barcodeDecoder: BarcodeDecoder
) {

    suspend fun decode(mode: DecodeMode): String {
        val code = when(mode) {
            DecodeMode.TEXT, DecodeMode.FIRST_PAGE -> decodeText(mode)
            DecodeMode.BARCODE -> decodeBarcode(listOf(BarcodeDecoder.CodeFormat.ITF, BarcodeDecoder.CodeFormat.CODE_128))
            DecodeMode.QR_CODE -> decodeBarcode(listOf(BarcodeDecoder.CodeFormat.QR_CODE))
        }

        println("[IAGO] - retries: ${ceil(bytesIterator.resolution / 1280 / 1.5).toInt()}")
        return code
    }

    private fun decodeText(mode: DecodeMode): String {
        return buildString {
            while (textIterator.hasNext()) {
                append(textIterator.next())

                if (mode == DecodeMode.FIRST_PAGE) {
                    break
                }
            }
        }
    }

    private suspend fun decodeBarcode(formats: List<BarcodeDecoder.CodeFormat>): String {
        while (bytesIterator.hasNext()) {
            var bytes = bytesIterator.next() ?: continue
            var code = barcodeDecoder.decode(bytes, formats, { isBarcodeValid(it) })

            if (code != null && code.isNotBlank()) {
                return code
            }

            for (i in 0..<3) {
                println("[RETRY] $i")
                bytes = bytesIterator.retry() ?: continue
                code = barcodeDecoder.decode(bytes, formats, { isBarcodeValid(it) })

                if (code != null && code.isNotBlank()) {
                    return code
                }
            }
        }

        return ""
    }

    private fun isBarcodeValid(code: String) = code.length >= 44

    enum class DecodeMode {
        TEXT,
        FIRST_PAGE,
        BARCODE,
        QR_CODE
    }
}