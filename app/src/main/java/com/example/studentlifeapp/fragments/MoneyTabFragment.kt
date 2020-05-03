package com.example.studentlifeapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.*
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.RepeatType
import com.example.studentlifeapp.data.Transaction
import com.example.studentlifeapp.data.TransactionType
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_money_tab.*
import kotlinx.android.synthetic.main.transaction_item.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.floor


class TransactionAdapter(private var transactions: MutableList<Transaction> = mutableListOf(), val onClick: (Transaction) ->Unit):
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>(){


    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(viewHolder: TransactionViewHolder, position: Int) {
        viewHolder.bind(transactions[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(parent.inflate(R.layout.transaction_item))
    }

    //    inner class SubjectViewHolder(inflater : LayoutInflater,parent:ViewGroup):RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item,parent,false)){
    inner class TransactionViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        init{
            itemView.setOnClickListener {
                onClick(transactions[adapterPosition])
            }
        }
        fun bind(transaction: Transaction){
            when (transaction.type){
                TransactionType.EXPENSE -> {
                    transaction_view_icon.setImageResource(R.drawable.ic_remove)
//                    event_view_icon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                    transaction_view_icon.setColorFilter(Color.rgb(220,20,60), PorterDuff.Mode.SRC_IN)

                }
                TransactionType.INCOME -> {
                    transaction_view_icon.setImageResource(R.drawable.ic_add)
                    transaction_view_icon.setColorFilter(Color.rgb(50,205 ,50), PorterDuff.Mode.SRC_IN)
//                    event_view_icon.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)

                }
            }
            val formatter = DateTimeFormatter.ofPattern("dd\nMMM")

            val moneyString =transaction.amount.toString()
            transaction_view_title.text = moneyString

            transaction_view_name.text = transaction.name
            transaction_view_time.text = formatter.format(transaction.date)
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

    interface TransactionClickedListener{
        fun transactionClicked(transaction: Transaction)
    }
    private var balance = 0.0
    private var weeklyBudget = 0.0
    private var monthIncome = 0.0
    private var monthExpense = 0.0
    private lateinit var viewModel: TransactionsViewModel
    private var expenseTransactions = mutableListOf<Transaction>()
    private var incomeTransactions = mutableListOf<Transaction>()

    private var expenseAdapter: TransactionAdapter = TransactionAdapter(expenseTransactions){transaction:Transaction -> transactionClicked(transaction) }
    private var incomeAdapter: TransactionAdapter = TransactionAdapter(incomeTransactions){transaction:Transaction -> transactionClicked(transaction) }
    private lateinit var listener: ListenerRegistration
    private var transactions = mutableListOf<Transaction>()


    private lateinit var transactionAddClickListener: TransactionAddClickListener
    private lateinit var transactionClickedListener: TransactionClickedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TransactionAddClickListener){
            transactionAddClickListener = context
        }else {
            throw ClassCastException(
                "$context must implement TransactionAddClickListener"
            )

        }
        if (context is TransactionClickedListener){
            transactionClickedListener = context
        }else {
            throw ClassCastException(
                "$context must implement TransactionClickedListener"
            )

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_money_tab, container, false)
        viewModel = activity?.run{
            ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        balance = 0.0
        transactions.clear()
        expenseTransactions.clear()
        incomeTransactions.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        money_tab_current.text = getString(R.string.money_holder,balance.toFloat())
        money_tab_weekly.text = getString(R.string.money_holder,weeklyBudget.toFloat())
        DatabaseManager().getDatabase().get().addOnSuccessListener {document->
            if (document.getDouble("current_balance") != null){
                balance = document.getDouble("current_balance")?: 0.0
                money_tab_current.text = getString(R.string.money_holder, balance)
                if (balance<0){
                    money_tab_current.setTextColor(Color.rgb(220,20,60))
                }else{
                    money_tab_current.setTextColorRes(R.color.secondaryTextColor)
                }
                calculateWeekly()
            }
            listener = transactionListener()
        }
//
//        expenseAdapter = TransactionAdapter(expenseTransactions)
//        incomeAdapter = TransactionAdapter(incomeTransactions)

        next_income_recycler.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        next_income_recycler.adapter = incomeAdapter
        next_income_recycler.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        incomeAdapter.notifyDataSetChanged()

        upcoming_expenses_recycler.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        upcoming_expenses_recycler.adapter = expenseAdapter
        upcoming_expenses_recycler.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        expenseAdapter.notifyDataSetChanged()

        viewModel.transactions.observe(viewLifecycleOwner, Observer { viewTransactions->
            viewTransactions.sortBy{it.date}
            expenseTransactions.clear()
            incomeTransactions.clear()
            monthExpense = 0.0
            monthIncome = 0.0

            for(transaction in viewTransactions){
                if (transaction.date.isAfter(LocalDateTime.now().plusDays(32))) {
                    if (transaction.type == TransactionType.INCOME){
                        incomeTransactions.add(transaction)
                        incomeAdapter.notifyDataSetChanged()
                    }else{
                        break
                    }
                }
                else if(transaction.date.isAfter(LocalDateTime.of(LocalDate.now(),LocalTime.of(23,58)))){
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
                expenseAdapter.refreshList(expenseTransactions)
                incomeAdapter.refreshList(incomeTransactions)

            }
            calculateWeekly()


        })

        fab_money_tab_add.setOnClickListener{
            val textInput = EditText(context)
            val builder = moneyInputBuilder(requireContext(), textInput, "Add Money")
            builder.setPositiveButton("Add") { _, _->
                val amount = textInput.text.toString().toDouble()
                val transaction = Transaction(
                    amount = amount, date = LocalDateTime.now().minusMinutes(5),
                    type = TransactionType.INCOME, completed = true
                )
                addTransaction(transaction)
            }
            builder.show()

        }
        fab_money_tab_minus.setOnClickListener{
            val textInput = EditText(context)
            val builder = moneyInputBuilder(requireContext(), textInput, "Minus Money")
            builder.setPositiveButton("Minus") { _, _->
                val amount = textInput.text.toString().toDouble()
                val transaction = Transaction(
                    amount = amount, date = LocalDateTime.now().minusMinutes(5),
                    type = TransactionType.EXPENSE, completed = true
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
                .setMessage("Weekly budget is calculated based on your monthly income and expense. It does not take into account income after a month.\n" +
                        "\nThe weekly budget is a guideline of roughly how much you can afford to spend each week on things outside of bills. \n" +
                        "\nThe amount here is just a guideline and money spending decisions should be carefully considered yourself")
                .setIcon(R.drawable.ic_info)
                .setPositiveButton("OK"){dialog,_->
                    dialog.cancel()

                }
                .show()
        }

    }

    private fun transactionClicked(transaction:Transaction){
        transactionClickedListener.transactionClicked(transaction)
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
        if (balance<0){
            money_tab_current.setTextColor(Color.rgb(220,20,60))
        }else{
            money_tab_current.setTextColorRes(R.color.secondaryTextColor)
        }
    }

    private fun calculateWeekly(){
        var monthlyMoney = (balance*0.25) + monthIncome - monthExpense
        weeklyBudget = if (monthlyMoney>0&&balance>0)monthlyMoney/4.4
                        else 0.0
        weeklyBudget = 5*(floor(abs(weeklyBudget/5)))
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

                    when(docChange.type){
                        DocumentChange.Type.ADDED ->{
                            transactions.add(transaction)
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
                                            val nextInstance = transaction.copy(date = newDay!!, transactionRef = "")
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
//                            transactions.removeIf{it.transactionRef == transaction.transactionRef}
                            val removeIndex = transactions.indexOf(transactions.find { it.transactionRef == transaction.transactionRef })
                            val originalTransaction = transactions[removeIndex]
                            transactions.remove(originalTransaction)
                        }
                        DocumentChange.Type.MODIFIED->{
                            val replaceIndex = transactions.indexOf(transactions.find { it.transactionRef == transaction.transactionRef })
                            transactions[replaceIndex] = transaction
                        }
                    }
                }
                viewModel.setTransactions(transactions.toMutableList())
                expenseAdapter.notifyDataSetChanged()
                incomeAdapter
                calculateWeekly()
            }
    }

}




