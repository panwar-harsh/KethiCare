package com.example.greendoc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WaterCalculatorViewModel : ViewModel() {

    // 🌿 Plant Location (Step 1)
    private val _location = MutableLiveData<String>()
    val location: LiveData<String> get() = _location
    fun setLocation(value: String) {
        _location.value = value
    }

    // 💧 Humidity (Step 2)
    private val _humidity = MutableLiveData<Int>()
    val humidity: LiveData<Int> get() = _humidity
    fun setHumidity(value: Int) {
        _humidity.value = value
    }

    private val _temperature = MutableLiveData<Int>()
    val temperature: LiveData<Int> get() = _temperature

    fun setTemperature(value: Int) {
        _temperature.value = value
    }

    // You can add more like temperature and pot volume in the same way:

    // 🪴 Pot Volume
}
