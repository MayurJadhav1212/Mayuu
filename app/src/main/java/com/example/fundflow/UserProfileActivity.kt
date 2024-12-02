package com.example.fundflow

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {

    // Declare Firebase instances and views
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userMobile: TextView  // TextView for mobile number
    private lateinit var logoutButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        userName = findViewById(R.id.profile_name)
        userEmail = findViewById(R.id.profile_email)
        userMobile = findViewById(R.id.profile_mobile)  // Initialize mobile TextView
        logoutButton = findViewById(R.id.logout_button)

        // Fetch and display user details
        loadUserProfile()

        // Handle logout button click
        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    private fun loadUserProfile() {
        // Get current user
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Fetch user data from Firestore
            db.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Safely retrieve values
                        val username = document.getString("username") ?: "Unknown User"
                        val email = document.getString("email") ?: "Not provided"
                        val mobile = document.getString("mobile") ?: "Not provided" // Correct key

                        // Display the user's username, email, and mobile number
                        userName.text = username
                        userEmail.text = email
                        userMobile.text = mobile  // Display mobile number
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to load user data: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
