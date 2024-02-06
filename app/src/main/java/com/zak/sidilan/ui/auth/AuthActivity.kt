package com.zak.sidilan.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zak.sidilan.MainActivity
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityAuthBinding
import com.zak.sidilan.databinding.FragmentLoginBinding
import com.zak.sidilan.ui.addbook.AddBookActivity

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)

        setupView()
        setupAction()

        setContentView(binding.root)
    }

    private fun setupView() {
        supportActionBar?.hide()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .commit()
        binding.toggleButton.check(binding.btnLog.id)
    }

    private fun setupAction() {
        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_log -> {
                        replaceFragment(LoginFragment())
                    }
                    R.id.btn_reg -> replaceFragment(RegisterFragment())
                }
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}