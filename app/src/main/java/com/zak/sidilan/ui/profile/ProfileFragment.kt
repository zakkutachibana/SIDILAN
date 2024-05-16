package com.zak.sidilan.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.zak.sidilan.MainActivity
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.FragmentProfileBinding
import com.zak.sidilan.ui.auth.AuthActivity
import com.zak.sidilan.util.HawkManager

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var hawkManager: HawkManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        hawkManager = HawkManager(requireActivity())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        setupView()
        setupAction()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupView() {
        val currentUser = hawkManager.retrieveData<User>("user")
        binding.tvProfileName.text = currentUser?.displayName
        binding.tvEmail.text = currentUser?.email
        binding.tvRole.text = currentUser?.role
        binding.ivProfilePicture.load(currentUser?.photoUrl)
        (requireActivity() as MainActivity).binding.fab.hide()
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
                    signOut()
                }
                .show()
        }
        binding.drawerIcon.setOnClickListener {
            (requireActivity() as MainActivity).binding.drawerLayout.open()
        }
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
        hawkManager.deleteData("user")
        val intent = Intent(context, AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}