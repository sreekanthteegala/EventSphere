package com.example.eventsphere.activities


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventsphere.databinding.ActivityEventDetailBinding
import com.example.eventsphere.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var eventId = ""
    private var hasRsvp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        eventId = intent.getStringExtra("eventId") ?: run { finish(); return }

        loadEvent()

        binding.btnRsvp.setOnClickListener { toggleRsvp() }
    }

    private fun loadEvent() {
        db.collection("events").document(eventId).addSnapshotListener { snap, _ ->
            snap ?: return@addSnapshotListener
            val event = snap.toObject(Event::class.java) ?: return@addSnapshotListener
            val uid = auth.currentUser?.uid

            hasRsvp = uid != null && event.rsvpList.contains(uid)

            binding.tvTitle.text       = event.title
            binding.tvCategory.text    = event.category
            binding.tvDateTime.text    = "📅 ${event.date} • ${event.time}"
            binding.tvLocation.text    = "📍 ${event.location}"
            binding.tvOrganizer.text   = "👤 Hosted by ${event.organizerName}"
            binding.tvDescription.text = event.description
            binding.tvRsvpCount.text   = "👥 ${event.rsvpList.size} people going"

            binding.btnRsvp.text = if (hasRsvp) "✅ You're Going!" else "RSVP — I'm Going! 🎉"

            if (event.imageUrl.isNotEmpty()) {
                Glide.with(this).load(event.imageUrl).into(binding.ivEventImage)
            }
        }
    }

    private fun toggleRsvp() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val ref = db.collection("events").document(eventId)

        if (hasRsvp) {
            ref.update("rsvpList", FieldValue.arrayRemove(uid))
                .addOnSuccessListener { Toast.makeText(this, "RSVP cancelled", Toast.LENGTH_SHORT).show() }
        } else {
            ref.update("rsvpList", FieldValue.arrayUnion(uid))
                .addOnSuccessListener { Toast.makeText(this, "You're going! 🎉", Toast.LENGTH_SHORT).show() }
        }
    }
}