package com.example.eventsphere.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventsphere.models.Event
import com.example.eventsphere.repository.EventRepository

class EventViewModel : ViewModel() {

    private val repository = EventRepository()

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    fun loadEvents() {
        repository.getEvents {
            _events.postValue(it)
        }
    }

    fun searchEvents(query: String) {
        if (query.isEmpty()) {
            loadEvents()
        } else {
            repository.searchEvents(query) {
                _events.postValue(it)
            }
        }
    }
}