package com.example.shopappfirebase.utils

object Constants {

	// Firestore Collections
    const val USERS: String = "users"
    const val PRODUCTS: String = "products"
    const val ADDRESSES: String = "addresses"
    const val ORDERS: String = "orders"

    const val DATABASE_MOBILE_KEY: String = "phoneNumber"
    const val DATABASE_GENDER_KEY: String = "gender"
    const val DATABASE_FIRST_NAME_KEY : String = "firstName"
    const val DATABASE_LAST_NAME_KEY : String = "lastName"
    const val DATABASE_PROFILE_IMAGE_KEY : String = "image"
    const val DATABASE_PROFILE_COMPLETED_KEY: String = "profileCompleted"
    const val DATABASE_CART_KEY : String = "cart"

    const val DATABASE_PRODUCT_NAME_KEY: String = "name"
    const val DATABASE_PRODUCT_PRICE_KEY: String = "price"
    const val DATABASE_PRODUCT_DESCRIPTION_KEY: String = "description"
    const val DATABASE_PRODUCT_QUANTITY_KEY: String = "stockQuantity"
    const val DATABASE_PRODUCT_USERNAME_KEY: String = "userName"
    const val DATABASE_PRODUCT_IMAGE_KEY: String = "image"
    const val DATABASE_PRODUCT_USER_ID_KEY : String = "userId"

    const val DATABASE_ADDRESS_HOME_ADDRESS = "Home"
    const val DATABASE_ADDRESS_OFFICE_ADDRESS = "Office"
    const val DATABASE_ADDRESS_OTHER_ADDRESS = "Other"

    const val INTENT_EXTRA_PRODUCT_KEY: String = "product"
    const val INTENT_EXTRA_ADDRESS_KEY: String = "address"
    const val INTENT_EXTRA_ADDRESS_ID_KEY: String = "id"
    const val INTENT_EXTRA_ORDER_KEY: String = "order"

    const val MYSHOPAPP_PREFERENCES: String = "MyShopAppPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val USER_DETAILS: String = "extra_user_details"

    const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 0
    const val CAMERA_REQUEST_CODE = 1
    const val READ_EXTERNAL_STORAGE_AND_CAMERA_REQUEST_CODE = 2

    const val GENDER_MALE = "male"
    const val GENDER_FEMALE = "female"
    const val GENDER_HIDDEN = "hidden"
}