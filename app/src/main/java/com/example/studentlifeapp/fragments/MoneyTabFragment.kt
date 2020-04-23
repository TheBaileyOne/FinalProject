package com.example.studentlifeapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.RepeatType
import com.example.studentlifeapp.data.Transaction
import com.example.studentlifeapp.data.TransactionType
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.tolocalDateTime
import com.example.studentlifeapp.util.addDecimalLimiter
import com.example.studentlifeapp.util.decimalLimiter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_item_view.*
import kotlinx.android.synthetic.main.fragment_add_transaction.*
import kotlinx.android.synthetic.main.fragment_money_tab.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.w3c.dom.Text
import java.lang.ClassCastException


class TransactionAdapter(private var transactions: MutableList<Transaction> = mutableListOf()):
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>(){


    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(viewHolder: TransactionViewHolder, position: Int) {
        viewHolder.bind(transactions[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(parent.inflate(R.layout.event_item_view))
    }

    //    inner class SubjectViewHolder(inflater : LayoutInflater,parent:ViewGroup):RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item,parent,false)){
    inner class TransactionViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        init{
//            itemView.setOnClickListener {
//                onClick(transactions[adapterPosition])
//            }
        }
        fun bind(transaction: Transaction){
            when (transaction.type){
                TransactionType.EXPENSE -> {
                    event_view_icon.setImageResource(R.drawable.ic_remove)
                    event_view_icon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)

                }
                TransactionType.INCOME -> {
                    event_view_icon.setImageResource(R.drawable.ic_add)
                    event_view_icon.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)

                }
            }
            val formatter = DateTimeFormatter.ofPattern("dd\nMMM\nYYYY")

            val moneyString =transaction.amount.toString()
            event_view_title.text = moneyString

            event_view_location.text = transaction.name
            event_view_time.text = formatter.format(transaction.date)
        }
    }
    fun refreshList(newTransactions:MutableList<Transaction>){
        transactions = newTransactions
        this.notifyDataSetChanged()
    }
}

class MoneyTabFragment : Fragment() {

    interface TransactionAddClickListener{
        fun transactionAddClick()
    }
    private var balance = 0.0
    private var weeklyBudget = 0.0
    private var monthIncome = 0.0
    private var monthExpense = 0.0
    private lateinit var viewModel: TransactionsViewModel
    private var expenseTransactions = mutableListOf<Transaction>()
    private var incomeTransactions = mutableListOf<Transaction>()

    private var expenseAdapter: TransactionAdapter = TransactionAdapter(expenseTransactions)
    private var incomeAdapter: TransactionAdapter = TransactionAdapter(incomeTransactions)
    private lateinit var listener: ListenerRegistration
    private var transactions = mutableListOf<Transaction>()


