package com.zak.sidilan.ui.addbook

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import coil.load
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.Book
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.entities.VolumeInfo
import com.zak.sidilan.databinding.ActivityAddBookBinding
import com.zak.sidilan.util.Formatter
import com.zak.sidilan.ui.bottomsheets.ModalBottomSheetView
import com.zak.sidilan.util.HawkManager
import org.koin.dsl.module
import java.util.Calendar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

val addBookActivityModule = module {
    factory { AddBookActivity() }
}

class AddBookActivity : AppCompatActivity(), ModalBottomSheetView.BottomSheetListener {
    private lateinit var binding: ActivityAddBookBinding
    private val viewModel: AddBookViewModel by viewModel()
    private var isUpdateMode = false
    private var bookId: String = ""
    private lateinit var hawkManager : HawkManager
    private var currentImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isUpdateMode = intent.getBooleanExtra("is_update_mode", false)
        bookId = intent.getStringExtra("book_id").toString()
        hawkManager = HawkManager(this)

        setView()
        setViewModel()
        setAction()
        setupListeners()

        if (isUpdateMode) {
            loadBookData(bookId)
        }

        val volumeInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("volume_info", VolumeInfo::class.java)
        } else {
            intent.getParcelableExtra<VolumeInfo>("volume_info")
        }

        val isbnBook = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("isbn_book", Book::class.java)
        } else {
            intent.getParcelableExtra<Book>("isbn_book")
        }
        setBookData(volumeInfo, isbnBook)
    }

    private fun setViewModel() {
        viewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadBookData(bookId: String) {
        viewModel.getBookDetailById(bookId)
        viewModel.bookDetail.observe(this) { bookDetail ->
            binding.edIsbn.setText(bookDetail?.book?.isbn.toString())
            binding.edBookTitle.setText(bookDetail?.book?.title.toString())
            binding.edPublishedDate.setText(bookDetail?.book?.publishedDate.toString())
            binding.edAuthors.setText(bookDetail?.book?.authors?.joinToString("\n"))
            binding.edGenre.setText(bookDetail?.book?.genre.toString())
            binding.edPrintPrice.setText((bookDetail?.book?.printPrice ?: 0).toString())
            binding.edSellPrice.setText((bookDetail?.book?.sellPrice ?: 0).toString())
            if (bookDetail?.book?.isPerpetual == false) {
                binding.edStartContractDate.setText(bookDetail.book.startContractDate.toString())
                binding.edEndContractDate.setText(bookDetail.book.endContractDate.toString())
            } else {
                binding.cbForever.isChecked = true
            }
        }
    }

    private fun setBookData(volumeInfo: VolumeInfo?, isbnBook: Book?) {
        if (volumeInfo != null) {
            for (identifier in volumeInfo.industryIdentifiers!!) {
                if (identifier?.type == "ISBN_13") {
                    val isbn13 = identifier.identifier
                    binding.edIsbn.setText(isbn13)
                }
            }
            val imageUrlFromApi = volumeInfo.imageLinks?.thumbnail.toString()
            val trimmedImageUrl = imageUrlFromApi.substringBefore("&img=1") + "&img=1"
            binding.ivBookCoverAdd.load(trimmedImageUrl)
            binding.edBookTitle.setText(volumeInfo.title.toString())
            binding.edPublishedDate.setText(Formatter.convertDateAPIToFirebase(volumeInfo.publishedDate.toString()))
            binding.edAuthors.setText(volumeInfo.authors?.joinToString("\n"))
            binding.edGenre.setText(volumeInfo.categories?.getOrNull(0).toString())
        } else {
            if (isbnBook != null) {
                binding.edIsbn.setText(isbnBook.isbn.toString())
            }
        }

    }


    private fun setView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        if (isUpdateMode) {
            supportActionBar?.title = getString(R.string.title_update_book)
            binding.btnAddBook.text = getString(R.string.title_update_book)
        } else {
            supportActionBar?.title = getString(R.string.title_add_book)
        }
        Formatter.addThousandSeparatorEditText(binding.edPrintPrice)
        Formatter.addThousandSeparatorEditText(binding.edSellPrice)

        val currentUser = hawkManager.retrieveData<User>("user")

        binding.userCard.tvUserName.text = currentUser?.displayName
        binding.userCard.ivProfilePicture.load(currentUser?.photoUrl)
    }

    private fun setAction() {
        val launcherGallery = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                currentImageUri = uri
                setImage()
            } else {
                Log.d("Photo Picker", "No media selected")
            }
        }
        binding.btnChangeBookPic.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.edlIsbn.setEndIconOnClickListener {
            when {
                binding.edIsbn.text?.isEmpty() == true -> isNotEmpty(
                    binding.edIsbn.text.toString(),
                    binding.edlIsbn,
                    binding.edIsbn
                )

                binding.edIsbn.text?.length!! != 13 && binding.edIsbn.text?.length!! != 10 -> binding.edlIsbn.error =
                    "Masukkan 10 atau 13 Digit ISBN!"

                else -> {
                    viewModel.searchBookByISBN(binding.edIsbn.text.toString())
                    viewModel.bookByIsbn.observe(this) { book ->
                        val modalBottomSheetView = ModalBottomSheetView(1, book, null)
                        modalBottomSheetView.show(supportFragmentManager, ModalBottomSheetView.TAG)
                        modalBottomSheetView.setBottomSheetListener(this) // Set the listener
                    }


                }
            }
        }
        binding.edPublishedDate.setOnClickListener {
            showDatePicker(binding.edPublishedDate, "Tanggal Terbit Buku")
        }
        binding.edStartContractDate.setOnClickListener {
            showDatePicker(binding.edStartContractDate, "Tanggal Mulai PKS")
        }
        binding.edEndContractDate.setOnClickListener {
            showDatePicker(binding.edEndContractDate, "Tanggal Selesai PKS")
        }
        binding.cbForever.setOnCheckedChangeListener { _, isChecked ->
            binding.edlStartContractDate.isEnabled = !isChecked
            binding.edlEndContractDate.isEnabled = !isChecked
            binding.edStartContractDate.text?.clear()
            binding.edEndContractDate.text?.clear()
        }

        binding.btnAddBook.setOnClickListener {
            if (isValid()) {
                binding.btnAddBook.isEnabled = false

                val isbn = binding.edIsbn.text.toString().toLong()
                val title = binding.edBookTitle.text.toString()
                val authors = binding.edAuthors.text.toString().split("\n").map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .map { it }
                val coverImage = getImageUriFromImageView(binding.ivBookCoverAdd)
                val genre = binding.edGenre.text.toString()
                val publishedDate = binding.edPublishedDate.text.toString()
                val printPrice = Formatter.getRawValue(binding.edPrintPrice).toLong()
                val sellPrice = Formatter.getRawValue(binding.edSellPrice).toLong()
                val isPerpetual = binding.cbForever.isChecked
                val startContractDate = binding.edStartContractDate.text.toString().ifEmpty { null }
                val endContractDate = binding.edEndContractDate.text.toString().ifEmpty { null }
                val createdBy = hawkManager.retrieveData<User>("user")?.id.toString()

                if (isUpdateMode) {
                    updateBook(bookId, isbn, title, authors, coverImage, genre, publishedDate, printPrice, sellPrice, isPerpetual, startContractDate, endContractDate)
                } else {
                    saveBook(isbn, title, authors, coverImage, genre, publishedDate, printPrice, sellPrice, isPerpetual, startContractDate, endContractDate, createdBy)
                }
            }
        }
    }
    private fun setImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivBookCoverAdd.setImageURI(it)
        }
    }

    private fun getImageUriFromImageView(imageView: ImageView): Uri? {
        return when (val drawable = imageView.drawable) {
            is BitmapDrawable -> {
                drawable.bitmap?.let { bitmap ->
                    val uri = getImageUriFromBitmap(bitmap)
                    uri
                }
            }
            else -> {
                val imageUrl = "placeholder" // Replace "placeholder" with the actual URL used by Coil
                Uri.parse(imageUrl)
            }
        }
    }

    // Function to get image URI from Bitmap
    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri? {
        return try {
            val cachePath = File.createTempFile("image", null, applicationContext.cacheDir)
            val outputStream = FileOutputStream(cachePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            cachePath.toUri()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    override fun onButtonClicked(volumeInfo: VolumeInfo?, book: Book?, bookQty: Long?) {
        val title = volumeInfo?.title.toString()
        val subtitle = volumeInfo?.subtitle.toString()
        val fullTitle =
            if (subtitle != "null" && subtitle.isNotEmpty()) "$title: $subtitle" else title
        binding.edBookTitle.setText(fullTitle)
        binding.edBookTitle.requestLayout()
        binding.edPublishedDate.setText(Formatter.convertDateAPIToFirebase(volumeInfo?.publishedDate.toString()))
        binding.edAuthors.setText(volumeInfo?.authors?.joinToString("\n"))
        binding.edGenre.setText(volumeInfo?.categories?.getOrNull(0))
        val imageUrlFromApi = volumeInfo?.imageLinks?.thumbnail.toString()
        val trimmedImageUrl = imageUrlFromApi.substringBefore("&img=1") + "&img=1"
        binding.ivBookCoverAdd.load(trimmedImageUrl)
        Toast.makeText(this, trimmedImageUrl, Toast.LENGTH_SHORT).show()
        println("ini linknya: $trimmedImageUrl")
    }

    override fun onDismissed() { }

    private fun saveBook(isbn: Long, title: String, authors: List<String>, coverImage: Uri?, genre: String, publishedDate: String, printPrice: Long, sellPrice: Long, isPerpetual: Boolean, startContractDate: String?, endContractDate: String?, createdBy: String) {
        viewModel.saveBookToFirebase(
            isbn, title, authors, coverImage, genre, publishedDate,
            printPrice, sellPrice, isPerpetual, startContractDate, endContractDate, createdBy
        ) { success, message ->
            binding.btnAddBook.isEnabled = true
            if (success) {
                Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add book: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateBook(bookId: String, isbn: Long, title: String, authors: List<String>, coverUrl: Uri?, genre: String, publishedDate: String, printPrice: Long, sellPrice: Long, isPerpetual: Boolean, startContractDate: String?, endContractDate: String?) {
        viewModel.updateBookFirebase(
            bookId, isbn, title, authors, coverUrl, genre, publishedDate,
            printPrice, sellPrice, isPerpetual, startContractDate, endContractDate
        ) { success, message ->
            binding.btnAddBook.isEnabled = true
            if (success) {
                Toast.makeText(this, "Book updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update book: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValid(): Boolean {
        when (binding.cbForever.isChecked) {
            true -> return isNotEmpty(
                binding.edIsbn.text.toString(),
                binding.edlIsbn,
                binding.edIsbn
            )
                    && isNotEmpty(
                binding.edBookTitle.text.toString(),
                binding.edlBookTitle,
                binding.edBookTitle
            )
                    && isNotEmpty(
                binding.edAuthors.text.toString(),
                binding.edlAuthors,
                binding.edAuthors
            )
                    && isNotEmpty(
                binding.edGenre.text.toString(),
                binding.edlGenre,
                binding.edGenre
            )
                    && isNotEmpty(
                binding.edPublishedDate.text.toString(),
                binding.edlPublishedDate,
                binding.edPublishedDate
            )
                    && isNotEmpty(
                binding.edPrintPrice.text.toString(),
                binding.edlPrintPrice,
                binding.edPrintPrice
            )
                    && isNotEmpty(
                binding.edSellPrice.text.toString(),
                binding.edlSellPrice,
                binding.edSellPrice
            )

            else -> return isNotEmpty(
                binding.edIsbn.text.toString(),
                binding.edlIsbn,
                binding.edIsbn
            )
                    && isNotEmpty(
                binding.edBookTitle.text.toString(),
                binding.edlBookTitle,
                binding.edBookTitle
            )
                    && isNotEmpty(
                binding.edAuthors.text.toString(),
                binding.edlAuthors,
                binding.edAuthors
            )
                    && isNotEmpty(
                binding.edGenre.text.toString(),
                binding.edlGenre,
                binding.edGenre
            )
                    && isNotEmpty(
                binding.edPublishedDate.text.toString(),
                binding.edlPublishedDate,
                binding.edPublishedDate
            )
                    && isNotEmpty(
                binding.edPrintPrice.text.toString(),
                binding.edlPrintPrice,
                binding.edPrintPrice
            )
                    && isNotEmpty(
                binding.edSellPrice.text.toString(),
                binding.edlSellPrice,
                binding.edSellPrice
            )
                    && isNotEmpty(
                binding.edStartContractDate.text.toString(),
                binding.edlStartContractDate,
                binding.edStartContractDate
            )
                    && isNotEmpty(
                binding.edEndContractDate.text.toString(),
                binding.edlEndContractDate,
                binding.edEndContractDate
            )
        }
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.ed_isbn -> {
                    isNotEmpty(binding.edIsbn.text.toString(), binding.edlIsbn, binding.edIsbn)
                }

                R.id.ed_book_title -> {
                    isNotEmpty(
                        binding.edBookTitle.text.toString(),
                        binding.edlBookTitle,
                        binding.edBookTitle
                    )
                }

                R.id.ed_authors -> {
                    isNotEmpty(
                        binding.edAuthors.text.toString(),
                        binding.edlAuthors,
                        binding.edAuthors
                    )
                }

                R.id.ed_genre -> {
                    isNotEmpty(binding.edGenre.text.toString(), binding.edlGenre, binding.edGenre)
                }

                R.id.ed_published_date -> {
                    isNotEmpty(
                        binding.edPublishedDate.text.toString(),
                        binding.edlPublishedDate,
                        binding.edPublishedDate
                    )
                }

                R.id.ed_print_price -> {
                    isNotEmpty(
                        binding.edPrintPrice.text.toString(),
                        binding.edlPrintPrice,
                        binding.edPrintPrice
                    )
                }

                R.id.ed_sell_price -> {
                    isNotEmpty(
                        binding.edSellPrice.text.toString(),
                        binding.edlSellPrice,
                        binding.edSellPrice
                    )
                }

                R.id.ed_start_contract_date -> {
                    if (!binding.cbForever.isChecked) {
                        isNotEmpty(
                            binding.edStartContractDate.text.toString(),
                            binding.edlStartContractDate,
                            binding.edStartContractDate
                        )
                    }
                }

                R.id.ed_end_contract_date -> {
                    if (!binding.cbForever.isChecked) {
                        isNotEmpty(
                            binding.edEndContractDate.text.toString(),
                            binding.edlEndContractDate,
                            binding.edEndContractDate
                        )
                    }
                }
            }
        }
    }

    private fun isNotEmpty(
        value: String,
        textInputLayout: TextInputLayout,
        editText: EditText
    ): Boolean {
        when {
            value.trim().isEmpty() -> {
                textInputLayout.error = "Input " + textInputLayout.hint.toString() + " diperlukan"
                editText.requestFocus()
                return false
            }

            else -> {
                textInputLayout.isErrorEnabled = false
            }
        }
        return true
    }

    private fun setupListeners() {
        binding.edIsbn.addTextChangedListener(TextFieldValidation(binding.edIsbn))
        binding.edBookTitle.addTextChangedListener(TextFieldValidation(binding.edBookTitle))
        binding.edAuthors.addTextChangedListener(TextFieldValidation(binding.edAuthors))
        binding.edGenre.addTextChangedListener(TextFieldValidation(binding.edGenre))
        binding.edPublishedDate.addTextChangedListener(TextFieldValidation(binding.edPublishedDate))
        binding.edPrintPrice.addTextChangedListener(TextFieldValidation(binding.edPrintPrice))
        binding.edSellPrice.addTextChangedListener(TextFieldValidation(binding.edSellPrice))
        binding.edStartContractDate.addTextChangedListener(TextFieldValidation(binding.edStartContractDate))
        binding.edEndContractDate.addTextChangedListener(TextFieldValidation(binding.edEndContractDate))
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

    private fun showDatePicker(dateEditText: TextView, title: String) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateEditText.text = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.setTitle(title)
        datePickerDialog.show()
    }
}