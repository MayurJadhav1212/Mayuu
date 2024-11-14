import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fundflow.Loan
import com.example.fundflow.R

class LoansAdapter(private val loanList: List<Loan>) : RecyclerView.Adapter<LoansAdapter.LoanViewHolder>() {

    inner class LoanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.loan_title)
        val amount: TextView = itemView.findViewById(R.id.loan_amount)
        val interestRate: TextView = itemView.findViewById(R.id.interest_rate)
        val monthlyPayment: TextView = itemView.findViewById(R.id.monthly_payment)
        val repaymentTerm: TextView = itemView.findViewById(R.id.repayment_term)
        val viewDetailsButton: Button = itemView.findViewById(R.id.view_details_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_loan, parent, false)
        return LoanViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val loan = loanList[position]
        holder.title.text = loan.title
        holder.amount.text = loan.amount
        holder.interestRate.text = loan.interestRate
        holder.monthlyPayment.text = loan.monthlyPayment
        holder.repaymentTerm.text = loan.repaymentTerm

        holder.viewDetailsButton.setOnClickListener {
            // Handle button click for viewing loan details
        }
    }

    override fun getItemCount(): Int {
        return loanList.size
    }
}
