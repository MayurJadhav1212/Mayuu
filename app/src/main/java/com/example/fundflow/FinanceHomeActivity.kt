package com.example.fundflow

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.appcompat.widget.Toolbar


class FinanceHomeActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var fab: FloatingActionButton
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_home)

        // Initializing Views
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)  // Set the toolbar as the app bar

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        fab = findViewById(R.id.fab)

        // Set up the ViewPager with the FragmentAdapter
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3  // Three tabs: All, Received, Pending
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> AllFragment()     // All transactions
                    1 -> ReceivedFragment()  // Received transactions
                    2 -> PendingFragment()   // Pending transactions
                    else -> throw IllegalArgumentException("Invalid position") // Better error handling
                }
            }
        }



        // Connecting the TabLayout with the ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "All"
                1 -> tab.text = "Received"
                2 -> tab.text = "Pending"
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

    // Inflate the menu with the profile icon
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_menu, menu)
        return true
    }

    // Handle profile icon click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                // Handle profile icon click
                val intent = Intent(this, Profile1Activity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
