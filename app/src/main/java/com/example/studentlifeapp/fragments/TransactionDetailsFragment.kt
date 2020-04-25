package com.example.studentlifeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.RepeatType
import com.example.studentlifeapp.data.Transaction
import com.example.studentlifeapp.data.TransactionType
import com.example.studentlifeapp.hideKeyboard
import kotlinx.android.synthetic.main.fragment_transaction_details.*
import org.threeten.bp.format.DateTimeFormatter

class TransactionDetailsFragment(val transaction: Transaction) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val formatter= DateTimeFormatter.ofPattern("EEE, dd MMM")
        if (transaction.name.isNullOrBlank()){
            transaction_details_name_view.visibility = View.GONE
        }else{
            transaction_details_name.text = getString(R.string.placeholder_string, transaction.name)
        }
        transaction_details_type.text = getString(R.string.placeholder_string, if(transaction.type == TransactionType.EXPENSE) "Expense" else "Income")
        transaction_details_date.text = getString(R.string.placeholder_string, formatter.format(transaction.date))
        if(transaction.repeatType == RepeatType.NEVER){
            transaction_details_repeat.text = getString(R.string.placeholder_string, "Never")
            button_stop_recurrance.visibility = View.GONE
        }else{
            transaction_details_repeat.text = getString(R.string.placeholder_string, "Every ${transaction.repeatNumber} " +
                    when(transaction.repeatType){
                        RepeatType.NEVER ->"Never"
                        RepeatType.YEARS -> "Years"
                        RepeatType.MONTHS -> "Months"
                        RepeatType.WEEKS -> "Weeks"
                        RepeatType.DAYS -> "Days"
                }
            )
        }

        button_delete_transaction.setOnClickListener {
            transaction.delete()
//            hideKeyboard()
            activity?.onBackPressed()
        }

        button_stop_recurrance.setOnClickListener {
            transaction.completed = true
            transaction.repeatNumber = 0
            transaction.repeatType = RepeatType.NEVER
            transaction.update()
            hideKeyboard()
            activity?.onBackPressed()
        }

        transaction_details_back.setOnClickListener {
            activity?.onBackPressed()
            hideKeyboard()
            activity?.hideKeyboard()
        }
    }


}
