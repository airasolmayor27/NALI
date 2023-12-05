package com.sti.nipa



data class User(
    val userId: String,
    val roleId: Int,
    val name :String

    // Add other user properties here
)


data class ApiResponse(
    val status: Boolean,
    val message: String,
    val device_data: List<DeviceData>,
    val login: Boolean,
    val result:  List<UserInformation>,
    val user: User  // Define it as an object, not a string
)

data class DeviceData(
    val user_id: String,
    val device_id: String
)

data class UserInformation(
    val user_id: String,
    val verified: String,
    val emer_contact: String,
    val contact_address: String,
    val city_muni:String,
    val blood_type:String,
    val contact_person:String


)