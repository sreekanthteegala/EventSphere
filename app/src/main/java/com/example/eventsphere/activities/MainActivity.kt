package com.example.eventsphere.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventsphere.adapters.EventAdapter
import com.example.eventsphere.databinding.ActivityMainBinding
import com.example.eventsphere.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val eventList = mutableListOf<Event>()
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()

        adapter = EventAdapter(eventList) { event ->
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("eventId", event.id)
            startActivity(intent)
        }
        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = adapter

        loadEvents()

        binding.fabCreate.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Quick theme toggle from the header icon
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

        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.etSearch.text.toString().trim()
            searchEvents(query)
            true
        }
    }

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

    private fun loadEvents() {
        db.collection("events")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                snap ?: return@addSnapshotListener
                eventList.clear()
                for (doc in snap) {
                    val event = doc.toObject(Event::class.java).copy(id = doc.id)
                    eventList.add(event)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun searchEvents(query: String) {
        if (query.isEmpty()) { loadEvents(); return }
        val filtered = eventList.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.location.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }
        adapter.updateList(filtered)
    }
}
