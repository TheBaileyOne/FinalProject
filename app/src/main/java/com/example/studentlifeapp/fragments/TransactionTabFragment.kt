package com.example.studentlifeapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Transaction
import kotlinx.android.synthetic.main.fragment_transaction_tab.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime


class TransactionTabFragment : Fragment() {
    private var transactions = mutableListOf<Transaction>()

    private var transactionAdapter = TransactionAdapter(transactions){transaction:Transaction -> transactionClicked(transaction) }

    private lateinit var transactionClickedListener: MoneyTabFragment.TransactionClickedListener

    private lateinit var viewModel: TransactionsViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MoneyTabFragment.TransactionClickedListener){
            transactionClickedListener = context
        }else {
            throw ClassCastException(
                "$context must implement TransactionClickedListener"
            )

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_transaction_tab, container, false)
        viewModel = activity?.run{
            ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
        }?: throw  Exception("Invalid activity")
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        transactions.clear()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transaction_history_recycler_view.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        transaction_history_recycler_view.adapter=transactionAdapter
        transaction_history_recycler_view.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        transactionAdapter.notifyDataSetChanged()

        viewModel.transactions.observe(viewLifecycleOwner, Observer { viewTransactions ->
            viewTransactions.sortByDescending{it.date}
            transactions.clear()
            for (transaction in viewTransactions){
                if (transaction.date.isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59))) && transaction.completed){
                    transactions.add(transaction)
                }
                if (transaction.date.isBefore(LocalDateTime.now().minusMonths(6))){
                    transaction.delete()
                }
            }
            transactionAdapter.refreshList(transactions)
            transactionAdapter.notifyDataSetChanged()
        })
    }
    private fun transactionClicked(transaction:Transaction){
        transactionClickedListener.transactionClicked(transaction)
    }

}
