package com.example.mycountryapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mycountryapp.model.Country

class CountryViewModel : ViewModel() {
    val countryLiveData = MutableLiveData<Country>()

    fun getDataFromRoom(){
        val country = Country("Turkey","Asia","Ankara","TRY","Turkish","www.ss.com")
        countryLiveData.value = country
    }
}