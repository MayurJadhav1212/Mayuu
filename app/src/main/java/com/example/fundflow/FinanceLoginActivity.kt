package com.example.fundflow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class FinanceLoginActivity : AppCompatActivity() {

    // Initialize FirebaseAuth instance
    private lateinit var auth: FirebaseAuth
    private val TAG = "FinanceLoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is already logged in
        val sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // User is already logged in, redirect to home activity
            val intent = Intent(this, FinanceHomeActivity::class.java)
            startActivity(intent)
            finish()
            return  // Skip the login UI
        }

        setContentView(R.layout.activity_finance_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Find the views by their IDs
        val emailInput: EditText = findViewById(R.id.email)
        val passwordInput: EditText = findViewById(R.id.password)
        val forgotPasswordText: TextView = findViewById(R.id.forgotPassword)
        val loginButton: Button = findViewById(R.id.loginButton)
        val rememberMeCheckbox: CheckBox = findViewById(R.id.rememberMe)
        val signUpText: TextView = findViewById(R.id.signUpText)
        val showPassword: CheckBox = findViewById(R.id.show_password)
        val backarow: ImageView =  findViewById(R.id.back)
        // Load saved email if "Remember Me" was checked
        val savedEmail = sharedPref.getString("savedEmail", "")
        emailInput.setText(savedEmail)

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
                // Firebase authentication logic
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                            // Save login state to SharedPreferences
                            sharedPref.edit().apply {
                                putBoolean("isLoggedIn", true)  // Mark as logged in
                                putString("userRole", "Financebtn")
                                apply()
                            }

                            // Save email if "Remember Me" is checked
                            if (rememberMeCheckbox.isChecked) {
                                sharedPref.edit().putString("savedEmail", email).apply()
                            } else {
                                sharedPref.edit().remove("savedEmail").apply()
                            }

                            // Redirect to the respective home activity
                            val intent = Intent(this, FinanceHomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle forgot password click
        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
            // Add logic to navigate to a reset password activity if needed
        }

        backarow.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

        // Handle sign-up text click
        signUpText.setOnClickListener {
            val intent = Intent(this, FinanceRegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Optionally handle "Remember Me" checkbox
        rememberMeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Remember Me checked", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Remember Me unchecked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        // Prevent back press from taking user to WelcomeActivity
        moveTaskToBack(true)  // Minimize the app instead
    }
}
