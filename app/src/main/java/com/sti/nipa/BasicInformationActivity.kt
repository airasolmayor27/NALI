package com.sti.nipa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BasicInformationActivity : AppCompatActivity() {

    private val BASE_URL = "https://naliproject.xyz/api/user/"
    private lateinit var contact_person: EditText
    private lateinit var addressEditText: EditText
    private lateinit var mobileNumberEditText: EditText
    private lateinit var medical_history: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_information)
        val bloodTypeSpinner = findViewById<Spinner>(R.id.blood_type_spinner)
        val city_muni_Spinner = findViewById<Spinner>(R.id.city_muni_spinner)
        val contact_person = findViewById<EditText>(R.id.contact_person)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val mobileNumberEditText = findViewById<EditText>(R.id.mobileNumberEditText)
        val medical_history = findViewById<EditText>(R.id.medical_history)
        // Find the toolbar and set it as the support action bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val adapter = ArrayAdapter.createFromResource(this, R.array.blood_type_options, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodTypeSpinner.adapter = adapter

        val adapter_city_muni = ArrayAdapter.createFromResource(this, R.array.city_muni_options,android.R.layout.simple_spinner_item)
        adapter_city_muni.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        city_muni_Spinner.adapter = adapter_city_muni

        val sessionManager = SessionManager(this)
        val loggedInUsernameKey = sessionManager.getUser_nameKey()

        val userDetails = sessionManager.getUserDetails()
        val user_ID = sessionManager.getUserID()

        val submitbutton = findViewById<Button>(R.id.submitButton)
        submitbutton.setOnClickListener {

            val contact_person = contact_person.text.toString()
            val addressEditText = addressEditText.text.toString()
            val mobileNumberEditText = mobileNumberEditText.text.toString()
            val medical_history = medical_history.text.toString()
            val selectedBloodType = bloodTypeSpinner.selectedItem.toString()
            val selectedCityMuni = city_muni_Spinner.selectedItem.toString()

            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            val formatted = current.format(formatter)

            val verified = 1
            val createdDtm = formatted
            // Create a Retrofit instance
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create an instance of the ApiInterface
            val apiService = retrofit.create(ApiInterface::class.java)

            val call = apiService.information(
                user_ID.toString(),
                mobileNumberEditText,
                addressEditText,
                selectedCityMuni,
                selectedBloodType,
                contact_person,
                medical_history,
                verified,
                createdDtm
            )
            Log.d("DEVICE", "Request URL: ${call.request().url}")
            // Enqueue the call to run asynchronously
            call.enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.code() == 201) { // 201 Created is typically returned for successful registration
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.status == true) {
                            Log.e("API", apiResponse.toString())
                            // Registration was successful
                            Toast.makeText(applicationContext, "${apiResponse.message}", Toast.LENGTH_SHORT).show()
                            // Registration was successful, navigate to LoginActivity

                            logoutAndRedirectToLogin()

                        } else {
                            Log.e("API", apiResponse.toString())

                        }
                    } else {
                        // Registration request failed
                        Toast.makeText(applicationContext, "Registration request failed", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // Registration request failed
                    Toast.makeText(applicationContext, "Registration request failed.", Toast.LENGTH_SHORT).show()
                    t.printStackTrace()
                }
            })

        }
    }
    // Handle the back button press
    override fun onSupportNavigateUp(): Boolean {
        showToast("COMPLETE YOUR INFORMATION")
        return true
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun logoutAndRedirectToLogin() {
        val sessionManager = SessionManager(this)
        sessionManager.logoutUser()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}