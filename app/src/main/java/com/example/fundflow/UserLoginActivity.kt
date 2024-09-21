package com.example.fundflow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class UserLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "UserActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Find the views by their IDs
        val emailInput: EditText = findViewById(R.id.email)
        val passwordInput: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.loginButton)
        val signUpText: TextView = findViewById(R.id.signUpText)
        val showPassword: CheckBox = findViewById(R.id.show_password)

        // Check if the user is already logged in and bypass login
        if (isUserLoggedIn()) {
            redirectToHome()
        }

        // Show or hide password when checkbox is toggled
        showPassword.setOnCheckedChangeListener { _, isChecked ->
            passwordInput.inputType = if (isChecked) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordInput.setSelection(passwordInput.text.length)  // Keep cursor at the end
        }

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                            // Determine user role and save login state
                            val userRole = determineUserRole(email)  // Example function
                            saveLoginState(userRole)

                            // Redirect based on user role
                            redirectToHome()
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle sign-up text click
        signUpText.setOnClickListener {
            val intent = Intent(this, UserRegisterActivity::class.java)
            startActivity(intent)
            finish() // Close current activity after navigating to sign-up
        }
    }

    // Save the login state and user role in SharedPreferences
    private fun saveLoginState(userRole: String) {
        val sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("is_logged_in", true)
            putString("userRole", userRole)
            apply()  // Save the changes asynchronously
        }
    }

    // Check if the user is logged in
    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("is_logged_in", false)
    }

    // Redirect based on user role saved in SharedPreferences
    private fun redirectToHome() {
        val sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userRole = sharedPref.getString("userRole", "Userbtn") // Default to "Userbtn"

        val intent = if (userRole == "Financebtn") {
            Intent(this, FinanceHomeActivity::class.java)
        } else {
            Intent(this, UserHomeActivity::class.java)
        }
        startActivity(intent)
        finish() // Close current activity
    }

    // Example function to determine user role (you can implement your own logic)
    private fun determineUserRole(email: String): String {
        // Replace with your logic to determine if the user is a "Financebtn" or "Userbtn"
        return if (email.endsWith("@finance.com")) "Financebtn" else "Userbtn"
    }

    override fun onBackPressed() {
        // Allow back navigation to WelcomeActivity if the user is not logged in successfully
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
