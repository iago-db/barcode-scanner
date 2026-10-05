package com.iago.barcode.scanner.pdf.bytearray.page_rasterizer

import com.iago.barcode.scanner.pdf.barcode.toByteArray
import com.iago.barcode.scanner.pdf.bytearray.PdfBackground
import io.github.yuroyami.kitepdf.PdfPage
import io.github.yuroyami.kitepdf.core.render.KITE_DEFAULT_MAX_RASTER_PIXELS
import io.github.yuroyami.kitepdf.nativerenderer.ApplePdfRasterizer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.posix.memcpy

actual class NativePageRasterizer: PageRasterizer("iOSNativePageRasterizer") {

    private data class Color(
        val red: Double,
        val green: Double,
        val blue: Double,
        val alpha: Double
    )

    actual override fun renderToByteArray(
        page: PdfPage,
        resolution: Double,
        background: PdfBackground
    ): ByteArray? {
        val color = background.toColor()
        val scale = page.scale(resolution, 40000000.0)
        println("[IAGO] scale: $scale")

        val data = runErasure("$name.renderToByteArray(): TempApplePdfRasterizer.renderToPngData()") {
            ApplePdfRasterizer.renderToPngData(
                page,
                scale,
                color.red,
                color.green,
                color.blue,
                color.alpha,
                KITE_DEFAULT_MAX_RASTER_PIXELS
            )
        }

        return runErasure("$name.renderToByteArray(): data?.toByteArray()") {
            data?.toByteArray()
        }
    }

    private fun PdfBackground.toColor(): Color {
        return when (this) {
            PdfBackground.WHITE -> Color(1.0, 1.0, 1.0, 1.0)
        }
    }
}