package com.zak.sidilan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.data.entities.Whitelist
import com.zak.sidilan.databinding.ActivityMainBinding
import com.zak.sidilan.ui.about.AboutActivity
import com.zak.sidilan.ui.auth.AuthActivity
import com.zak.sidilan.ui.auth.AuthViewModel
import com.zak.sidilan.ui.executivemenus.ExecutiveMenusActivity
import com.zak.sidilan.ui.stockopname.StockOpnameActivity
import com.zak.sidilan.ui.stockopnamehistory.StockOpnameHistoryActivity
import com.zak.sidilan.ui.trx.bookin.BookInTrxActivity
import com.zak.sidilan.ui.trx.bookout.BookOutTrxActivity
import com.zak.sidilan.ui.trxhistory.TrxHistoryActivity
import com.zak.sidilan.ui.users.UserManagementActivity
import com.zak.sidilan.util.HawkManager
import com.zak.sidilan.util.HelperFunction
import com.zak.sidilan.util.UserRole
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var hawkManager: HawkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_SIDILAN)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hawkManager = HawkManager(this)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_books, R.id.navigation_dashboard, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setAction()
        setViewModel()
    }

    private fun setViewModel() {
        val userId = hawkManager.retrieveData<User>("user")?.id.toString()
        authViewModel.getCurrentUserOnce(userId)
        authViewModel.currentUser.observe(this) { user ->
            val userRole = HelperFunction.parseUserRole(user?.role)
            updateUIVisibility(userRole)
        }
    }

    private fun setAction() {
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
                    val intent = Intent(this, StockOpnameActivity::class.java)
                    startActivity(intent)
                }

                R.id.book_trx_history_item -> {
                    val intent = Intent(this, TrxHistoryActivity::class.java)
                    startActivity(intent)
                }

                R.id.stock_opname_history_item -> {
                    val intent = Intent(this, StockOpnameHistoryActivity::class.java)
                    startActivity(intent)                }

                R.id.executive_charts -> {
                    val intent = Intent(this, ExecutiveMenusActivity::class.java)
                    startActivity(intent)
                }

                R.id.user_management -> {
                    val intent = Intent(this, UserManagementActivity::class.java)
                    startActivity(intent)
                }

                R.id.about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
                binding.drawerLayout.close()
            }, 300)
            true
        }
    }

    private fun updateUIVisibility(userRole: UserRole?) {
        when (userRole) {
            null -> { signOut() }
            UserRole.ADMIN -> {
                binding.navigationView.menu.findItem(R.id.access_items).isVisible = true
                binding.navigationView.menu.findItem(R.id.book_in_item).isVisible = true
                binding.navigationView.menu.findItem(R.id.book_out_item).isVisible = true
                binding.navigationView.menu.findItem(R.id.stock_opname_item).isVisible = true
            }
            UserRole.DIRECTOR -> {
                binding.navigationView.menu.findItem(R.id.access_items).isVisible = true
                binding.navigationView.menu.findItem(R.id.book_in_item).isVisible = false
                binding.navigationView.menu.findItem(R.id.book_out_item).isVisible = false
                binding.navigationView.menu.findItem(R.id.stock_opname_item).isVisible = false
            }
            else -> {
                binding.navigationView.menu.findItem(R.id.access_items).isVisible = false
                binding.navigationView.menu.findItem(R.id.book_in_item).isVisible = true
                binding.navigationView.menu.findItem(R.id.book_out_item).isVisible = true
                binding.navigationView.menu.findItem(R.id.stock_opname_item).isVisible = true
            }
        }
    }

    private fun isEmailWhitelisted(email: String, callback: (Boolean) -> Unit) {
        authViewModel.validateWhitelist(email) { isWhitelisted ->
            callback(isWhitelisted)
        }
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
        hawkManager.deleteData("user")

        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            signOut()
            return
        } else {
            isEmailWhitelisted(firebaseUser.email.toString()) { isWhitelisted ->
                if (!isWhitelisted) {
                    signOut()
                }
            }
        }
    }
}