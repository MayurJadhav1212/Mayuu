package com.example.fundflow

import LoansAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LoansFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var loanAdapter: LoansAdapter
    private var loanList = listOf(
        Loan("Home Loan", "$200,000", "3.5%", "$1,200", "30 years"),
        Loan("Car Loan", "$25,000", "5.0%", "$500", "5 years"),
        Loan("Student Loan", "$15,000", "4.0%", "$250", "10 years") ,
                Loan("business loan", "$200,000", "3.5%", "$1,200", "30 years"),
    ) // Sample loan data
    private var filteredLoanList = loanList.toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loans, container, false)

        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loanAdapter = LoansAdapter(filteredLoanList)
        recyclerView.adapter = loanAdapter

        // Initialize SearchView
        searchView = view.findViewById(R.id.SearchView)
        setupSearchView()

        return view
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // We handle real-time filtering in onQueryTextChange
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterLoans(newText ?: "")
                return true
            }
        })
    }

    private fun filterLoans(query: String) {
        filteredLoanList.clear()
        if (query.isEmpty()) {
            filteredLoanList.addAll(loanList)
        } else {
            val searchQuery = query.lowercase()
            loanList.forEach { loan ->
                if (loan.title.lowercase().contains(searchQuery)) {
                    filteredLoanList.add(loan)
                }
            }
        }
        loanAdapter.notifyDataSetChanged()
    }
}
