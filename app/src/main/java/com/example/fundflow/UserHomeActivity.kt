package com.example.fundflow

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class UserHomeActivity : AppCompatActivity() {

    private  lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_home)



        bottomNavigationView= findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener {  menuItem ->
            when(menuItem.itemId){
                R.id.bottom_loans ->{
                    replaceFragment(LoansFragment())
                    true
                }
                R.id.bottom_transaction ->{
                    replaceFragment(TransactionFragment())
                    true
                }
                R.id.bottom_profile ->{
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
replaceFragment(LoansFragment())

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}

