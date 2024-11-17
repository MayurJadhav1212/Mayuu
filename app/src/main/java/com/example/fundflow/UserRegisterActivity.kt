package com.example.fundflow

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fundflow.R
import com.example.fundflow.UserLoginActivity
import com.google.firebase.auth.FirebaseAuth

class UserRegisterActivity : AppCompatActivity() {

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var emailAddress: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var showPassword: CheckBox
    private lateinit var showConfirmPassword: CheckBox
    private lateinit var termsConditions: CheckBox
    private lateinit var registerButton: Button
    private lateinit var backArrow: ImageView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        // Initialize Firebase Auth
        try {
            auth = FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Firebase initialization failed: ${e.message}")
            Toast.makeText(this, "Error initializing Firebase", Toast.LENGTH_LONG).show()
            return
        }

        // Initialize views
        initializeViews()

        // Set up password visibility checkboxes
        setupPasswordVisibility()

        // Handle register button click
        registerButton.setOnClickListener {
            if (validateInputs()) {
                registerUser()
            }
        }

        // Handle back arrow click
        backArrow.setOnClickListener {
            startActivity(Intent(this, UserLoginActivity::class.java))
            finish()
        }
    }

    private fun initializeViews() {
        firstName = findViewById(R.id.first_name)
        lastName = findViewById(R.id.last_name)
        emailAddress = findViewById(R.id.email_address)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        showPassword = findViewById(R.id.show_password)
        showConfirmPassword = findViewById(R.id.show_confirm_password)
        termsConditions = findViewById(R.id.terms_conditions)
        registerButton = findViewById(R.id.register_button)
        backArrow = findViewById(R.id.back)
    }

    private fun setupPasswordVisibility() {
        showPassword.setOnCheckedChangeListener { _, isChecked ->
            password.inputType = if (isChecked) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            password.setSelection(password.text.length)
        }

        showConfirmPassword.setOnCheckedChangeListener { _, isChecked ->
            confirmPassword.inputType = if (isChecked) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            confirmPassword.setSelection(confirmPassword.text.length)
        }
    }

    private fun registerUser() {
        val email = emailAddress.text.toString().trim()
        val pass = password.text.toString().trim()

        // Check for valid email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Check for password length
        if (pass.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, UserLoginActivity::class.java))
                    finish()
                } else {
                    Log.e("Register", "Registration failed: ${task.exception?.message}")
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Log.e("Register", "Error creating user: ${it.message}")
                Toast.makeText(this, "Error creating user: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun validateInputs(): Boolean {
        return when {
            firstName.text.isEmpty() -> {
                Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show()
                false
            }
            lastName.text.isEmpty() -> {
                Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show()
                false
            }
            emailAddress.text.isEmpty() -> {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                false
            }
            password.text.isEmpty() -> {
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
                false
            }
            confirmPassword.text.isEmpty() -> {
                Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show()
                false
            }
            password.text.toString() != confirmPassword.text.toString() -> {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                false
            }
            !termsConditions.isChecked -> {
                Toast.makeText(this, "Please accept the Terms and Conditions", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}
