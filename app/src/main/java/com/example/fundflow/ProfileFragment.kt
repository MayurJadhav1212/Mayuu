package com.example.fundflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var profileImage: ImageView
    private lateinit var firstName: TextView
    private lateinit var userEmail: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Bind views
        profileImage = view.findViewById(R.id.profileImage)
        firstName = view.findViewById(R.id.userName)
        userEmail = view.findViewById(R.id.userEmail)
        val editProfileButton = view.findViewById<Button>(R.id.editProfileButton)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

        // Load user information
        loadUserInfo()

        // Set button click listeners
        editProfileButton.setOnClickListener {
            Toast.makeText(activity, "Edit Profile Clicked", Toast.LENGTH_SHORT).show()
            // Example: startActivity(Intent(activity, EditProfileActivity::class.java))
        }

        logoutButton.setOnClickListener {
            Log.d("ProfileFragment", "Logout button clicked")

            auth.signOut()
            Log.d("ProfileFragment", "User signed out")

            val intent = Intent(requireContext(), UserLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Log.d("ProfileFragment", "Redirecting to UserLoginActivity")
        }




        return view
    }

    private fun loadUserInfo() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Set email from auth object
            userEmail.text = currentUser.email

            // Fetch additional user details from Firestore
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Check if 'name' field exists in Firestore document
                        val name = document.getString("name")
                        if (!name.isNullOrEmpty()) {
                            firstName.text = name
                        } else {
                            Log.e("ProfileFragment", "Name field is empty or missing in Firestore")
                            Toast.makeText(activity, "Name field missing", Toast.LENGTH_SHORT).show()
                            firstName.text = "Unknown Name" // Default if name is missing
                        }

                        // Load profile image with Picasso if URL exists
                        val profileImageUrl = document.getString("profileImageUrl")
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Picasso.get()
                                .load(profileImageUrl)
                                .placeholder(R.drawable.baseline_account_circle_24)  // Placeholder while loading
                                .error(R.drawable.baseline_account_circle_24)       // Error image
                                .into(profileImage)
                        } else {
                            profileImage.setImageResource(R.drawable.baseline_account_circle_24)
                        }
                    } else {
                        Log.e("ProfileFragment", "No document found for user ID: ${currentUser.uid}")
                        Toast.makeText(activity, "No user data found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileFragment", "Failed to load user data: ${exception.message}")
                    Toast.makeText(activity, "Failed to load user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("ProfileFragment", "User not signed in")
            Toast.makeText(activity, "User not signed in", Toast.LENGTH_SHORT).show()
        }
    }
}
