package com.example.eventsphere.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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

        // RecyclerView setup
        adapter = EventAdapter(eventList) { event ->
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("eventId", event.id)
            startActivity(intent)
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = adapter

        loadEvents()

        // Create Event button
        binding.fabCreate.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        // Profile button
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

        // 🔥 REAL-TIME SEARCH
        binding.etSearch.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            searchEvents(query)
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

    // 📥 Load all events
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

    // 🔍 Firestore Search
    private fun searchEvents(query: String) {
        if (query.isEmpty()) {
            loadEvents()
            return
        }

        db.collection("events")
            .orderBy("title", Query.Direction.ASCENDING)
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                }

                if (list.isEmpty()) {
                    Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show()
                }

                adapter.updateList(list)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Search failed. Check internet.", Toast.LENGTH_SHORT).show()
            }
    }
}