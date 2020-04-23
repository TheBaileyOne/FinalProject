package com.example.studentlifeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Transaction
import kotlinx.android.synthetic.main.fragment_subjects_tab.*
import kotlinx.android.synthetic.main.fragment_transaction_tab.*
import org.threeten.bp.LocalDateTime


class TransactionTabFragment : Fragment() {
    private var transactions = mutableListOf<Transaction>()

    private var transactionAdapter = TransactionAdapter(transactions)

    private lateinit var viewModel: TransactionsViewModel

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

        viewModel.transactions.observe(this, Observer { viewTransactions ->
            viewTransactions.sortByDescending{it.date}
            for (transaction in viewTransactions){
                if (transaction.date.isBefore(LocalDateTime.now())){
                    transactions.add(transaction)
                }
            }
            transactionAdapter.refreshList(transactions)
            transactionAdapter.notifyDataSetChanged()
        })
    }

}
