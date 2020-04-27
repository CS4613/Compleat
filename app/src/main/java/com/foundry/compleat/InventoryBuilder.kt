package com.foundry.compleat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.TextureView
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysisConfig
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.foundry.compleat.utils.BarcodeAnalyzer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// This is an arbitrary number we are using to keep track of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts.
private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class InventoryBuilder : AppCompatActivity(), LifecycleOwner {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }

    private lateinit var viewFinder: TextureView
    private lateinit var analysisExecutor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_builder)

        viewFinder = findViewById(R.id.view_finder)

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            viewFinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }

        analysisExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val previewConfig= PreviewConfig.Builder()
            .setLensFacing(CameraX.LensFacing.BACK)
            .build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener { previewOutput ->
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            viewFinder.surfaceTexture = previewOutput.surfaceTexture
            parent.addView(viewFinder, 0)
        }

        val imageAnalysisConfig = ImageAnalysisConfig.Builder()
            .setTargetResolution(Size(800,800))
            .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
            .build()
        val imageAnalysis = ImageAnalysis(imageAnalysisConfig)

        val barcodeAnalyzer = BarcodeAnalyzer { barCodes ->
            barCodes.forEach {
                Log.d("InventoryBuilder", "Barcode detected: ${it.rawValue}.")
            }
        }

        imageAnalysis.setAnalyzer(analysisExecutor, barcodeAnalyzer)

        CameraX.bindToLifecycle(this as LifecycleOwner, preview, imageAnalysis)
    }

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (isCameraPermissionGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(
                    this,
                    "Camera permissions are required to continue.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission = ContextCompat.checkSelfPermission(
            baseContext, Manifest.permission.CAMERA )
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

}


