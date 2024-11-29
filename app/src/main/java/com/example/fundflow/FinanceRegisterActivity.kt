package com.example.fundflow

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FinanceRegisterActivity : AppCompatActivity() {

    // Declare variables for EditTexts and CheckBox
    private lateinit var financeName: EditText
    private lateinit var username: EditText
    private lateinit var emailAddress: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var mobileNumber: EditText
    private lateinit var termsConditions: CheckBox
    private lateinit var registerButton: Button
    private lateinit var backArrow: ImageView

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register) // Ensure XML file name is correct

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        financeName = findViewById(R.id.financename)
        username = findViewById(R.id.username)
        emailAddress = findViewById(R.id.email_address)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        mobileNumber = findViewById(R.id.mobile_number)
        termsConditions = findViewById(R.id.terms_conditions)
        registerButton = findViewById(R.id.register_button)
        backArrow = findViewById(R.id.back)

        // Handle register button click
        registerButton.setOnClickListener {
            if (validateInputs()) {
                val email = emailAddress.text.toString().trim()
                val pass = password.text.toString().trim()

                // Firebase create user
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Get the current user ID
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                            // Save user data to Firestore after successful registration
                            saveUserDataToFirestore(userId)

                            // Registration success
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

                            // Optionally navigate to another activity
                            startActivity(Intent(this, FinanceLoginActivity::class.java))
                            finish()
                        } else {
                            // Registration failed
                            Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        // Handle back arrow click
        backArrow.setOnClickListener {
            startActivity(Intent(this, FinanceLoginActivity::class.java))
            finish()
        }
    }

    // Save user data to Firestore
    private fun saveUserDataToFirestore(userId: String) {
        val user = hashMapOf(
            "financeName" to financeName.text.toString(),
            "username" to username.text.toString(),
            "email" to emailAddress.text.toString(),
            "mobileNumber" to mobileNumber.text.toString()
        )

        // Save the user data under the user's ID in Firestore
        db.collection("Users").document(userId).set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User data saved successfully
                    Toast.makeText(this, "User Data Saved Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle errors
                    Toast.makeText(this, "Failed to Save User Data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Validate user input
    private fun validateInputs(): Boolean {
        if (financeName.text.isEmpty() || username.text.isEmpty() || emailAddress.text.isEmpty()
            || password.text.isEmpty() || confirmPassword.text.isEmpty() || mobileNumber.text.isEmpty()
        ) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.text.toString() != confirmPassword.text.toString()) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!termsConditions.isChecked) {
            Toast.makeText(this, "Please accept the Terms and Conditions", Toast.LENGTH_SHORT).show()
            return false
        }

        if (mobileNumber.text.length != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
//g