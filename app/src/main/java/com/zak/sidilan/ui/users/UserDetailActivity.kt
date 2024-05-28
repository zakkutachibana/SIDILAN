package com.zak.sidilan.ui.users

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.ActivityUserDetailBinding
import com.zak.sidilan.util.Formatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module


val userDetailActivityModule = module {
    factory { UserDetailActivity() }
}

class UserDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private val viewModel: UserDetailViewModel by viewModel()
    private var userInfo : User? = null
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
        setupAction()
    }

    private fun setupAction() {
        binding.btnEmail.setOnClickListener {
            intentEmail(userInfo?.email.toString())
            Toast.makeText(this, "email= ${userInfo?.email}", Toast.LENGTH_SHORT).show()
        }
        binding.btnWhatsapp.setOnClickListener {
            intentWhatsApp(userInfo?.phoneNumber.toString())
        }
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail Pengguna"

        binding.itemRole.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_role, null)
        binding.itemRole.tvItemTitle.text = "Role"
        binding.itemEmail.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_email, null)
        binding.itemEmail.tvItemTitle.text = "Email"
        binding.itemPhoneNumber.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_phone, null)
        binding.itemPhoneNumber.tvItemTitle.text = "Nomor Telepon"
        binding.itemJoinTime.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_account_time, null)
        binding.itemJoinTime.tvItemTitle.text = "Bergabung pada"
    }

    private fun setupViewModel() {
        viewModel.user.observe(this) { user ->
            userInfo = user
            binding.ivProfilePicture.load(user?.photoUrl)
            binding.tvDisplayName.text = user?.displayName
            binding.itemRole.tvItemValue.text = user?.role
            binding.itemEmail.tvItemValue.text = user?.email
            binding.itemPhoneNumber.tvItemValue.text = user?.phoneNumber
            binding.itemJoinTime.tvItemValue.text = Formatter.convertEpochToLocal(user?.joinedAt)
        }
    }

    private fun intentEmail(email: String) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SIDILAN")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "SIDILAN")
        startActivity(Intent.createChooser(emailIntent, "Send Email using:"))
    }

    private fun intentWhatsApp(localPhoneNumber: String) {
        val internationalPhoneNumber = Formatter.convertToInternationalFormat(localPhoneNumber)
        val defaultMessage = "Halo ${userInfo?.displayName}"
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$internationalPhoneNumber&text=$defaultMessage")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
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