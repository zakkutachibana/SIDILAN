package com.zak.sidilan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.zak.sidilan.databinding.ActivityMainBinding
import com.zak.sidilan.ui.auth.AuthActivity
import com.zak.sidilan.ui.executivemenus.ExecutiveMenusActivity
import com.zak.sidilan.ui.trx.bookin.BookInTrxActivity
import com.zak.sidilan.ui.trx.bookout.BookOutTrxActivity
import com.zak.sidilan.ui.bottomsheets.ModalBottomSheetAction
import com.zak.sidilan.ui.users.UserListActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_SIDILAN)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_books, R.id.navigation_dashboard, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_books -> binding.fab.show()
                R.id.navigation_dashboard -> Handler(Looper.getMainLooper()).postDelayed({
                    binding.fab.hide()
                }, 400)

                R.id.navigation_profile -> Handler(Looper.getMainLooper()).postDelayed({
                    binding.fab.hide()
                }, 200)

                else -> {}
            }

        }
        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            // Not signed in, launch the Login activity
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        setView()
        setAction()
    }


    private fun setAction() {
        binding.fab.setOnClickListener {
            val modalBottomSheetAction = ModalBottomSheetAction(1, null, this)
            modalBottomSheetAction.show(supportFragmentManager, ModalBottomSheetAction.TAG)
        }
    }


    private fun setView() {

        binding.searchBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        binding.navigationView.setCheckedItem(R.id.home)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.book_in_item -> {
                    val intent = Intent(this, BookInTrxActivity::class.java)
                    startActivity(intent)
                }

                R.id.book_out_item -> {
                    val intent = Intent(this, BookOutTrxActivity::class.java)
                    startActivity(intent)
                }

                R.id.stock_opname_item -> {
                    Toast.makeText(this, "stock_opname_item", Toast.LENGTH_SHORT).show()
                }

                R.id.book_in_trx_item -> Toast.makeText(
                    this,
                    "book_in_trx_item",
                    Toast.LENGTH_SHORT
                ).show()

                R.id.book_out_trx_item -> Toast.makeText(
                    this,
                    "book_out_trx_item",
                    Toast.LENGTH_SHORT
                ).show()

                R.id.stock_opname_trx_item -> Toast.makeText(
                    this,
                    "stock_opname_trx_item",
                    Toast.LENGTH_SHORT
                ).show()

                R.id.executive_charts -> {
                    val intent = Intent(this, ExecutiveMenusActivity::class.java)
                    startActivity(intent)
                }

                R.id.user_management -> {
                    val intent = Intent(this, UserListActivity::class.java)
                    startActivity(intent)
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
                binding.drawerLayout.close()
            }, 300)
            true
        }
    }
}