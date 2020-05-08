package com.foundry.compleat.tools

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.util.Size
import android.view.TextureView
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.foundry.compleat.R
import com.foundry.compleat.utils.BarcodeAnalyzer
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.squareup.square.Environment
import com.squareup.square.SquareClient
import com.squareup.square.models.CatalogQuery
import com.squareup.square.models.CatalogQueryExact
import com.squareup.square.models.SearchCatalogObjectsRequest
import java.util.*
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

    var client: SquareClient = SquareClient.Builder()
        .environment(Environment.PRODUCTION)
        .accessToken("ADD ACCESS TOKEN HERE")
        .build()
    var api = client.catalogApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_builder)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

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
                CameraX.unbindAll()
                searchBarcode(it)
                Log.d("InventoryBuilder", "Barcode detected: ${it.rawValue}.")
            }
        }

        imageAnalysis.setAnalyzer(analysisExecutor, barcodeAnalyzer)

        CameraX.bindToLifecycle(this as LifecycleOwner, preview, imageAnalysis)
    }

    fun searchBarcode(it: FirebaseVisionBarcode) {
        val itemVarType = MutableList<String>(1) {"ITEM_VARIATION"}

        var exactQuery = CatalogQueryExact.Builder(
            "sku",
            it.rawValue.toString()
        ).build()

        var query = CatalogQuery.Builder()
            .exactQuery(exactQuery)
            .build()

        var body = SearchCatalogObjectsRequest.Builder()
            .objectTypes(itemVarType)
            .query(query)
            .build()

        val itemVariationResponse = api.searchCatalogObjects(body)

        val itemType = MutableList<String>(1){"ITEM"}
        if( itemVariationResponse.objects != null ) {
            exactQuery = CatalogQueryExact.Builder(
                "version",
                itemVariationResponse.objects[0].version.toString()
            ).build()
            query = CatalogQuery.Builder()
                .exactQuery(exactQuery)
                .build()
            body = SearchCatalogObjectsRequest.Builder()
                .objectTypes(itemType)
                .query(query)
                .build()

            val itemObject = api.searchCatalogObjects(body).objects[0].itemData

            val rawBarcode = this.viewFinder.findViewById<TextView>(R.id.rawBarcode)
            val itemInfo = this.viewFinder.findViewById<TextView>(R.id.item_info)
            rawBarcode.setText(it.rawValue)
            itemInfo.setText(
                "Item Name: " + itemObject.name + "\n" +
                        "Description: " + itemVariationResponse.objects[0].itemData.description + "\n" +
                        "Price: " + itemVariationResponse.objects[0].itemVariationData.priceMoney.amount + "\n"
            )
        } else {
            val rawBarcode = window.decorView.findViewById<TextView>(R.id.rawBarcode)
            val itemInfo = window.decorView.findViewById<TextView>(R.id.item_info)
            rawBarcode.setText(it.rawValue)
            itemInfo.setText("Item Not Found.")
        }


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


