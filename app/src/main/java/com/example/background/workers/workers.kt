package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.R

class BlurWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params){


    private val TAG = "BlurWorker"
    override fun doWork(): Result{
        val appContext = applicationContext

        makeStatusNotification("Blurring image", appContext)

        return try {
            val picture = BitmapFactory.decodeResource(
                appContext.resources,
                R.drawable.android_cupcake
            )

            val output = blurBitmap(picture, appContext)

            // Write bitmap to file
            val outputUri = writeBitmapToFile(appContext, output)

            makeStatusNotification("Output is $outputUri", appContext)

            Result.success()
        }catch (throwable: Throwable){
            Log.e(TAG, "Error applying blur")
            Result.failure()
        }
    }
}

