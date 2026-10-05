package com.iago.barcode.scanner.pdf.barcode

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
public fun NSData.toByteArray(): ByteArray {
    val byteArray = ByteArray(length.toInt())

    byteArray.usePinned {
        memcpy(it.addressOf(0), bytes, length)
    }

    return byteArray
}