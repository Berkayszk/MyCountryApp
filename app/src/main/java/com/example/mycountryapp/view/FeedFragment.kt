package com.example.mycountryapp.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycountryapp.R
import com.example.mycountryapp.adapter.CountryAdapter
import com.example.mycountryapp.viewmodel.FeedViewModel
import kotlinx.android.synthetic.main.fragment_feed.*

class FeedFragment : Fragment() {
    private lateinit var viewModel : FeedViewModel
    private val countryAdapter = CountryAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        viewModel.refreshData()

        countryList.layoutManager = LinearLayoutManager(context)
        countryList.adapter = countryAdapter

        swipeRefleshLayout.setOnRefreshListener {
            countryList.visibility = View.GONE
            countryLoading.visibility = View.VISIBLE
            countryerror.visibility = View.GONE
            viewModel.refreshData()
            swipeRefleshLayout.isRefreshing = false
        }

        observeLiveData()
    }
    private fun observeLiveData() {
        viewModel.countries.observe(viewLifecycleOwner, Observer { countries->
            countries?.let {
                countryList.visibility=View.VISIBLE
                countryAdapter.updateCountryList(countries)
            }
        })
        viewModel.countryError.observe(viewLifecycleOwner, Observer { error->
            error?.let {
                if(it) {
                    countryerror.visibility = View.VISIBLE
                } else {
                    countryerror.visibility = View.GONE
                }
            }
        })
        viewModel.countryLoading.observe(viewLifecycleOwner, Observer { loading->
            loading?.let {
                if (it) {
                    countryLoading.visibility = View.VISIBLE
                    countryList.visibility = View.GONE
                    countryerror.visibility = View.GONE
                } else {
                    countryLoading.visibility = View.GONE
                }
            }
        })

    }

}