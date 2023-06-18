package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private val city = "Dhaka,BD"
    private val apiKey = "b52eb4317078fe9bdd42f07a1e559b3c"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.info.setOnClickListener {
            Snackbar.make(binding.mainContainer, "Created by Fariha Sultana", Snackbar.LENGTH_SHORT).show()
        }
        weatherTask().execute()

    }

    inner class weatherTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg p0: String?): String {
            var response = ""
            try {
                val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$apiKey"
                response = URL(url).readText(Charsets.UTF_8)
            } catch (e: Exception) {
                Log.e("WeatherApp", "Error fetching weather data", e)
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!result.isNullOrEmpty()) {
                try {
                    val jsonObject = JSONObject(result)
                    // Parse the JSON response and update UI accordingly
                    val main = jsonObject.getJSONObject("main")
                    val sys = jsonObject.getJSONObject("sys")
                    val wind = jsonObject.getJSONObject("wind")
                    val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
                    val updateAt: Long = jsonObject.getLong("dt")
                    val updatedAtText =
                        "Updated At : " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                            Date(updateAt*1000)
                        )
                    val temp = main.getString("temp") + "℃"
                    val tempMin = "Min Temp: " + main.getString("temp_min") + "℃"
                    val tempMax = "Max Temp: " + main.getString("temp_max") + "℃"
                    val pressure = main.getString("pressure")
                    val humidity = main.getString("humidity")
                    val sunrise: Long = sys.getLong("sunrise")
                    val sunset: Long = sys.getLong("sunset")
                    val windSpeed = wind.getString("speed")
                    val weatherDescription = weather.getString("description")
                    val address = jsonObject.getString("name") + " , " + sys.getString("country")

                    binding.address.text = address
                    binding.updatedAt.text = updatedAtText
                    binding.status.text = weatherDescription.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    binding.temp.text = temp
                    binding.tempMin.text = tempMin
                    binding.tempMax.text = tempMax
                    binding.sunrise.text =
                        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                    binding.sunset.text =
                        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                    binding.wind.text = windSpeed
                    binding.pressure.text = pressure
                    binding.humidity.text = humidity


                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

                } catch (e: Exception) {
                    Log.e("WeatherApp", "Error parsing JSON response", e)
                    showErrorMessage()
                }
            } else {
                showErrorMessage()
            }
        }

        private fun showErrorMessage() {
            binding.loader.visibility = View.GONE
            binding.mainContainer.visibility = View.GONE
            binding.errorText.visibility = View.VISIBLE
        }
    }
}
