package com.example.shopappfirebase.firestore

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shopappfirebase.model.*
import com.example.shopappfirebase.ui.activities.*
import com.example.shopappfirebase.ui.fragments.DashboardFragment
import com.example.shopappfirebase.ui.fragments.OrdersFragment
import com.example.shopappfirebase.ui.fragments.ProductFragment
import com.example.shopappfirebase.utils.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FirestoreClass {

	private var firestore: FirebaseFirestore = Firebase.firestore
	private var storage: FirebaseStorage = Firebase.storage

	// USER
	fun getCurrentUserId(): String {
		val currentUser = Firebase.auth.currentUser
		val id = currentUser?.uid
		if (currentUser == null) return "" else return id.toString()
	}

	fun getCurrentUserDetails(activity: Activity) {
		firestore.collection(Constants.USERS)
			.document(getCurrentUserId())
			.get()
			.addOnSuccessListener { document ->
				val user = document.toObject(User::class.java)!!
				when (activity) {
					is LoginActivity -> {
						activity.userLoggedInSuccess(user)
					}
					is SettingsActivity -> {
						activity.onUserDetailsLoadedSuccessfuly(user)
					}
					is CartActivity -> {
						activity.onUserDetailsLoadSuccessfuly(user)
					}
					is ProductDetailsActivity -> {
						activity.onUserDetailsLoadSuccessfuly(user)
					}
					is CheckoutActivity -> {
						activity.onUserDetailsLoadSuccess(user)
					}
				}
			}
			.addOnFailureListener {
				when (activity) {
					is SettingsActivity -> {
						Toast.makeText(activity, "There was an error when fetching your data.", Toast.LENGTH_SHORT).show()
					}
					is CartActivity -> {
						activity.onUserDetailsLoadFailure()
					}
					is CheckoutActivity -> {
						activity.onUserDetailsLoadFailure()
					}
				}
			}
	}

	fun uploadUser(activity: RegisterActivity, user: User) {
		firestore.collection(Constants.USERS)
			.document(user.id)
			.set(user, SetOptions.merge())
			.addOnSuccessListener {
				activity.onUserRegisterSuccess()
			}
			.addOnFailureListener {
				activity.hideProgressDialog()
				activity.showSnackBar("Something went wrong with creating the user.", true)
				Log.e("FIREBASE", "Error creating an user")
			}
	}

	fun updateUser(activity: Activity, userHashMap: HashMap<String, Any>) {
		firestore.collection(Constants.USERS)
			.document(getCurrentUserId())
			.update(userHashMap)
			.addOnSuccessListener {
				when (activity) {
					is UserProfileActivity -> {
						activity.hideProgressDialog()
						if (userHashMap.size == 1 && userHashMap.containsKey(Constants.DATABASE_PROFILE_IMAGE_KEY)) {
							activity.onUserProfileUpdateSuccess("Your profile picture was updated successfully.")
						} else {
							activity.onUserProfileUpdateSuccess("Your profile was updated successfully.")
						}
					}
					is CartActivity -> {
						activity.onUserUpdateSuccess()
					}
					is ProductDetailsActivity -> {
						activity.onUserUpdateSuccess()
					}
				}
			}
			.addOnFailureListener {
				when (activity) {
					is UserProfileActivity -> {
						activity.hideProgressDialog()
						if (userHashMap.size == 1 && userHashMap.containsKey(Constants.DATABASE_PROFILE_IMAGE_KEY)) {
							activity.showSnackBar("Something went wrong when updating your profile picture.", true)
						} else {
							activity.showSnackBar("Something went wrong when updating your profile.", true)
						}
						Log.e("FIREBASE", "Error updating an user")
					}
					is CartActivity -> {
						activity.onUserUpdateFailure()
					}
					is ProductDetailsActivity -> {
						activity.onUserUpdateFailure()
					}
				}
			}
	}

	// PRODUCTS
	fun uploadProduct(activity: Activity, product: Product) {
		firestore.collection(Constants.PRODUCTS)
			.document()
			.set(product, SetOptions.merge())
			.addOnSuccessListener {
				when (activity) {
					is AddProductActivity -> {
						activity.onProductUploadSuccess()
					}
				}
				Log.e("FIREBASE", "Successfully created product")
			}
			.addOnFailureListener {
				when (activity) {
					is AddProductActivity -> {
						activity.onProductUploadFailure()
					}
				}
				Log.e("FIREBASE", "Error creating product")
			}
	}

	fun getUserProducts(fragment: Fragment) {
		firestore.collection(Constants.PRODUCTS)
			.whereEqualTo(Constants.DATABASE_PRODUCT_USER_ID_KEY, getCurrentUserId())
			.get()
			.addOnSuccessListener { result ->
				val userProducts: ArrayList<Product> = ArrayList()
				for (p in result.documents) {
					val product = p.toObject(Product::class.java)
					product!!.id = p.id
					userProducts.add(product)
				}
				when (fragment) {
					is ProductFragment -> {
						fragment.onUserProductsLoadSuccess(userProducts)
					}
				}
			}
			.addOnFailureListener {
				when (fragment) {
					is ProductFragment -> {
						fragment.onUserProductsLoadFailure()
					}
				}
			}
	}

	fun getAllProducts(fragment: Fragment) {
		firestore.collection(Constants.PRODUCTS)
			.get()
			.addOnSuccessListener { result ->
				val userProducts: ArrayList<Product> = ArrayList()
				for (p in result.documents) {
					val product = p.toObject(Product::class.java)
					product!!.id = p.id
					userProducts.add(product)
				}
				when (fragment) {
					is DashboardFragment -> {
						fragment.onUserProductsLoadSuccess(userProducts)
					}
				}
			}
			.addOnFailureListener {
				when (fragment) {
					is DashboardFragment -> {
						fragment.onUserProductsLoadFailure()
					}
				}
			}
	}

	fun deleteProduct(fragment: Fragment, productId: String) {
		firestore.collection(Constants.PRODUCTS)
			.document(productId)
			.delete()
			.addOnSuccessListener { result ->
				when (fragment) {
					is ProductFragment -> {
						fragment.onUserProductsDeleteSuccess()
					}
				}
			}
			.addOnFailureListener {
				when (fragment) {
					is ProductFragment -> {
						fragment.onUserProductsDeleteFailure()
					}
				}
			}
	}

	fun updateProduct(activity: Activity, productId: String, productHashMap: HashMap<String, Any>) {
		firestore.collection(Constants.PRODUCTS)
			.document(productId)
			.update(productHashMap)
			.addOnSuccessListener {
				when (activity) {
					is EditProductActivity -> {
						activity.onProductUpdateSuccess()
					}
				}
			}
			.addOnFailureListener {
				when (activity) {
					is EditProductActivity -> {
						activity.onProductUpdateFailure()
					}
				}
			}
	}

	fun updateProductQuantities(activity: CheckoutActivity, cart: ArrayList<CartItem>) {
		firestore.runBatch { batch ->
			// UPDATE PRODUCT QUANTITIES
			for (cartItem in cart) {
				val productHashMap = HashMap<String, Any>()

				productHashMap[Constants.DATABASE_PRODUCT_QUANTITY_KEY] =
					(cartItem.product!!.stockQuantity.toInt() - cartItem.quantity).toString()

				val docRef = firestore.collection(Constants.PRODUCTS).document(cartItem.product.id)
				batch.update(docRef, productHashMap)
			}

			// EMPTY THE CART
			val docRef = firestore.collection(Constants.USERS).document(getCurrentUserId())
			val userHashMap = HashMap<String, Any>()
			userHashMap[Constants.DATABASE_CART_KEY] = HashMap<String, CartItem>()
			batch.update(docRef, userHashMap)
		}.addOnSuccessListener {
			activity.onProductQuantityUpdateSuccess()
		}.addOnFailureListener {
			activity.onProductQuantityUpdateFailure()
		}
	}

	// ADDRESS
	fun uploadAddress(activity: Activity, address: Address) {
		firestore.collection(Constants.ADDRESSES)
			.document()
			.set(address, SetOptions.merge())
			.addOnSuccessListener {
				when (activity) {
					is AddEditAddressActivity -> {
						activity.onAddressUploadSuccess()
					}
				}
				Log.e("FIREBASE", "Successfully created address")
			}
			.addOnFailureListener {
				when (activity) {
					is AddEditAddressActivity -> {
						activity.onAddressUploadFailure()
					}
				}
				Log.e("FIREBASE", "Error creating address")
			}
	}

	fun getUserAddresses(activity: AddressListActivity) {
		firestore.collection(Constants.ADDRESSES)
			.whereEqualTo(Constants.DATABASE_PRODUCT_USER_ID_KEY, getCurrentUserId())
			.get()
			.addOnSuccessListener { result ->
				val addressList: ArrayList<Address> = ArrayList()
				for (adr in result.documents) {
					val address = adr.toObject(Address::class.java)
					address!!.id = adr.id
					addressList.add(address)
				}
				activity.onAddressListFetchSuccess(addressList)
			}
			.addOnFailureListener {
				activity.onAddressListFetchFailure()
			}
	}

	fun updateAddress(activity: AddEditAddressActivity, address: Address) {
		firestore.collection(Constants.ADDRESSES)
			.document(address.id)
			.set(address, SetOptions.merge())
			.addOnSuccessListener {
				activity.onAddressUpdateSuccess()
			}
			.addOnFailureListener {
				activity.onAddressUpdateFailure()
			}
	}

	fun deleteAddress(activity: AddressListActivity, addressId: String) {
		firestore.collection(Constants.ADDRESSES)
			.document(addressId)
			.delete()
			.addOnSuccessListener {
				activity.onAddressDeleteSuccess()
			}
			.addOnFailureListener {
				activity.onAddressDeleteFailure()
			}
	}

	// ORDERS
	fun uploadOrder(activity: CheckoutActivity, order: Order) {
		firestore.collection(Constants.ORDERS)
			.document()
			.set(order, SetOptions.merge())
			.addOnSuccessListener {
				activity.onOrderUploadSuccess()
				Log.e("FIREBASE", "Successfully created address")
			}
			.addOnFailureListener {
				activity.onOrderUploadFailure()
				Log.e("FIREBASE", "Error creating address")
			}
	}

	fun getUserOrders(fragment: OrdersFragment) {
		firestore.collection(Constants.ORDERS)
			.whereEqualTo(Constants.DATABASE_PRODUCT_USER_ID_KEY, getCurrentUserId())
			.get()
			.addOnSuccessListener { document ->
				val orderList : ArrayList<Order> = ArrayList()

				for (doc in document.documents) {
					val order = doc.toObject(Order::class.java)
					order!!.id = doc.id
					orderList.add(order)
				}

				fragment.onUserOrdersLoadSuccess(orderList)
			}
			.addOnFailureListener {
				fragment.onUserOrdersLoadFailure()
			}
	}

	// OTHER
	fun uploadImage(activity: Activity, imageFileURI: Uri, imageType: String) {
		val imageExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
			activity.contentResolver.getType(imageFileURI)
		)
		val storageRef : StorageReference =  storage.reference.child(
			imageType +
					System.currentTimeMillis() +
					"." +
					imageExtension
		)

		storageRef.putFile(imageFileURI)
			.addOnSuccessListener { task ->
				Log.i("STORAGE", "Image uploaded successfully")
				task.metadata!!.reference!!.downloadUrl
					.addOnSuccessListener { uri ->
						when (activity) {
							is UserProfileActivity -> {
								activity.onImageUploadSuccess(uri.toString())
							}
							is AddProductActivity -> {
								activity.onImageUploadFinish(uri.toString())
							}
							is EditProductActivity -> {
								activity.onImageUploadFinish(uri.toString())
							}
						}
					}
			}
			.addOnFailureListener { exception ->
				Log.e("STORAGE", exception.message.toString())
				when (activity) {
					is UserProfileActivity -> {
						activity.hideProgressDialog()
						Toast.makeText(
							activity,
							"An error occured during upload.",
							Toast.LENGTH_SHORT
						).show()
					}
					is AddProductActivity -> {
						activity.onImageUploadFinish("")
					}
				}
			}
	}
}