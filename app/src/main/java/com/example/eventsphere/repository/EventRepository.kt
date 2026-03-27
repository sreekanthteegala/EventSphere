package com.example.eventsphere.repository

import com.example.eventsphere.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class EventRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getEvents(onResult: (List<Event>) -> Unit) {
        db.collection("events")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull {
                    it.toObject(Event::class.java)?.copy(id = it.id)
                } ?: emptyList()

                onResult(list)
            }
    }

    fun searchEvents(query: String, onResult: (List<Event>) -> Unit) {
        db.collection("events")
            .orderBy("title")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull {
                    it.toObject(Event::class.java)?.copy(id = it.id)
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList()) // fallback
            }
    }
}