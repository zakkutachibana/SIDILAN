package com.zak.sidilan.ui.trxdetail

import android.R.id.message
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookInDonationTrx
import com.zak.sidilan.data.entities.BookInPrintingTrx
import com.zak.sidilan.data.entities.BookOutDonationTrx
import com.zak.sidilan.data.entities.BookOutSellingTrx
import com.zak.sidilan.databinding.ActivityTrxDetailBinding
import com.zak.sidilan.util.Formatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.File


class TrxDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrxDetailBinding
    private val viewModel: TrxDetailViewModel by viewModel()
    private lateinit var adapter: BookTrxHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrxDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trxId = intent.getStringExtra("trxId")
        if (trxId != null) {
            viewModel.getBookTrxById(trxId)
        }
        setupView()
        setupViewModel()
        setupAction()

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

    private fun setupViewModel() {
        viewModel.trxDetail.observe(this) { trxDetail ->
            when (val bookTrx = trxDetail?.bookTrx) {
                is BookInPrintingTrx -> {
                    setupRecyclerView(1)
                    adapter.submitList(bookTrx.books)
                    binding.header.tvBookPrice.text = "Biaya Cetak"
                    binding.tvTrxTypeValue.text = "Cetak Buku"
                    binding.tvParticipantAddress.text = bookTrx.address
                    binding.tvTrxIdValue.text = bookTrx.id
                    binding.tvTrxDateValue.text = bookTrx.bookInDate
                    binding.tvParticipantValue.text = bookTrx.printingShopName
                    binding.tvTotalBookValue.text =
                        getString(R.string.total_stock_qty, bookTrx.totalBookQty.toString())
                    binding.tvTotalKindValue.text =
                        getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvTotalMoneyValue.text = getString(
                        R.string.rp_price,
                        Formatter.addThousandSeparatorTextView(bookTrx.finalCost)
                    )

                    binding.footer.tvBookSubtotal.text =
                        Formatter.addThousandSeparatorTextView(bookTrx.totalCost)
                    binding.footer.tvBookQty.text = bookTrx.totalBookQty.toString()
                    binding.footer.tvBookTitleTrx.text =
                        getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvNumberAsWords.text = getString(
                        R.string.rupiah,
                        Formatter.convertNumberToWords(bookTrx.finalCost)
                    )

                    when (bookTrx.discountType) {
                        "percent" -> {
                            binding.footer.tvBookSubtotal.paintFlags =
                                binding.footer.tvBookSubtotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            binding.tvDiscountAmount.text =
                                Formatter.addThousandSeparatorTextView(bookTrx.discountAmount)
                            binding.tvTitleDiscount.text = getString(
                                R.string.discount_percent_view,
                                bookTrx.discountPercent.toString()
                            )
                            binding.tvFinalMoney.text =
                                Formatter.addThousandSeparatorTextView(bookTrx.finalCost)
                        }

                        "flat" -> {
                            binding.footer.tvBookSubtotal.paintFlags =
                                binding.footer.tvBookSubtotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            binding.tvDiscountAmount.text =
                                Formatter.addThousandSeparatorTextView(bookTrx.discountAmount)
                            binding.tvFinalMoney.text =
                                Formatter.addThousandSeparatorTextView(bookTrx.finalCost)
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
                    binding.tvTrxTypeValue.text = "Buku Masuk"
                    binding.tvParticipantAddress.text = bookTrx.address

                    binding.header.guideline.visibility = View.GONE
                    binding.header.guideline3.visibility = View.GONE

                    binding.tvTrxIdValue.text = bookTrx.id
                    binding.tvTrxDateValue.text = bookTrx.bookInDate
                    binding.tvParticipantValue.text = bookTrx.donorName
                    binding.tvTotalBookValue.text =
                        getString(R.string.total_stock_qty, bookTrx.totalBookQty.toString())
                    binding.tvTotalKindValue.text =
                        getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvTotalMoneyValue.visibility = View.GONE
                    binding.tvTotalMoney.visibility = View.GONE
                    binding.footer.materialDivider.visibility = View.GONE

                    binding.footer.tvBookSubtotal.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    binding.footer.tvBookSubtotal.text = "-"
                    binding.footer.tvBookSubtotalRp.visibility = View.GONE
                    binding.footer.tvBookQty.text = bookTrx.totalBookQty.toString()
                    binding.footer.tvBookTitleTrx.text =
                        getString(R.string.book_count, bookTrx.totalBookKind.toString())

                    binding.layoutDiscount.visibility = View.GONE
                    binding.tvNumberAsWords.visibility = View.GONE
                }

                is BookOutSellingTrx -> {
                    binding.btnInvoice.visibility = View.VISIBLE
                    setupRecyclerView(1)
                    adapter.submitList(bookTrx.books)
                    binding.header.tvBookPrice.text = "Harga Jual"
                    binding.tvTrxTypeValue.text = "Jual Buku"
                    binding.tvParticipantAddress.text = bookTrx.address

                    binding.tvTrxIdValue.text = bookTrx.id
                    binding.tvTrxDateValue.text = bookTrx.bookOutDate
                    binding.tvParticipantValue.text = bookTrx.buyerName
                    binding.tvTotalBookValue.text =
                        getString(R.string.total_stock_qty, bookTrx.totalBookQty.toString())
                    binding.tvTotalKindValue.text =
                        getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvTotalMoneyValue.text = getString(
                        R.string.rp_price,
                        Formatter.addThousandSeparatorTextView(bookTrx.finalPrice)
                    )

                    binding.footer.tvBookSubtotal.text =
                        Formatter.addThousandSeparatorTextView(bookTrx.totalPrice)
                    binding.footer.tvBookQty.text = bookTrx.totalBookQty.toString()
                    binding.footer.tvBookTitleTrx.text =
                        getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvNumberAsWords.text = getString(
                        R.string.rupiah,
                        Formatter.convertNumberToWords(bookTrx.finalPrice)
                    )

                    when (bookTrx.discountType) {
                        "percent" -> {
                            binding.footer.tvBookSubtotal.paintFlags =
                                binding.footer.tvBookSubtotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            binding.tvDiscountAmount.text =
                                Formatter.addThousandSeparatorTextView(bookTrx.discountAmount)
                            binding.tvTitleDiscount.text = getString(
                                R.string.discount_percent_view,
                                bookTrx.discountPercent.toString()
                            )
                            binding.tvFinalMoney.text =
                                Formatter.addThousandSeparatorTextView(bookTrx.finalPrice)
                        }

                        "flat" -> {
                            binding.footer.tvBookSubtotal.paintFlags =
                                binding.footer.tvBookSubtotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            binding.tvDiscountAmount.text =
                                Formatter.addThousandSeparatorTextView(bookTrx.discountAmount)
                            binding.tvFinalMoney.text =
                                Formatter.addThousandSeparatorTextView(bookTrx.finalPrice)
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
                    binding.tvTrxTypeValue.text = "Buku Keluar"
                    binding.tvParticipantAddress.text = bookTrx.address

                    binding.tvTrxIdValue.text = bookTrx.id
                    binding.tvTrxDateValue.text = bookTrx.bookOutDate
                    binding.tvParticipantValue.text = bookTrx.doneeName
                    binding.tvTotalBookValue.text =
                        getString(R.string.total_stock_qty, bookTrx.totalBookQty.toString())
                    binding.tvTotalKindValue.text =
                        getString(R.string.book_count, bookTrx.totalBookKind.toString())
                    binding.tvTotalMoneyValue.visibility = View.GONE
                    binding.tvTotalMoney.visibility = View.GONE

                    binding.footer.tvBookSubtotal.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    binding.footer.tvBookSubtotal.text = "-"
                    binding.footer.tvBookSubtotalRp.visibility = View.GONE
                    binding.footer.tvBookQty.text = bookTrx.totalBookQty.toString()
                    binding.footer.tvBookTitleTrx.text =
                        getString(R.string.book_count, bookTrx.totalBookKind.toString())
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
                binding.userCard.tvUserName.text = getString(
                    R.string.by_at,
                    user?.displayName,
                    user?.role,
                    Formatter.convertEpochToLocal(trxDetail?.logs?.createdAt)
                )
                binding.userCard.ivProfilePicture.load(user?.photoUrl)
            }
        }
    }

    private fun setupAction() {
        binding.btnInvoice.setOnClickListener {
            setLoading(true)
            viewModel.trxDetail.observe(this) { trxDetail ->
                val invId = trxDetail?.bookTrx?.id
                if (invId != null) {
                    viewModel.getInvoiceDownloadUrl(invId) {
                        if (it != null) {
                            openURLInBrowser(this, it)
                        } else {
                            generatePdfInvoice(this, invId)
                        }
                    }
                }
            }
        }
    }
    private fun openURLInBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            setLoading(false)
        } else {
            Log.e("Intent", "No activity found to handle the intent")
        }
    }

    private fun generatePdfInvoice(context: Context, invId: String?) {
        viewModel.trxDetail.observe(this) {
            if (it?.bookTrx is BookOutSellingTrx) {
                viewModel.getInvoiceNumber { invoiceNumber ->

                    val file = File(context.getExternalFilesDir(null), "${invId}.pdf")
                    val pdfWriter = PdfWriter(file.absolutePath)
                    val pdfDocument = PdfDocument(pdfWriter)
                    val document = Document(pdfDocument, PageSize.A4)
                    val iconBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon_only_white)
                    val stream = ByteArrayOutputStream()
                    iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val iconData = stream.toByteArray()
                    val imageData = ImageDataFactory.create(iconData)
                    val icon = Image(imageData).apply {
                        setFixedPosition(265f, 705f, 60f) // Position the icon appropriately
                    }

                    val invoiceNumberFull = Formatter.generateInvoiceNumber(invoiceNumber!! + 1, it.bookTrx.bookOutDate)
                    val header = Paragraph("INVOICE NO:\n")
                        .add(invoiceNumberFull)
                        .setFontSize(16f)
                        .setFontColor(ColorConstants.WHITE)
                        .setBackgroundColor(Formatter.hexToRgb("#1B263B"))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMargin(10f)
                        .setPadding(40f)

                    val companyDetails = Paragraph()
                        .add("Penerbit Peneleh\n")
                        .add("Permata Land A49, Malang\n")
                        .add("Jawa Timur, Indonesia\n")
                        .add("penerbitpeneleh@gmail.com")
                        .setFontSize(12f)
                        .setFontColor(ColorConstants.WHITE)
                        .setMargin(0f)
                        .setPadding(10f)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFixedPosition(325f, 695f, 200f)

                    // Add header to document
                    document.add(header)
                    document.add(companyDetails)
                    document.add(icon)

                    // Add invoice details
                    val invoiceDetails = Paragraph()
                        .add("Billed To:\n${it.bookTrx.buyerName}\n${it.bookTrx.address}\n\n")
                        .setFontSize(12f)
                        .setMargin(20f)

                    document.add(invoiceDetails)

                    // Add table
                    val table =
                        Table(UnitValue.createPercentArray(floatArrayOf(4f, 2f, 1f, 2f))).apply {
                            width = UnitValue.createPercentValue(100f)
                        }
                    table.addHeaderCell(
                        Cell().add(
                            Paragraph("Judul Buku").setFontColor(
                                ColorConstants.WHITE
                            ).setTextAlignment(TextAlignment.CENTER)
                        ).setBackgroundColor(Formatter.hexToRgb("#1B263B"))
                    )
                    table.addHeaderCell(
                        Cell().add(
                            Paragraph("Harga Satuan").setFontColor(
                                ColorConstants.WHITE
                            ).setTextAlignment(TextAlignment.CENTER)
                        ).setBackgroundColor(Formatter.hexToRgb("#1B263B"))
                    )
                    table.addHeaderCell(
                        Cell().add(
                            Paragraph("Quantity").setFontColor(ColorConstants.WHITE)
                                .setTextAlignment(TextAlignment.CENTER)
                        ).setBackgroundColor(Formatter.hexToRgb("#1B263B"))
                    )
                    table.addHeaderCell(
                        Cell().add(
                            Paragraph("Subtotal").setFontColor(ColorConstants.WHITE)
                                .setTextAlignment(TextAlignment.CENTER)
                        ).setBackgroundColor(Formatter.hexToRgb("#1B263B"))
                    )

                    for (book in it.bookTrx.books) {
                        table.addCell(book.bookTitle)
                        table.addCell(
                            Cell().add(
                                Paragraph(
                                    getString(
                                        R.string.rp_price,
                                        Formatter.addThousandSeparatorTextView(book.unitPrice)
                                    )
                                ).setTextAlignment(TextAlignment.CENTER)
                            )
                        )
                        table.addCell(
                            Cell().add(Paragraph(book.qty.toString()))
                                .setTextAlignment(TextAlignment.CENTER)
                        )
                        table.addCell(
                            Cell().add(
                                Paragraph(
                                    getString(
                                        R.string.rp_price,
                                        Formatter.addThousandSeparatorTextView(book.subtotal)
                                    )
                                ).setTextAlignment(TextAlignment.RIGHT)
                            )
                        )
                    }

                    table.addFooterCell(
                        Cell().add(
                            Paragraph(
                                getString(
                                    R.string.book_count,
                                    it.bookTrx.totalBookKind.toString()
                                )
                            ).setFontColor(ColorConstants.WHITE)
                                .setTextAlignment(TextAlignment.CENTER)
                        ).setBackgroundColor(Formatter.hexToRgb("#1B263B"))
                    )
                    table.addFooterCell(
                        Cell().add(
                            Paragraph("-").setFontColor(ColorConstants.WHITE)
                                .setTextAlignment(TextAlignment.CENTER)
                        ).setBackgroundColor(Formatter.hexToRgb("#1B263B"))
                    )
                    table.addFooterCell(
                        Cell().add(
                            Paragraph(
                                getString(
                                    R.string.total_stock_qty,
                                    it.bookTrx.totalBookQty.toString()
                                )
                            ).setFontColor(ColorConstants.WHITE)
                                .setTextAlignment(TextAlignment.CENTER)
                        ).setBackgroundColor(Formatter.hexToRgb("#1B263B"))
                    )
                    table.addFooterCell(
                        Cell().add(
                            Paragraph(
                                getString(
                                    R.string.rp_price,
                                    Formatter.addThousandSeparatorTextView(it.bookTrx.totalPrice)
                                )
                            ).setFontColor(ColorConstants.WHITE)
                                .setTextAlignment(TextAlignment.RIGHT)
                        ).setBackgroundColor(Formatter.hexToRgb("#1B263B"))
                    )

                    document.add(table)

                    // Add totals
                    val totals = Paragraph()
                        .add("\nDiscount: Rp. ${Formatter.addThousandSeparatorTextView(it.bookTrx.discountAmount)}\n")
                        .add("Total: Rp. ${Formatter.addThousandSeparatorTextView(it.bookTrx.finalPrice)}\n")
                        .setFontSize(12f)
                        .setTextAlignment(TextAlignment.RIGHT)

                    document.add(totals)

                    val wordedNumber = Paragraph()
                        .add(
                            getString(
                                R.string.rupiah,
                                Formatter.convertNumberToWords(it.bookTrx.finalPrice)
                            )
                        )
                        .setFontSize(12f)
                        .setTextAlignment(TextAlignment.CENTER)

                    document.add(wordedNumber)

                    document.close()

                    viewModel.saveInvoicePDF(file) { success ->
                        if (success == true) {
                            if (invId != null) {
                                viewModel.getInvoiceDownloadUrl(invId) { url ->
                                    if (url != null) {
                                        openURLInBrowser(this, url)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

    }

    private fun setupRecyclerView(type: Int) {
        adapter = BookTrxHistoryAdapter(this, type)
        binding.rvTrxHistory.layoutManager = LinearLayoutManager(this)
        binding.rvTrxHistory.adapter = adapter
        binding.rvTrxHistory.itemAnimator = DefaultItemAnimator()
    }

    private fun setLoading(status: Boolean) {
        if (status) {
            binding.loading.root.visibility = View.VISIBLE
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        } else {
            binding.loading.root.visibility = View.GONE
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
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
}