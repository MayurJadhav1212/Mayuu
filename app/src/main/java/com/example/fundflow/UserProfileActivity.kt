package com.example.fundflow

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {

    private var profileImage: ImageView? = null
    private var profileTitle: TextView? = null
    private var profileName: TextView? = null
    private var profileEmail: TextView? = null
    private var saveButton: Button? = null
    private var logoutButton: Button? = null

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile) // Make sure the layout name is correct

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back button (up arrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views
        profileImage = findViewById(R.id.profile_image)
        profileTitle = findViewById(R.id.profile_title)
        profileName = findViewById(R.id.profile_name)
        profileEmail = findViewById(R.id.profile_email)
        saveButton = findViewById(R.id.save_button)
        logoutButton = findViewById(R.id.logout_button)

        // Load profile data
        loadProfileData()

        // Set up button actions
        saveButton?.setOnClickListener {
            saveProfileData()
        }

        logoutButton?.setOnClickListener {
            logoutUser()
        }
    }

    private fun loadProfileData() {
        // Fetch email from Firebase Authentication
        val user = auth.currentUser
        if (user != null) {
            // Set email (Firebase Authentication automatically provides the email)
            profileEmail?.text = user.email

            // Fetch the user's name from Firestore
            val userId = user.uid
            val userRef = firestore.collection("users").document(userId)

            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Assuming 'name' field is stored in Firestore under the 'users' collection
                    val name = document.getString("name") ?: "No name available"
                    profileName?.text = name
                }
            }.addOnFailureListener { exception ->
                // Handle failure to fetch data from Firestore
                profileName?.text = "Error loading name"
            }
        }
    }

    private fun saveProfileData() {
        // Logic to save updated profile data
    }

    private fun logoutUser() {
        // Logic to log out the user
        auth.signOut()
        val intent = Intent(this@UserProfileActivity, UserLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Handle back button click (up arrow)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()  // This will navigate back to the previous activity
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
