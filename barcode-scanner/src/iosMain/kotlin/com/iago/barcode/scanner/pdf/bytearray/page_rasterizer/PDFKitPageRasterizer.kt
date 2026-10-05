package com.iago.barcode.scanner.pdf.bytearray.page_rasterizer

import com.iago.barcode.scanner.pdf.barcode.toByteArray
import com.iago.barcode.scanner.pdf.barcode.toNSData
import com.iago.barcode.scanner.pdf.bytearray.PdfBackground
import io.github.yuroyami.kitepdf.PdfPage
import io.github.yuroyami.kitepdf.core.KiteRawApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGSize
import platform.PDFKit.PDFDocument
import platform.PDFKit.kPDFDisplayBoxMediaBox
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIGraphicsImageRendererFormat
import platform.UIKit.UIImagePNGRepresentation

class PDFKitPageRasterizer: PageRasterizer("PDFKitPageRasterizer") {
    @OptIn(KiteRawApi::class, ExperimentalForeignApi::class)
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun renderToByteArray(
        kitePage: PdfPage,
        resolution: Double,
        background: PdfBackground
    ): ByteArray? {
        val kiteDocument = kitePage.document
        val document = allocateErasure { PDFDocument(kiteDocument.rawBytes.toNSData()) }
        val page = runErasure("PDFDocument.pageAtIndex") { document.pageAtIndex(kiteDocument.pages.indexOf(kitePage).toULong()) } ?: return null

        val bounds = runErasure("PDFPage.boundsForBox") { page.boundsForBox(kPDFDisplayBoxMediaBox) }
        val (width, height) = runErasure("PDFPage.boundsForBox") { bounds.useContents { size.width to size.height } }
        val size = cValue<CGSize> { this.width = width; this.height = height }
        val format = allocateErasure { UIGraphicsImageRendererFormat.defaultFormat().apply { this.scale = kitePage.scale(resolution, 40000000.0) } }
        val renderer = allocateErasure { UIGraphicsImageRenderer(size, format) }

        val image = runErasure("") {
            renderer.imageWithActions {
                val context = it ?: return@imageWithActions

                UIColor.whiteColor.set()
                context.fillRect(bounds)
                CGContextTranslateCTM(context.CGContext, 0.0, height)
                CGContextScaleCTM(context.CGContext, 1.0, -1.0)
                page.drawWithBox(kPDFDisplayBoxMediaBox, context.CGContext)
            }
        }

        return UIImagePNGRepresentation(image)?.toByteArray()
    }
}