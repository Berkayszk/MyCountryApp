package com.example.mycountryapp.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.mycountryapp.model.Country
import com.example.mycountryapp.service.CountryAPIService
import com.example.mycountryapp.service.CountryDatabase
import com.example.mycountryapp.util.CustomSharedPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : BaseViewModel(application) {
    private var countryApiService = CountryAPIService()
    private var disposable = CompositeDisposable()
    private var customPreferences = CustomSharedPreferences(getApplication())
    private var refleshTime = 10* 10 * 1000* 1000 *1000L

    val countries = MutableLiveData<List<Country>>()
    val countryError = MutableLiveData<Boolean>()
    val countryLoading = MutableLiveData<Boolean>()

    fun refreshData() {
        val updateTime= customPreferences.getTime()
        if (updateTime!=null && updateTime !=0L && System.nanoTime() - updateTime < refleshTime){
            getDataFromSQLite()
        }
        else{
            getDataFromAPI()
        }
    }
   fun refleshDataFromAPI(){
       getDataFromAPI()
   }
    private fun getDataFromSQLite(){
        countryLoading.value = true
        launch {

            val countries = CountryDatabase(getApplication()).countryDao().getAllCountries()
            showCountries(countries)
            Toast.makeText(getApplication(), "Countries From SQLite", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getDataFromAPI(){
        countryLoading.value = true

        disposable.add(
            countryApiService.getData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Country>>(){
                    override fun onSuccess(t: List<Country>) {
                        storeinSQLite(t)
                        Toast.makeText(getApplication(), "Countries From API", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {

                        countryLoading.value = false
                        countryError.value = true
                        e.printStackTrace()
                    }

                })
        )
    }
    private fun showCountries(countryList: List<Country>){
        countries.value = countryList
        countryError.value = false
        countryLoading.value = false
    }
    private fun storeinSQLite(list : List<Country>){
        launch {
            val dao = CountryDatabase(getApplication()).countryDao()
            dao.deleteAllCountries()
            val listLong =  dao.insertAll(*list.toTypedArray()) // --> list --> individual
            var i = 0
            while (i < list.size){
                list[i].uuid = listLong[i].toInt()
                i =i + 1
            }
            showCountries(list)
        }
        customPreferences.saveTime(System.nanoTime())
    }
}