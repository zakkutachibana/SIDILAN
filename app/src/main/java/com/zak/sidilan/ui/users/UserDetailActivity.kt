package com.zak.sidilan.ui.users

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.zak.sidilan.databinding.ActivityUserDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module


val userDetailActivityModule = module {
    factory { UserDetailActivity() }
}

class UserDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private val viewModel: UserDetailViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("userId")
        if (userId != null) {
            viewModel.getUserById(userId)
        }

        setupView()
        setupViewModel()
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail Pengguna"
    }

    private fun setupViewModel() {
        viewModel.user.observe(this) { user ->
            binding.tvUserName.text = user?.displayName
            binding.tvDisplayName.text = user?.displayName
            binding.tvUserEmail.text = user?.email
            binding.ivProfilePicture.load(user?.photoUrl)
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