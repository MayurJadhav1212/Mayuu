package com.example.fundflow

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRegisterActivity : AppCompatActivity() {

    // Declare variables for EditTexts and Buttons
    private lateinit var username: EditText // Changed from firstName and lastName to username
    private lateinit var emailAddress: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var mobileNumber: EditText
    private lateinit var registerButton: Button
    private lateinit var backArrow: ImageView

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        username = findViewById(R.id.username) // Initialize username
        emailAddress = findViewById(R.id.email_address)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        mobileNumber = findViewById(R.id.mobile_number)
        registerButton = findViewById(R.id.register_button)
        backArrow = findViewById(R.id.back)

        // Handle password visibility toggle for the "password" field
        togglePasswordVisibility(password)

        // Handle password visibility toggle for the "confirmPassword" field
        togglePasswordVisibility(confirmPassword)

        // Handle register button click
        registerButton.setOnClickListener {
            if (validateInputs()) {
                val email = emailAddress.text.toString().trim()
                val pass = password.text.toString().trim()
                val mobile = mobileNumber.text.toString().trim()

                // Firebase create user
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: ""

                            // Save user data to Firestore
                            saveUserDataToFirestore(userId, mobile)

                            // Registration success
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

                            // Navigate to login activity
                            startActivity(Intent(this, UserLoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        // Handle back arrow click
        backArrow.setOnClickListener {
            startActivity(Intent(this, UserLoginActivity::class.java))
            finish()
        }
    }

    // Save user data to Firestore, including username and mobile number
    private fun saveUserDataToFirestore(userId: String, mobile: String) {
        val user = hashMapOf(
            "username" to username.text.toString(), // Store username
            "email" to emailAddress.text.toString(),
            "mobile" to mobile
        )

        db.collection("Users").document(userId).set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User Data Saved Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to Save User Data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Validate user inputs
    private fun validateInputs(): Boolean {
        if (username.text.isEmpty() || emailAddress.text.isEmpty()
            || password.text.isEmpty() || confirmPassword.text.isEmpty() || mobileNumber.text.isEmpty()
        ) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate email format
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!emailAddress.text.toString().trim().matches(emailPattern.toRegex())) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check mobile number length
        val mobile = mobileNumber.text.toString().trim()
        if (mobile.length != 10) {
            Toast.makeText(this, "Mobile number must be 10 digits", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if passwords match
        if (password.text.toString() != confirmPassword.text.toString()) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Function to handle password visibility toggle
    private fun togglePasswordVisibility(editText: EditText) {
        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = editText.compoundDrawablesRelative[2] // Drawable at the end
                if (drawableEnd != null && event.rawX >= (editText.right - drawableEnd.bounds.width())) {
                    if (editText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
                    } else {
                        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
                    }
                    editText.setSelection(editText.text.length)
                    true
                } else false
            } else false
        }
    }
}
