package com.example.fundflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile1Activity : AppCompatActivity() {

    // UI elements
    private lateinit var profileFName: TextView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileMobile: TextView
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile1) // Use your actual layout XML file name

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI elements
        profileFName = findViewById(R.id.profilef_name)
        profileName = findViewById(R.id.profile_name)
        profileEmail = findViewById(R.id.profile_email)
        profileMobile = findViewById(R.id.profile_mobile)
        saveButton = findViewById(R.id.save_button)
        logoutButton = findViewById(R.id.logout_button)

        // Load user data
        loadUserData()

        // Set onClickListener for Save Button
        saveButton.setOnClickListener {
            updateUserProfile()
        }

        // Set onClickListener for Logout Button
        logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    // Method to load user data from Firebase Firestore
    private fun loadUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Fetch user data from Firestore
            db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Populate fields with user data
                        profileFName.text = document.getString("finance_name") ?: "N/A"
                        profileName.text = document.getString("user_name") ?: "N/A"
                        profileEmail.text = document.getString("email") ?: "N/A"
                        profileMobile.text = document.getString("mobile_number") ?: "N/A"
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to load user data: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "User is not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to update the user profile
    private fun updateUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val updatedFName = profileFName.text.toString()
            val updatedName = profileName.text.toString()
            val updatedEmail = profileEmail.text.toString()
            val updatedMobile = profileMobile.text.toString()

            // Validate input
            if (updatedFName.isEmpty() || updatedName.isEmpty() || updatedEmail.isEmpty() || updatedMobile.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                return
            }

            // Update Firestore with new data
            val userUpdates = hashMapOf(
                "finance_name" to updatedFName,
                "user_name" to updatedName,
                "email" to updatedEmail,
                "mobile_number" to updatedMobile
            ) as Map<String, Any>  // Cast to Map<String, Any>

            db.collection("Users").document(userId)
                .update(userUpdates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to update profile: ${exception.message}", Toast.LENGTH_LONG).show()
                }

        } else {
            Toast.makeText(this, "User is not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to log out the user
    private fun logoutUser() {
        // Sign out from Firebase
        auth.signOut()
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()

        // Redirect to Login screen
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
