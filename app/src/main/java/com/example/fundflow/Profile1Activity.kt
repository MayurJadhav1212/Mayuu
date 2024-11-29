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

class Profile1Activity : AppCompatActivity() {

    // Declare Firebase instances and views
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var financeName: TextView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userMobile: TextView
    private lateinit var logoutButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile1)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        financeName = findViewById(R.id.profilef_name)
        userName = findViewById(R.id.profile_name)
        userEmail = findViewById(R.id.profile_email)
        userMobile = findViewById(R.id.profile_mobile)
        logoutButton = findViewById(R.id.logout_button)

        // Fetch and display user details
        loadUserProfile()

        // Handle logout button click
        logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    private fun loadUserProfile() {
        // Get the current user ID
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Fetch user data from Firestore
            db.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Safely retrieve values
                        val financeNameText = document.getString("financeName") ?: "Unknown"
                        val userNameText = document.getString("userName") ?: "User"
                        val emailText = document.getString("email") ?: "Not provided"
                        val mobileText = document.getString("mobile") ?: "Not provided"

                        // Display the user's data
                        financeName.text = financeNameText
                        userName.text = userNameText
                        userEmail.text = emailText
                        userMobile.text = mobileText
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to load user data: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}
