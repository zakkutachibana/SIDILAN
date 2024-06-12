package com.zak.sidilan.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.zak.sidilan.MainActivity
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.ActivityAuthBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.zak.sidilan.util.HawkManager
import org.koin.dsl.module
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

val authActivityModule = module {
    factory { AuthActivity() }
}
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var hawkManager: HawkManager
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hawkManager = HawkManager(this)
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        setupView()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.hide()
        viewModel.toastMessage.observe(this) {
            MotionToast.createColorToast(this,
                "Info",
                it,
                MotionToastStyle.INFO,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))

        }
    }

    private fun setupAction() {
        binding.btnSignIn.setOnClickListener {
            signIn()
        }
        binding.tvUnableToLogin.setOnClickListener {
            MaterialAlertDialogBuilder(
                this,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setTitle("Tidak Bisa Login")
                .setIcon(R.drawable.ic_no_account)
                .setMessage("Aplikasi SIDILAN merupakan aplikasi internal Penerbit Peneleh. Untuk mendapatkan akses aplikasi, mohon hubungi Direktur Penerbit Peneleh untuk menambahkan akun Google anda.")
                .setPositiveButton("Ok") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.reload()?.addOnCompleteListener { reloadTask ->
                        if (reloadTask.isSuccessful) {
                            val refreshedUser = auth.currentUser
                            val userEmail = refreshedUser?.email.toString()
                            checkWhitelisting(userEmail)
                        } else {
                            // Reload failed, handle the error
                            updateUI(null)
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    updateUI(null)
                }
            }
    }

    private fun checkWhitelisting(email: String) {
        viewModel.isEmailWhitelisted(email) { isWhitelisted, additionalInfo ->
            if (isWhitelisted) {
                MotionToast.createColorToast(this,
                    "Success",
                    "Anda masuk dalam Whitelist",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    1000L,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                val currentUser = auth.currentUser
                val user = User(currentUser?.uid.toString(), currentUser?.displayName, additionalInfo?.role, currentUser?.email,
                    currentUser?.photoUrl.toString().replace("s96-c", "s300-c"), additionalInfo?.phoneNumber, "joinedAt")
                viewModel.saveUserToFirebase(
                    user.id,
                    user.displayName.toString(),
                    user.email.toString(),
                    user.photoUrl.toString().replace("s96-c", "s300-c"),
                    user.role.toString(),
                    user.phoneNumber.toString()
                    )
                hawkManager.saveData("user", user)
                updateUI(currentUser)
            } else {
                // Not whitelisted
                MotionToast.createColorToast(this,
                    "Error",
                    "Anda tidak masuk dalam Whitelist",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    1000L,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))

                hawkManager.deleteData("user")
                googleSignInClient.signOut()
                auth.signOut()
                updateUI(null)
            }
        }
    }
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        currentUser?.reload()?.addOnCompleteListener { reloadTask ->
            if (reloadTask.isSuccessful) {
                val refreshedUser = auth.currentUser
                updateUI(refreshedUser)
            } else {
                updateUI(null)
            }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }


}