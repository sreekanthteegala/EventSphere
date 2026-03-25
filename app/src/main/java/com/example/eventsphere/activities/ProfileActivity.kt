package com.example.eventsphere.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.eventsphere.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        loadProfile()

        binding.tvChangePic.visibility = android.view.View.GONE

        // -- Theme toggle --
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", true)
        binding.switchTheme.isChecked = isDark

        binding.switchTheme.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("dark_mode", checked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            // Recreate so the whole app re-applies the theme
            recreate()
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            binding.tvName.text  = doc.getString("name") ?: ""
            binding.tvEmail.text = doc.getString("email") ?: ""
        }

        db.collection("events").whereEqualTo("organizerId", uid).get()
            .addOnSuccessListener { binding.tvEventsCreated.text = it.size().toString() }

        db.collection("events").whereArrayContains("rsvpList", uid).get()
            .addOnSuccessListener { binding.tvEventsJoined.text = it.size().toString() }
    }
}