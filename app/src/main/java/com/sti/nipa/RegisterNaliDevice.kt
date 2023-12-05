package com.sti.nipa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterNaliDevice : AppCompatActivity() {
    private val BASE_URL = "https://naliproject.xyz/api/"
    private  lateinit var btn_home : Button
    private lateinit var registerButton: Button
    private lateinit var deviceEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_nali_device)

        // Find the toolbar and set it as the support action bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val sessionManager = SessionManager(this)
        val loggedInUsernameKey = sessionManager.getUser_nameKey()

        val userDetails = sessionManager.getUserDetails()
        val user_ID = sessionManager.getUserID()
        deviceEditText = findViewById(R.id.deviceEditText);
        registerButton = findViewById(R.id.register_device);
        btn_home = findViewById(R.id.go_back_home);
        btn_home.setOnClickListener {

            val intent = Intent(this, HomeActivity::class.java)

            startActivity(intent)
            finish()
        }



        registerButton.setOnClickListener {
            val device = deviceEditText.text.toString()

            if (device.isBlank()) {
                deviceEditText.error = "Device ID is Required"
                return@setOnClickListener
            }

            // Continue with the rest of your logic for button click

            // Create a Retrofit instance
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiInterface::class.java)

            val call = apiService.device(
                device,
                user_ID.toString()
            )
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
                            val intent = Intent(this@RegisterNaliDevice, HomeFragment::class.java)
                            startActivity(intent)
                            finish() // Optional: Close the RegisterActivity if you don't want to go back to it
                        } else {
                            Log.e("API", apiResponse.toString())
                            if (apiResponse != null && "'Device already exists" == apiResponse.message) {
                                // Handle the case where the email already exists
                                // Display an appropriate message to the user
                                Toast.makeText(applicationContext, "'Device already exists", Toast.LENGTH_SHORT).show()
                            } else {
                                // Registration failed for other reasons
                                Toast.makeText(applicationContext, "Registration failed: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                                // Handle failure, maybe show an error message
                            }
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

                    // Print the actual error message
                    Log.e("API", "Registration request failed: ${t.message}")
                }
            })
        }
    }

    // Handle the back button press
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}