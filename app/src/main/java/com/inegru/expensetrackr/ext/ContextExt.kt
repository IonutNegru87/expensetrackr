package com.inegru.expensetrackr.ext

import android.content.Context
import com.inegru.expensetrackr.common.utils.DateUtils
import java.io.File

fun Context.createImageFile(): File {
    val timeStamp = DateUtils.formatDateTime()
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}