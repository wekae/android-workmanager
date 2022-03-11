package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R

class BlurWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params){


    private val TAG = "BlurWorker"
    override fun doWork(): Result{
        val appContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image", appContext)

        return try {
            // REMOVE THIS
            // val picture = BitmapFactory.decodeResource(
            //    appContext.resources,
            //    R.drawable.android_cupcake
            //)

            if(TextUtils.isEmpty(resourceUri)){
                Log.e(TAG, "Invalid input URI")
                throw IllegalArgumentException("Invalid input URI")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )

            val output = blurBitmap(picture, appContext)

            // Write bitmap to file
            val outputUri = writeBitmapToFile(appContext, output)

            makeStatusNotification("Output is $outputUri", appContext)

            // Output temporary URI
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            Result.success(outputData)
        }catch (throwable: Throwable){
            Log.e(TAG, "Error applying blur")
            Result.failure()
        }
    }
}

