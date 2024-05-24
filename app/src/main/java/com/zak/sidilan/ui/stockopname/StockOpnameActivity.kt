package com.zak.sidilan.ui.stockopname

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityStockOpnameBinding

class StockOpnameActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStockOpnameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockOpnameBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}