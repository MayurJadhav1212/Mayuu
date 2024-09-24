//package com.example.fundflow
//
//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//
//class UserHomeActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_user_home)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}
package com.example.fundflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class UserHomeActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_home)

        // Initializing Views
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        fab = findViewById(R.id.fab)

        // Set up the ViewPager with the FragmentAdapter
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3  // Three tabs: All, Received, Pending
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> LoansFragment()     // All transactions
                    1 -> TransactionFragment()  // Received transactions
                    2 -> DashbordFragment()   // Pending transactions
                    else -> AllFragment() // Default
                }
            }
        }

        // Connecting the TabLayout with the ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Loans"
                1 -> tab.text = "Transaction"
                2 -> tab.text = "Dasbord"
            }
        }.attach()

        // Handling Floating Action Button (FAB) click
        fab.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> Toast.makeText(this, "New Transaction", Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(this, "Add Received Transaction", Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(this, "Add Pending Transaction", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

