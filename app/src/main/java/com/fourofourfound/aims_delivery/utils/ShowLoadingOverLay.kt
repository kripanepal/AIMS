package com.fourofourfound.aims_delivery.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ProgressBar
import com.fourofourfound.aimsdelivery.R
import com.github.ybq.android.spinkit.style.CubeGrid

fun showLoadingOverLay(context: Context): AlertDialog {
    var builder = AlertDialog.Builder(context);
    val progressBar = ProgressBar(context)
    val cube = CubeGrid()
    cube.color = context.getColor(R.color.Aims_Orange)
    progressBar.indeterminateDrawable = cube
    progressBar.setPadding(0, 50, 0, 50)
    builder.setView(progressBar);
    return builder.create().apply {
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}