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



class UserLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "UserLoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        val sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            // Redirect to home if already logged in
            startActivity(Intent(this, UserHomeActivity::class.java))
            finish()  // Close the login activity
            return  // Skip setting up the login UI
        }

        // Set the login layout if the user is not logged in
        setContentView(R.layout.activity_user_login)

        // Find views by their IDs
        val emailInput: EditText = findViewById(R.id.email)
        val passwordInput: EditText = findViewById(R.id.password)
        val forgotPasswordText: TextView = findViewById(R.id.forgotPassword)
        val loginButton: Button = findViewById(R.id.loginButton)
        val rememberMeCheckbox: CheckBox = findViewById(R.id.rememberMe)
        val signUpText: TextView = findViewById(R.id.signUpText)
        val showPassword: CheckBox = findViewById(R.id.show_password)
        val backArrow: ImageView = findViewById(R.id.back)

        // Load saved email if "Remember Me" was checked
        emailInput.setText(sharedPref.getString("savedEmail", ""))

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

                            // Save login state
                            with(sharedPref.edit()) {
                                putBoolean("isLoggedIn", true)
                                putString("userRole", "Userbtn")  // Update with actual role if needed
                                apply()
                            }

                            // Save email if "Remember Me" is checked
                            if (rememberMeCheckbox.isChecked) {
                                sharedPref.edit().putString("savedEmail", email).apply()
                            } else {
                                sharedPref.edit().remove("savedEmail").apply()
                            }

                            // Redirect to the home activity
                            startActivity(Intent(this, UserHomeActivity::class.java))
                            finish()
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle forgot password click
        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
            // Logic to navigate to a reset password activity can be added here
        }

        // Handle sign-up text click
        signUpText.setOnClickListener {
            startActivity(Intent(this, UserRegisterActivity::class.java))
            finish()
        }

        // Handle back arrow click
        backArrow.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

        // Handle "Remember Me" checkbox feedback
        rememberMeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Remember Me checked" else "Remember Me unchecked"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        // Prevent back press from taking the user to WelcomeActivity
        moveTaskToBack(true)  // Minimize the app instead
    }
}
