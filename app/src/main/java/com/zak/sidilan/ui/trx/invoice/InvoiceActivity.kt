package com.zak.sidilan.ui.trx.invoice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityInvoiceBinding

class InvoiceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityInvoiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}