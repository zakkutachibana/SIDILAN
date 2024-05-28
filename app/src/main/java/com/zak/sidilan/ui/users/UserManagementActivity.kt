package com.zak.sidilan.ui.users

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityUserManagementBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

val userListActivityModule = module {
    factory { UserManagementActivity() }
}
class UserManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserManagementBinding

    private lateinit var adapter: UserManagementPagerAdapter
    private val viewModel: UserManagementViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserManagementPagerAdapter(supportFragmentManager, lifecycle)

        setupView()
        setupAction()
    }

    private fun setupView() {
        binding.viewPager.adapter = adapter
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    if (tab.position == 0) {
                        binding.fab.hide()
                    } else {
                        binding.fab.show()
                    }
                    binding.viewPager.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabs.selectTab(binding.tabs.getTabAt(position))
                if (position == 0) {
                    binding.fab.hide()
                } else {
                    binding.fab.show()
                }
            }
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Manajemen Pengguna"
        supportActionBar?.elevation = 0f
    }

    private fun setupAction() {
        binding.fab.setOnClickListener {
            val layout = LayoutInflater.from(this).inflate(R.layout.layout_add_whitelist, null)
            val edEmail = layout.findViewById<EditText>(R.id.ed_email)
            val edlEmail = layout.findViewById<TextInputLayout>(R.id.edl_email)
            val edPhone = layout.findViewById<EditText>(R.id.ed_phone_number)
            val edlPhone = layout.findViewById<TextInputLayout>(R.id.edl_phone_number)
            val edRole = layout.findViewById<EditText>(R.id.ed_role)
            val edlRole = layout.findViewById<TextInputLayout>(R.id.edl_role)
            val dialog = MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setTitle(R.string.add_whitelist)
                .setView(layout)
                .setIcon(R.drawable.ic_delete)
                .setMessage(R.string.add_whitelist_action)
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Ya", null)
                .show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                when {
                    edEmail.text.isEmpty() -> edlEmail.error = "Email tidak boleh kosong!"
                    edPhone.text.isEmpty() -> edlPhone.error = "Nomor Telepon tidak boleh kosong!"
                    edRole.text.isEmpty() -> edlRole.error = "Role tidak boleh kosong!"
                    else -> {
                        val email = edEmail.text.toString()
                        val role = edRole.text.toString()
                        val phoneNumber = edPhone.text.toString()
                        viewModel.addWhitelist(email, role, phoneNumber)
                        dialog.dismiss()
                    }
                }
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

}