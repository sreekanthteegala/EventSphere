package com.example.eventsphere.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventsphere.databinding.ActivityCreateEventBinding
import com.example.eventsphere.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventBinding
    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val categories = listOf("General", "Music", "Tech", "Sports", "Art", "Food", "Business", "Education")
        binding.spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                binding.etDate.setText("$d/${m + 1}/$y")
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.etTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, h, min ->
                binding.etTime.setText(String.format("%02d:%02d", h, min))
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        binding.btnCreate.setOnClickListener { publishEvent() }
    }

    private fun publishEvent() {
        val title    = binding.etTitle.text.toString().trim()
        val desc     = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val date     = binding.etDate.text.toString().trim()
        val time     = binding.etTime.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()

        if (title.isEmpty() || desc.isEmpty() || location.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                val organizerName = userDoc.getString("name") ?: "Unknown"

                val event = Event(
                    title = title,
                    description = desc,
                    location = location,
                    date = date,
                    time = time,
                    category = category,
                    imageUrl = "",
                    organizerId = uid,
                    organizerName = organizerName
                )

                val docRef = db.collection("events").document()
                val eventWithId = event.copy(id = docRef.id)

                docRef.set(eventWithId)
                    .addOnSuccessListener {
                        showLoading(false)
                        Toast.makeText(this, "Event published! 🎉", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        showLoading(false)
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnCreate.isEnabled = !show
    }
}