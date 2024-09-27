package com.example.fundflow

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FinanceHomeActivity : AppCompatActivity() {

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
                    0 -> AllFragment()     // All transactions
                    1 -> ReceivedFragment()  // Received transactions
                    2 -> PendingFragment()   // Pending transactions
                    else -> AllFragment() // Default
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

        // Set the status bar color based on the current theme
        setStatusBarColor()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this,
                if (isInDarkMode()) R.color.grey else R.color.blue)
        }
    }

    private fun isInDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}
