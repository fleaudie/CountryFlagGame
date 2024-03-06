package com.fleaudie.countrygame.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fleaudie.countrygame.R
import com.fleaudie.countrygame.databinding.FragmentFirstBinding
import com.fleaudie.countrygame.viewmodel.CountryViewModel


class FirstFragment : Fragment(R.layout.fragment_first) {
    private lateinit var countryViewModel: CountryViewModel
    private lateinit var flagImageView: ImageView
    private lateinit var countryNameTextView: TextView
    private lateinit var changeCountryButton: Button
    private lateinit var guessCountryNameText: EditText
    private lateinit var scoreTextView: TextView
    private var binding: FragmentFirstBinding? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var userScore: Int = 0
    private lateinit var skipCountryButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        flagImageView = binding?.flagImageView!!
        countryNameTextView = binding?.countryNameTextView!!
        changeCountryButton = binding?.changeCountryButton!!
        guessCountryNameText = binding?.guessCountryName!!
        scoreTextView = binding?.scoreTextView!!
        skipCountryButton = binding?.skipButton!!

        userScore = sharedPreferences.getInt("score", 0)
        updateScore()

        countryViewModel = ViewModelProvider(this)[CountryViewModel::class.java]

        countryViewModel.countriesLiveData.observe(viewLifecycleOwner) { (countryName, flagUrl) ->
            Log.d("CountryFragment", "Country Name: $countryName, Flag URL: $flagUrl")
        }

        countryViewModel.countriesLiveData.observe(viewLifecycleOwner) { countries ->
            if (!countries.isNullOrEmpty()) {
                countryViewModel.updateCurrentCountry()
                updateUI()
            }
        }

        skipCountryButton.setOnClickListener {
            countryViewModel.updateCurrentCountry()
            updateUI()
        }

        changeCountryButton.setOnClickListener {
            val userInput = guessCountryNameText.text.toString().trim()

            if (userInput.equals(countryNameTextView.text.toString(), ignoreCase = true)) {
                userScore += 10
                updateScore()
                Toast.makeText(context, "Doğru! +10 Puan", Toast.LENGTH_SHORT).show()
                guessCountryNameText.text.clear()
                countryViewModel.updateCurrentCountry()
                updateUI()
            } else {
                userScore = 0
                updateScore()
                Toast.makeText(context, "Yanlış! Puanınız sıfırlandı.", Toast.LENGTH_SHORT).show()
                guessCountryNameText.text.clear()
            }

        }
    }

    private fun updateUI() {
        countryViewModel.getCurrentCountry()?.let { (countryName, flagUrl) ->
            Glide.with(this)
                .load(flagUrl)
                .centerCrop()
                .into(flagImageView)

            countryNameTextView.text = countryName
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateScore() {
        scoreTextView.text = "Puan: $userScore"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStop() {
        super.onStop()
        with(sharedPreferences.edit()) {
            putInt("score", userScore)
            apply()
        }
    }
}