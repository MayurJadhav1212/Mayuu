package com.example.fundflow


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize shared preferences
        val sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val selectedRole = sharedPref.getString("userRole", null)

        // Automatically redirect users to their respective home page based on their role
        when (selectedRole) {
            "Financebtn" -> {
                startActivity(Intent(this, FinanceLoginActivity::class.java))
                finish()
                return  // Prevent further execution
            }
            "Userbtn" -> {
                startActivity(Intent(this,UserLoginActivity::class.java))
                finish()
                return  // Prevent further execution
            }
            else -> {
                // If no selection has been made, show the Welcome page
                setContentView(R.layout.activity_welcome)
            }
        }

        // Login button for User
        val loginButton: Button = findViewById(R.id.Userbtn)
        loginButton.setOnClickListener {
            val intent = Intent(this,UserLoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Create Account button for Finance
        val createAccountButton: Button = findViewById(R.id.Financebtn)
        createAccountButton.setOnClickListener {
            val intent = Intent(this, FinanceLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
