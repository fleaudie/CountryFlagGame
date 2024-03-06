package com.fleaudie.countrygame.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fleaudie.countrygame.model.Country
import com.fleaudie.countrygame.service.FlagAPIService
import kotlinx.coroutines.launch
import kotlin.random.Random

class CountryViewModel(application: Application) : AndroidViewModel(application) {

    private val _countriesLiveData = MutableLiveData<List<Pair<String, String>>>()
    val countriesLiveData: LiveData<List<Pair<String, String>>> get() = _countriesLiveData

    private var currentCountryIndex = -1

    init {
        viewModelScope.launch {
            fetchCountries()
        }
    }

    private suspend fun fetchCountries() {
        try {
            val response = FlagAPIService.countryApiService.getIndependentCountries()

            if (response.isSuccessful) {
                val countries: List<Country>? = response.body()

                countries?.let {
                    if (it.isNotEmpty()) {
                        val countryList = mutableListOf<Pair<String, String>>()

                        for (country in it) {
                            val translations = country.translations
                            val countryName = translations?.get("tur")?.get("common")
                            val flagUrl = country.flags["png"]

                            if (!countryName.isNullOrEmpty() && !flagUrl.isNullOrEmpty()) {
                                countryList.add(Pair(countryName, flagUrl))
                            } else {
                                Log.e("CountryViewModel", "Country name or flag URL is null or empty: countryName=$countryName, flagUrl=$flagUrl")
                            }
                        }

                        _countriesLiveData.postValue(countryList)
                        updateCurrentCountry()
                    } else {
                        Log.e("CountryViewModel", "Empty countries list")
                    }
                }
            } else {
                Log.e("CountryViewModel", "Error: ${response.message()}")
            }
        } catch (e: Exception) {
            // Handle other exceptions
            Log.e("CountryViewModel", "Error fetching countries: ${e.message}")
        }
    }

    fun updateCurrentCountry() {
        currentCountryIndex = if (_countriesLiveData.value.isNullOrEmpty()) {
            -1
        } else {
            Random.nextInt(0, _countriesLiveData.value!!.size)
        }
    }

    fun getCurrentCountry(): Pair<String, String>? {
        return if (currentCountryIndex != -1) {
            _countriesLiveData.value?.get(currentCountryIndex)
        } else {
            null
        }
    }
}
