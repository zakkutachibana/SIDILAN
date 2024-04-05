package com.zak.sidilan.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.zak.sidilan.R
import com.zak.sidilan.databinding.FragmentProfileBinding
import com.zak.sidilan.ui.addbook.AddBookActivity
import com.zak.sidilan.ui.auth.AuthActivity
import com.zak.sidilan.util.AuthManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        setupView()
        setupAction()
        return binding.root
    }

    private fun setupView() {
        val currentUser = AuthManager.getCurrentUser()
        binding.tvProfileName.text = currentUser.displayName
        binding.tvEmail.text = currentUser.email
        binding.ivProfilePicture.load(currentUser.photoUrl)
    }


    private fun setupAction() {
        binding.btnLogOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(resources.getString(R.string.title_log_out))
                .setMessage(resources.getString(R.string.message_log_out))
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    AuthManager.setCurrentUser(null)
                    signOut()
                }
                .show()
        }
        }
    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
        AuthManager.setCurrentUser(null)
        val intent = Intent(context, AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}