package com.example.fundflow

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FinanceRegisterActivity : AppCompatActivity() {

    // Declare variables for EditTexts and CheckBoxes
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var emailAddress: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var showPassword: CheckBox
    private lateinit var showConfirmPassword: CheckBox
    private lateinit var termsConditions: CheckBox
    private lateinit var registerButton: Button
    private lateinit var backarow: ImageView

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_register)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        firstName = findViewById(R.id.first_name)
        lastName = findViewById(R.id.last_name)
        emailAddress = findViewById(R.id.email_address)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        showPassword = findViewById(R.id.show_password)
        showConfirmPassword = findViewById(R.id.show_confirm_password)
        termsConditions = findViewById(R.id.terms_conditions)
        registerButton = findViewById(R.id.register_button)
        backarow =  findViewById(R.id.back)

        // Set up the checkbox to toggle password visibility
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

        backarow.setOnClickListener {
            startActivity(Intent(this, FinanceLoginActivity::class.java))
            finish()
        }
    }

    // Save user data to Firestore
    private fun saveUserDataToFirestore(userId: String) {
        val user = hashMapOf(
            "firstName" to firstName.text.toString(),
            "lastName" to lastName.text.toString(),
            "email" to emailAddress.text.toString()
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
        if (firstName.text.isEmpty() || lastName.text.isEmpty() || emailAddress.text.isEmpty()
            || password.text.isEmpty() || confirmPassword.text.isEmpty()) {
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

        return true
    }
}
//g