package com.zak.sidilan.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.BookDetail
import com.zak.sidilan.data.entities.VolumeInfo
import com.zak.sidilan.databinding.ActivityScanBinding
import com.zak.sidilan.ui.addbook.AddBookActivity
import com.zak.sidilan.ui.bottomsheets.ModalBottomSheetView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalGetImage
val scanActivityModule = module {
    factory { ScanActivity() }
}

@ExperimentalGetImage
class ScanActivity : AppCompatActivity(), ModalBottomSheetView.BottomSheetListener {
    private lateinit var binding: ActivityScanBinding
    private lateinit var cameraExecutor: ExecutorService
    private var type: Int = 0
    private val viewModel: ScanViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setupView()
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
        type = intent.getIntExtra("type",0)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.title_scan_isbn)
    }

    override fun onButtonClicked(volumeInfo: VolumeInfo?, book: Book?, bookQty: Long?) {
        val intent = Intent(baseContext, AddBookActivity::class.java)
        intent.putExtra("volume_info", volumeInfo)
        intent.putExtra("isbn_book", book)
        startActivity(intent)
        finish()
    }

    override fun onDismissed() {
        startCamera()
    }
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_EAN_13
            )
            .build()
        val scanner = BarcodeScanning.getClient(options)
        val analysisBarcode = ImageAnalysis.Builder()
            .build()

        analysisBarcode.setAnalyzer(
            Executors.newSingleThreadExecutor()
        ) { imageProxy ->
            processImageProxy(scanner, imageProxy)
        }

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()

                // Set up flash mode
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysisBarcode)
                val cameraControl = camera.cameraControl
                binding.toggleFlashButton.setOnCheckedChangeListener { _, isChecked ->
                    cameraControl.enableTorch(isChecked)
                }
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {

        imageProxy.image?.let { image ->
            val inputImage =
                InputImage.fromMediaImage(
                    image,
                    imageProxy.imageInfo.rotationDegrees
                )

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodeList ->
                    if (barcodeList.isNotEmpty()) {
                        val barcode = barcodeList[0]
                        val value = barcode.rawValue ?: ""
                        stopCamera()
                        when (type) {
                            //type 1: AddBook from ISBN
                            1 -> {
                                getBookByIsbn(value)
                            }

                        }
                    }
                }
                .addOnFailureListener {
                    // This failure will happen if the barcode scanning model
                    // fails to download from Google Play Services
                    Log.e(TAG, it.message.orEmpty())
                }.addOnCompleteListener {
                    // When the image is from CameraX analysis use case, must
                    // call image.close() on received images when finished
                    // using them. Otherwise, new images may not be received
                    // or the camera may stall.
                    imageProxy.image?.close()
                    imageProxy.close()
                }
        }
    }
    private fun stopCamera() {
        // You can unbind all use cases to stop the camera
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(this))
    }
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getBookByIsbn(isbn: String) {
        viewModel.searchBookByISBN(isbn)
        viewModel.bookByIsbn.observe(this) { book ->
            val modalBottomSheetView = ModalBottomSheetView(1, book, null)
            modalBottomSheetView.show(supportFragmentManager, ModalBottomSheetView.TAG)
            modalBottomSheetView.setBottomSheetListener(this)
        }
        viewModel.toastMessage.observe(this) {
            if (it == "Book not found on Google Books!") {
                val isbnBook = Book("", isbn.toLong(), "", listOf(), "", "", "", 0, 0, false, "", "")
                val modalBottomSheetView = ModalBottomSheetView(2, null, BookDetail(isbnBook, null, null))
                modalBottomSheetView.show(supportFragmentManager, ModalBottomSheetView.TAG)
                modalBottomSheetView.setBottomSheetListener(this)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}