    private lateinit var transactionAddClickListener: TransactionAddClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TransactionAddClickListener){
            transactionAddClickListener = context
        }else {
            throw ClassCastException(
                "$context must implement TransactionAddClickListener"
            )

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_money_tab, container, false)
        viewModel = activity?.run{
            ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
        }?: throw Exception("Invalid Activity")


        DatabaseManager().getDatabase().get().addOnSuccessListener {document->
            if (document.getDouble("current_balance") != null){
                updateBalance(document.getDouble("current_balance")!!)
            }
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        transactions.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        money_tab_current.text = getString(R.string.money_holder,balance.toFloat())
        money_tab_weekly.text = getString(R.string.money_holder,weeklyBudget.toFloat())

        expenseAdapter = TransactionAdapter(expenseTransactions)
        incomeAdapter = TransactionAdapter(incomeTransactions)
        listener = transactionListener()

        next_income_recycler.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        next_income_recycler.adapter = incomeAdapter
        next_income_recycler.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        incomeAdapter.notifyDataSetChanged()

        upcoming_expenses_recycler.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        upcoming_expenses_recycler.adapter = expenseAdapter
        upcoming_expenses_recycler.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        expenseAdapter.notifyDataSetChanged()


        fab_money_tab_add.setOnClickListener{
            val textInput = EditText(context)
            val builder = moneyInputBuilder(context!!, textInput, "Add Money")
            builder.setPositiveButton("Add") { _, _->
                val amount = textInput.text.toString().toDouble()
                val transaction = Transaction(
                    amount = amount, date = LocalDateTime.now(),type = TransactionType.INCOME
                )
                addTransaction(transaction)
            }
            builder.show()

        }
        fab_money_tab_minus.setOnClickListener{
            val textInput = EditText(context)
            val builder = moneyInputBuilder(context!!, textInput, "Minus Money")
            builder.setPositiveButton("Minus") { _, _->
                val amount = textInput.text.toString().toDouble()
                val transaction = Transaction(
                    amount = amount, date = LocalDateTime.now(), type = TransactionType.EXPENSE
                )
                addTransaction(transaction)
            }
            builder.show()

        }
        future_transaction_button.setOnClickListener{
            transactionAddClickListener.transactionAddClick()
        }
        money_info.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Money Manager Info")
                .setMessage("Weekly budget is calculated based on our monthly income and expense.\n" +
                        "\nThe weekly budget is a guideline of roughly how much you can afford to spend each week on things outside of bills. " +
                        "\nFor example things like groceries, society activities, socialising\n" +
                        "\nThe amount here is just a guideline and money spending decisions should be carefully considered yourself")
                .setIcon(R.drawable.ic_info)
                .setPositiveButton("OK"){dialog,_->
                    dialog.cancel()

                }
                .show()
        }

    }

    private fun addTransaction(transaction: Transaction){
        transaction.addToDatabase()
        if (transaction.completed){
            if (transaction.type == TransactionType.EXPENSE ){
                updateBalance(0-transaction.amount)
            }else{
                updateBalance(transaction.amount)
            }
        }
        Log.d("Transaction", "Transaction:${transaction.amount}")
    }
    private fun updateBalance(amount:Double){
        this.balance += amount
        money_tab_current.text = getString(R.string.money_holder, balance.toFloat())
        DatabaseManager().getDatabase().update("current_balance", balance)
    }

    private fun calculateWeekly(){
        var monthlyMoney = balance + monthIncome - monthExpense
        monthlyMoney *= 0.95 //slightlty less to make it seem alow for error
        weeklyBudget = if (monthlyMoney>0)monthlyMoney/4.4
                        else 0.0
        money_tab_weekly.text = getString(R.string.money_holder, weeklyBudget.toFloat())
    }

    private fun moneyInputBuilder(context:Context, textInput:EditText, title:String):AlertDialog.Builder{
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)

        textInput.hint = "Amount"
        textInput.addDecimalLimiter()
        textInput.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
        val layoutChild = LinearLayout(context)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutChild.layoutParams = layoutParams
        layoutChild.orientation = LinearLayout.HORIZONTAL
        val textView = TextView(context)
        textView.text = "      £"
        textView.textSize = 16f
        layoutChild.addView(textView)
        layoutChild.addView(textInput)
        builder.setView(layoutChild)
        builder.setCancelable(true)
        return builder
    }

    private fun transactionListener():ListenerRegistration{
        return DatabaseManager().getDatabase().collection("transactions")
            .addSnapshotListener{snapshot,e->
                if (e!= null){
                    return@addSnapshotListener
                }
                for (docChange in snapshot!!.documentChanges){
                    val doc = docChange.document
                    val transaction = Transaction(
                        name = doc.getString("name")!!,
                        amount = doc.getDouble("amount")!!,
                        type = TransactionType.valueOf(doc.getString("type")!!),
                        date = doc.getTimestamp("date")!!.tolocalDateTime(),
                        completed = doc.getBoolean("completed")!!,
                        repeatNumber = doc.getDouble("repeat_number")!!.toInt(),
                        repeatType = RepeatType.valueOf(doc.getString("repeat_type")!!)
                    )
                    transaction.transactionRef = doc.id
                    transactions.add(transaction)
                    when(docChange.type){
                        DocumentChange.Type.ADDED ->{
                            viewModel.getTransactions()?.add(transaction)
                            //Display the transactions within the next month
                            if (transaction.date.isAfter(LocalDateTime.now())
                                && transaction.date.isBefore(LocalDateTime.now().plusDays(32))){
                                when(transaction.type){
                                    TransactionType.EXPENSE ->{
                                        expenseTransactions.add(transaction)
                                        expenseAdapter.notifyDataSetChanged()
                                        monthExpense += transaction.amount
                                    }
                                    TransactionType.INCOME -> {
                                        incomeTransactions.add(transaction)
                                        incomeAdapter.notifyDataSetChanged()
                                        monthIncome += transaction.amount
                                    }
                                }
                            }
                            if(!transaction.completed&&transaction.date.isBefore(LocalDateTime.now())){
                                DatabaseManager().getDatabase().collection("transactions")
                                    .document(transaction.transactionRef).update("completed",true)
                                    .addOnSuccessListener {
                                        if (transaction.repeatType!=RepeatType.NEVER && transaction.repeatNumber>0){
                                            val repeatLong = transaction.repeatNumber.toLong()
                                            val newDay = when(transaction.repeatType){
                                                RepeatType.DAYS -> transaction.date.plusDays(repeatLong)
                                                RepeatType.WEEKS -> transaction.date.plusWeeks(repeatLong)
                                                RepeatType.MONTHS -> transaction.date.plusMonths(repeatLong)
                                                RepeatType.YEARS -> transaction.date.plusYears(repeatLong)
                                                else -> throw Exception("Error with repeat type")
                                            }
                                            val nextInstance = transaction.copy(date = newDay!!)
                                            nextInstance.addToDatabase()
                                        }
                                        transaction.completed = true

                                    }
                                when (transaction.type){
                                    TransactionType.EXPENSE -> updateBalance(0-transaction.amount)
                                    TransactionType.INCOME -> updateBalance(transaction.amount)
                                }

                            }
                        }
                        DocumentChange.Type.REMOVED->{

                        }
                        else ->{

                        }
                    }
                }
                viewModel.setTransactions(transactions.toMutableList())
            }
    }

}




