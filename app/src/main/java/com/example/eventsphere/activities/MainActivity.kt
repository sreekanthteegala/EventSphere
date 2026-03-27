package com.example.eventsphere.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventsphere.adapters.EventAdapter
import com.example.eventsphere.databinding.ActivityMainBinding
import com.example.eventsphere.viewmodel.EventViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: EventAdapter

    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()

        // RecyclerView setup
        adapter = EventAdapter(mutableListOf()) { event ->
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("eventId", event.id)
            startActivity(intent)
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = adapter

        // 🔥 Observe data from ViewModel
        viewModel.events.observe(this) { list ->
            adapter.updateList(list)
        }

        // 🔥 Load events
        viewModel.loadEvents()

        // 🔍 Real-time search
        binding.etSearch.addTextChangedListener {
            val query = it.toString().trim()
            viewModel.searchEvents(query)
        }

        // Create Event
        binding.fabCreate.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        // Profile
        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Theme toggle
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        binding.ivThemeToggle.setOnClickListener {
            val isDark = prefs.getBoolean("dark_mode", true)
            val newMode = !isDark
            prefs.edit().putBoolean("dark_mode", newMode).apply()

            AppCompatDelegate.setDefaultNightMode(
                if (newMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            recreate()
        }
    }

    // 🔔 Notification Permission
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }
}