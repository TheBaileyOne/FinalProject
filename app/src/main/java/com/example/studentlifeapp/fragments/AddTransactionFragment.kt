package com.example.studentlifeapp.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.*

import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.RepeatType
import com.example.studentlifeapp.data.Transaction
import com.example.studentlifeapp.data.TransactionType
import kotlinx.android.synthetic.main.fragment_add_transaction.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

//Add transactioon to database
class AddTransactionFragment : Fragment() {
    private lateinit var repeatType:RepeatType

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_add_transaction, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        radio_expense.isChecked = true

        val values = enumValues<RepeatType>()
        val spinner = view.findViewById<Spinner>(R.id.spinner_transaction_repeat)
        spinner?.adapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_item, values).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                var type = parent?.getItemAtPosition(position).toString()
                repeatType = RepeatType.valueOf(type)
                if(repeatType == RepeatType.NEVER){
                    add_transaction_repeat_num.visibility = View.GONE
                }else{

                    add_transaction_repeat_num.visibility = View.VISIBLE
                }


            }
        }
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM, YYYY")
        val format = SimpleDateFormat("dd MMM, YYYY", Locale.UK)

        val setDate = view.findViewById<EditText>(R.id.add_transaction_date_edit)
        var startDateDate:String

        setDate.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                startDateDate = format.format(selectedDate.time)
                add_transaction_date_edit.setText(startDateDate)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }


        button_add_transaction.setOnClickListener{
            addTransaction()
        }
        transaction_expand_back.setOnClickListener {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            activity?.onBackPressed()
        }



    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.option_about_app).isVisible = false
        menu.findItem(R.id.option_logout).isVisible = false

    }
    private fun addTransaction(){
        if(add_transaction_amount_edit.text.isNullOrBlank()||add_transaction_name_edit.text.isNullOrBlank()||
            add_transaction_date_edit.text.isNullOrBlank()|| (add_transaction_repeat_num.text.isNullOrBlank()&& repeatType != RepeatType.NEVER)){
            Toast.makeText(context, "Please fill in all compulsory fields", Toast.LENGTH_LONG).show()

        }else{
            val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm")
            val name = add_transaction_name_edit.text.toString()
            val amount = add_transaction_amount_edit.text.toString().toDouble()
            val date =  LocalDateTime.parse("${add_transaction_date_edit.text} 23:59", formatter)
            val repeat= if (add_transaction_repeat_num.text.isNullOrBlank() || repeatType ==RepeatType.NEVER) 0
                                else add_transaction_repeat_num.text.toString().toInt()
            val type = if(radio_income.isChecked) TransactionType.INCOME else TransactionType.EXPENSE
            val completed = repeat<=0

            val transaction = Transaction(name,amount, date, completed, type, repeat, repeatType)
            transaction.addToDatabase(activity)
        }
    }

}
