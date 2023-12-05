package com.sti.nipa
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.telephony.emergency.EmergencyNumber
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var curLocationTextView: TextView

    private  lateinit var profileName: TextView
    private lateinit var nali_device_id: TextView
    private  lateinit var registerBtn: Button
    public  lateinit var emergencyNumber: TextView
    public  lateinit var emergencyPeson:TextView
    public  lateinit var bloodtype : TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    // Define your Retrofit instance for making API calls
//    private val retrofit = Retrofit.Builder()
//        .baseUrl("https://naliproject.xyz/api/") // Replace with your CodeIgniter API base URL
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    // Create an instance of your API interface
//    private val apiInterface = retrofit.create(ApiInterface::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        curLocationTextView = view.findViewById(R.id.cur_location)
        profileName = view.findViewById(R.id.profileName)
        registerBtn = view.findViewById(R.id.register_device_button)
        nali_device_id = view.findViewById(R.id.nali_device_id)
        bloodtype = view.findViewById(R.id.bloodtypeTextView)
        emergencyNumber = view.findViewById(R.id.emergencyContact)
        emergencyPeson = view.findViewById(R.id.emergencyContactPerson)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        registerBtn.setOnClickListener {    val intent = Intent(requireContext(), RegisterNaliDevice::class.java)
            startActivity(intent) }

    // Inside your onCreateView method where you set up the OkHttpClient
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                // Log the message as desired, e.g., to Android Logcat
                // You can also write it to a file or use any other logging mechanism
                Log.d("API Request", message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY // Set the desired logging level
        }
    // Inside onCreateView method

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
// Create an instance of your Retrofit service interface with the custom OkHttpClient
        val apiInterface = Retrofit.Builder()
            .baseUrl("https://naliproject.xyz/api/") // Replace with your CodeIgniter API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // Set the OkHttpClient with the custom logging interceptor
            .build()
            .create(ApiInterface::class.java)

        val sessionManager = SessionManager(view.context)
        val loggedInUsernameKey = sessionManager.getUser_nameKey()

        val userDetails = sessionManager.getUserDetails()
        val user_ID = sessionManager.getUserID()

        val loggedInUsernameText = userDetails[loggedInUsernameKey] ?: ""
        profileName.text = loggedInUsernameText.toString()




        val refreshButton = view.findViewById<Button>(R.id.refreshButton)
        refreshButton.setOnClickListener {
            refreshLocation()

        }



        val call = apiInterface.check(user_ID.toString())

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(callchecker: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse1 = response.body()
                    Log.e("USER",apiResponse1.toString())
                    if (apiResponse1 != null) {
                        if (apiResponse1.status == true) {
                            if (apiResponse1.result.isNotEmpty()) {
                                val verified: Boolean = apiResponse1.result[0].verified == "1"
                                bloodtype.text = "Blood Type: ${apiResponse1.result[0].blood_type}"

                                emergencyNumber.text = "Emergency Contact: ${apiResponse1.result[0].emer_contact}"
                                //baliktad
                                emergencyPeson.text =  "Emergency Contact: ${apiResponse1.result[0].contact_address}"

                                if (verified) {
                                  Log.e("USER", "VERFIED")
                                } else {
                                   // showToast("COMPLETE YOUR INFORMATION")
                                    showCompletionDialog()
                                }

                            } else {
                                nali_device_id.text = "No Data Registered"


                            }
                        } else {
                            showToast("Status False")
                        }
                    } else {
                        // Handle the response here when the API response is null
                        showToast("API response is null")
                    }
                } else {
                    // Handle the error response here
                    showToast("Failed to retrieve user information")
                }
            }

            override fun onFailure(callchecker: Call<ApiResponse>, t: Throwable) {
                // Handle failure here
            }
        })



        val callchecker = apiInterface.checkerdevice(user_ID.toString())
        Log.d("DEVICE", "Request URL: ${callchecker.request().url}")

        callchecker.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(callchecker: Call<ApiResponse>, response1: Response<ApiResponse>) {
                if (response1.isSuccessful) {
                    val apiResponse1 = response1.body()
                    Log.e("DEVICE",apiResponse1.toString())
                    if (apiResponse1 != null) {
                        if (apiResponse1.status == true) {
                            if (apiResponse1.device_data.isNotEmpty()) {
                                val userId: String = apiResponse1.device_data[0].device_id

                                nali_device_id.text = userId.toUpperCase()
                                Log.e("USER", userId)
                                registerBtn.isEnabled = false
                            } else {
                                nali_device_id.text = "No Device Registered"
                                registerBtn.isEnabled = true

                            }
                        } else {
                            showToast("Status False")
                        }
                    } else {
                        // Handle the response here when the API response is null
                        showToast("API response is null")
                    }
                } else {
                    // Handle the error response here
                    showToast("Failed to retrieve user information")
                }
            }

            override fun onFailure(callchecker: Call<ApiResponse>, t: Throwable) {
                // Handle failure here
            }
        })




        // Check for location permission
        if (isLocationPermissionGranted()) {
            // If permission is granted, get the current location
            getCurrentLocation()
        } else {
            // Request location permission
            requestLocationPermission()
        }

        return view
    }

    private fun refreshLocation() {
        if (isLocationPermissionGranted()) {
            getCurrentLocation()
        } else {
            requestLocationPermission()
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun showCompletionDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Complete Information")
        builder.setMessage("Please complete your information.")
        builder.setPositiveButton("CONFIRM") { dialog, _ ->


            val intent = Intent(requireContext(), BasicInformationActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        // Set the dialog to be non-cancelable when clicking outside
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }

    private fun isLocationPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }



    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In a real app, you should handle null values.
                    val latitude = location?.latitude ?: 0.0
                    val longitude = location?.longitude ?: 0.0
                    val locationName = getLocationName(latitude, longitude)
                    curLocationTextView.text = locationName
                }
                .addOnFailureListener { e ->
                    // Handle any errors that occurred while getting the location
                    curLocationTextView.text = "Error: ${e.message}"
                }
        } catch (securityException: SecurityException) {
            curLocationTextView.text = "Location permission denied by the user."
        }
    }

    private fun getLocationName(latitude: Double, longitude: Double): String {
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                return address.getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Location not found"
    }

    private fun showEmergencyInputDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.custom_alert_dialog, null)
        val emergencyNameInput = dialogView.findViewById<EditText>(R.id.emergencyNameInput)
        val currentLocationText = dialogView.findViewById<TextView>(R.id.currentLocationText)

        // Initialize the Fused Location Provider Client
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request the current location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Update the TextView with the current location
                    if (location != null) {
                        val geocoder = Geocoder(requireContext())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val street = address.thoroughfare ?: ""
                            val barangay = address.subLocality ?: ""
                            val municipalityOrCity = address.locality ?: ""
                            val province = address.adminArea ?: ""

                            val locationText = "Current Location: $street, $barangay, $municipalityOrCity, $province"
                            currentLocationText.text = locationText
                        } else {
                            currentLocationText.text = "Current Location: Address not found"
                        }
                    } else {
                        currentLocationText.text = "Current Location: Location not available"
                    }
                }
        } else {
            currentLocationText.text = "Current Location: Permission not granted"
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setPositiveButton("Submit") { dialog, _ ->
            val emergencyName = emergencyNameInput.text.toString()
            if (emergencyName.isNotEmpty()) {
                showToast("Emergency Name: $emergencyName")
            } else {
                showToast("Please enter an emergency name.")
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the current location
                getCurrentLocation()
            } else {
                curLocationTextView.text = "Location permission denied by the user."
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    fun onRefreshButtonClick(view: View?) {
        // Handle the refresh button click here
        // You can update the user information or perform any necessary actions.
    }


}

