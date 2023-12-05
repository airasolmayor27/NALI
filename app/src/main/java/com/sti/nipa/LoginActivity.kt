package com.sti.nipa
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private val BASE_URL = "https://naliproject.xyz/api/" // Replace with your API base URL
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI elements
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Set up Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        // Check if the user is already logged in
        val sessionManager = SessionManager(this)
        if (sessionManager.checkLogin()) {


            // User is logged in, redirect to the HomeActivity or desired activity
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        //Handle register button click

        registerButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)

        }

        // Handle login button click
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Call the login API using Retrofit
            apiInterface.login(username, password).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        Log.e("API",apiResponse.toString())
                        // Check if login is true
                        if (apiResponse?.login == true) {
                            val user = apiResponse?.user

                            val iduser = user?.userId
                            val iduserInt = iduser?.toIntOrNull() ?: 0
                            showToast(iduserInt.toString())
                            val roleId = user?.roleId
                            val user_name = user?.name // real username
                            val roleIdInt = roleId ?: 0


                            val username = usernameEditText.text.toString() // email
                            val password = passwordEditText.text.toString()



                            // Save user credentials and login status
                            val sessionManager = SessionManager(this@LoginActivity)
                            sessionManager.createLoginSession(username, password, iduserInt,roleIdInt,user_name.toString())
                            // User is logged in, redirect to the HomeActivity or desired activity
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()



                        } else {
                            // Handle the case where login is false
                            val errorMessage = "Login failed. Email and password are required."
                            showToast(errorMessage)
                        }
                    } else {
                        // Handle API error and display the status code
                        val statusCode = response.code()
                        val errorMessage = "API Error. Status Code: $statusCode"
                        showToast(errorMessage)
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // Log the error message
                    Log.e("NetworkError", "Error: ${t.message}", t)
                    // Show an error message to the user
                    showToast("Network error. Please try again.")
                }
            })



        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

