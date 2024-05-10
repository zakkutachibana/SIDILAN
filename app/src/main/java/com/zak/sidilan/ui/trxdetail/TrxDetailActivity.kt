package com.zak.sidilan.ui.trxdetail

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookInDonationTrx
import com.zak.sidilan.data.entities.BookInPrintingTrx
import com.zak.sidilan.data.entities.BookOutDonationTrx
import com.zak.sidilan.data.entities.BookOutSellingTrx
import com.zak.sidilan.databinding.ActivityTrxDetailBinding
import com.zak.sidilan.ui.bookdetail.BookDetailViewModel
import com.zak.sidilan.util.Formatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TrxDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrxDetailBinding
    private val viewModel: TrxDetailViewModel by viewModel()
    private val bookViewModel: BookDetailViewModel by viewModel()
    private lateinit var adapter : BookTrxHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrxDetailBinding.inflate(layoutInflater)


        val trxId = intent.getStringExtra("trxId")
        if (trxId != null) {
            viewModel.getBookTrxById(trxId)
        }
        setupView()
        setupViewModel()
        setupAction()

        setContentView(binding.root)
    }
    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail Transaksi"

        binding.header.tvBookTitleTrx.textAlignment = View.TEXT_ALIGNMENT_CENTER
        binding.header.tvBookPrice.textAlignment = View.TEXT_ALIGNMENT_CENTER
        binding.header.tvBookSubtotal.textAlignment = View.TEXT_ALIGNMENT_CENTER

        binding.header.tvBookSubtotalRp.visibility = View.GONE

        binding.header.tvBookQty.setTypeface(null, Typeface.BOLD)
        binding.header.tvBookTitleTrx.setTypeface(null, Typeface.BOLD)
        binding.header.tvBookPrice.setTypeface(null, Typeface.BOLD)
        binding.header.tvBookSubtotal.setTypeface(null, Typeface.BOLD)

        binding.header.tvBookQty.text = "qty"
        binding.header.tvBookTitleTrx.text = "Judul Buku"
        binding.header.tvBookSubtotal.text = "Subtotal"

        binding.footer.tvBookTitleTrx.textAlignment = View.TEXT_ALIGNMENT_CENTER
        binding.footer.tvBookPrice.textAlignment = View.TEXT_ALIGNMENT_CENTER

        binding.footer.tvBookQty.setTypeface(null, Typeface.BOLD)
        binding.footer.tvBookTitleTrx.setTypeface(null, Typeface.BOLD)
        binding.footer.tvBookPrice.setTypeface(null, Typeface.BOLD)
        binding.footer.tvBookSubtotal.setTypeface(null, Typeface.BOLD)
        binding.footer.tvBookSubtotalRp.setTypeface(null, Typeface.BOLD)

        binding.footer.tvBookPrice.text = ""
    }

    private fun setupViewModel () {
        viewModel.trxDetail.observe(this) { trxDetail ->
            when (val bookTrx = trxDetail?.bookTrx) {
                is BookInPrintingTrx -> {
                    setupRecyclerView(1)
                    adapter.submitList(bookTrx.books)
                    binding.header.tvBookPrice.text = "Biaya Cetak"

                    binding.tvTrxIdValue.text = bookTrx.id
                    binding.tvTrxDateValue.text = bookTrx.bookInDate
                    binding.tvParticipantValue.text = bookTrx.printingShopName
                    binding.tvTotalBookValue.text = getString(R.string.total_stock_qty, bookTrx.totalBookQty.toString())
                    binding.tvTotalKindValue.text = getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvTotalMoneyValue.text = getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(bookTrx.finalCost))

                    binding.footer.tvBookSubtotal.text = Formatter.addThousandSeparatorTextView(bookTrx.totalCost)
                    binding.footer.tvBookQty.text = bookTrx.totalBookQty.toString()
                    binding.footer.tvBookTitleTrx.text = getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvNumberAsWords.text = getString(R.string.rupiah, Formatter.convertNumberToWords(bookTrx.finalCost))

                    when (bookTrx.discountType) {
                        "percent" -> {
                            binding.footer.tvBookSubtotal.paintFlags = binding.footer.tvBookSubtotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            binding.tvDiscountAmount.text = Formatter.addThousandSeparatorTextView(bookTrx.discountAmount)
                            binding.tvTitleDiscount.text = getString(R.string.discount_percent_view, bookTrx.discountPercent.toString())
                            binding.tvFinalMoney.text = Formatter.addThousandSeparatorTextView(bookTrx.finalCost)
                        }
                        "flat" -> {
                            binding.footer.tvBookSubtotal.paintFlags = binding.footer.tvBookSubtotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            binding.tvDiscountAmount.text = Formatter.addThousandSeparatorTextView(bookTrx.discountAmount)
                            binding.tvFinalMoney.text = Formatter.addThousandSeparatorTextView(bookTrx.finalCost)
                        }

                        else -> {
                            binding.layoutDiscount.visibility = View.GONE
                            binding.footer.materialDivider.visibility = View.GONE
                        }
                    }

                }
                is BookInDonationTrx -> {
                    setupRecyclerView(0)
                    adapter.submitList(bookTrx.books)
                    binding.header.tvBookPrice.text = "Biaya Cetak"

                    binding.header.guideline.visibility = View.GONE
                    binding.header.guideline3.visibility = View.GONE

                    binding.tvTrxIdValue.text = bookTrx.id
                    binding.tvTrxDateValue.text = bookTrx.bookInDate
                    binding.tvParticipantValue.text = bookTrx.donorName
                    binding.tvTotalBookValue.text = getString(R.string.total_stock_qty, bookTrx.totalBookQty.toString())
                    binding.tvTotalKindValue.text = getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvTotalMoneyValue.visibility = View.GONE
                    binding.tvTotalMoney.visibility = View.GONE
                    binding.footer.materialDivider.visibility = View.GONE


                    binding.footer.tvBookSubtotal.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    binding.footer.tvBookSubtotal.text = "-"
                    binding.footer.tvBookSubtotalRp.visibility = View.GONE
                    binding.footer.tvBookQty.text = bookTrx.totalBookQty.toString()
                    binding.footer.tvBookTitleTrx.text = getString(R.string.book_count, bookTrx.totalBookKind.toString())

                    binding.layoutDiscount.visibility = View.GONE
                    binding.tvNumberAsWords.visibility = View.GONE
                }
                is BookOutSellingTrx -> {
                    setupRecyclerView(2)
                    adapter.submitList(bookTrx.books)
                    binding.header.tvBookPrice.text = "Harga Jual"

                    binding.tvTrxIdValue.text = bookTrx.id
                    binding.tvTrxDateValue.text = bookTrx.bookOutDate
                    binding.tvParticipantValue.text = bookTrx.buyerName
                    binding.tvTotalBookValue.text = getString(R.string.total_stock_qty, bookTrx.totalBookQty.toString())
                    binding.tvTotalKindValue.text = getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvTotalMoneyValue.text = getString(R.string.rp_price, Formatter.addThousandSeparatorTextView(bookTrx.finalPrice))

                    binding.footer.tvBookSubtotal.text = Formatter.addThousandSeparatorTextView(bookTrx.totalPrice)
                    binding.footer.tvBookQty.text = bookTrx.totalBookQty.toString()
                    binding.footer.tvBookTitleTrx.text = getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvNumberAsWords.text = getString(R.string.rupiah, Formatter.convertNumberToWords(bookTrx.finalPrice))

                    when (bookTrx.discountType) {
                        "percent" -> {
                            binding.footer.tvBookSubtotal.paintFlags = binding.footer.tvBookSubtotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            binding.tvDiscountAmount.text = Formatter.addThousandSeparatorTextView(bookTrx.discountAmount)
                            binding.tvTitleDiscount.text = getString(R.string.discount_percent_view, bookTrx.discountPercent.toString())
                            binding.tvFinalMoney.text = Formatter.addThousandSeparatorTextView(bookTrx.finalPrice)
                        }
                        "flat" -> {
                            binding.footer.tvBookSubtotal.paintFlags = binding.footer.tvBookSubtotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            binding.tvDiscountAmount.text = Formatter.addThousandSeparatorTextView(bookTrx.discountAmount)
                            binding.tvFinalMoney.text = Formatter.addThousandSeparatorTextView(bookTrx.finalPrice)
                        }
                        else -> {
                            binding.layoutDiscount.visibility = View.GONE
                            binding.footer.materialDivider.visibility = View.GONE
                        }
                    }

                }
                is BookOutDonationTrx -> {
                    setupRecyclerView(0)
                    adapter.submitList(bookTrx.books)
                    binding.header.tvBookPrice.text = "Harga Jual"

                    binding.tvTrxIdValue.text = bookTrx.id
                    binding.tvTrxDateValue.text = bookTrx.bookOutDate
                    binding.tvParticipantValue.text = bookTrx.doneeName
                    binding.tvTotalBookValue.text = getString(R.string.total_stock_qty, bookTrx.totalBookQty.toString())
                    binding.tvTotalKindValue.text = getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvTotalMoneyValue.visibility = View.GONE
                    binding.tvTotalMoney.visibility = View.GONE

                    binding.footer.tvBookSubtotal.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    binding.footer.tvBookSubtotal.text = "-"
                    binding.footer.tvBookSubtotalRp.visibility = View.GONE
                    binding.footer.tvBookQty.text = bookTrx.totalBookQty.toString()
                    binding.footer.tvBookTitleTrx.text = getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.footer.materialDivider.visibility = View.GONE

                    binding.layoutDiscount.visibility = View.GONE
                    binding.tvNumberAsWords.visibility = View.GONE
                }
                else -> {
                    finish()
                }
            }
            viewModel.getUserById(trxDetail?.logs?.createdBy.toString())
            viewModel.user.observe(this) { user ->
                binding.userCard.tvUserName.text = getString(R.string.by_at, user?.displayName, user?.role, Formatter.convertEpochToLocal(trxDetail?.logs?.createdAt))
                binding.userCard.ivProfilePicture.load(user?.photoUrl)
            }
        }
    }

    private fun setupAction () {
        binding.btnInvoice.setOnClickListener {
            viewModel.trxDetail.observe(this) { trxDetail ->
                val invId = trxDetail?.bookTrx?.id
                generatePdfInvoice(this, invId)
            }

        }
    }

    private fun generatePdfInvoice(context: Context, invId: String?): Uri? {

            val pdfDocument = PdfDocument()

            // Create a PageInfo object defining the page size
            val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1)
                .create() // US Letter size: 612 x 792 points

            // Start a new page
            val page = pdfDocument.startPage(pageInfo)

            // Get the Canvas object to draw on the page
            val canvas = page.canvas

            // Add content to the page
            // For example, draw text
            val paint = Paint()
            paint.textSize = 12f
            canvas.drawText("Invoice", 100f, 100f, paint)

            // Finish the page
            pdfDocument.finishPage(page)

            // Define the file path where the PDF file will be saved locally
            val fileName = "${invId}.pdf"
            val file = File(context.cacheDir, fileName)

            try {
                // Create a FileOutputStream to write to the file
                val fileOutputStream = FileOutputStream(file)

                // Write the PDF document to the FileOutputStream
                pdfDocument.writeTo(fileOutputStream)

                // Close the FileOutputStream
                fileOutputStream.close()

                // Close the PdfDocument
                pdfDocument.close()

                Log.d("PDF", "PDF invoice created locally: ${file.absolutePath}")

                // Upload the PDF file to Firebase Storage
                viewModel.saveInvoicePDF(file)

                // Return the Uri of the local PDF file
                return Uri.fromFile(file)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

    }
    private fun setupRecyclerView(type: Int) {
        adapter = BookTrxHistoryAdapter(this, bookViewModel, type)
        binding.rvTrxHistory.layoutManager = LinearLayoutManager(this)
        binding.rvTrxHistory.adapter = adapter
        binding.rvTrxHistory.itemAnimator = DefaultItemAnimator()
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
}