package com.inegru.expensetrackr.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun ExpensePhoto(
    uri: Uri?,
    modifier: Modifier
) {
    uri?.let {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .size(Size.ORIGINAL)
                .build()
        )
        if (painter.state is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator()
        } else {
            Image(
                painter = painter,
                contentScale = ContentScale.FillWidth,
                contentDescription = "Expense receipt",
                modifier = modifier
                    .padding(8.dp)
            )
        }
    }
}