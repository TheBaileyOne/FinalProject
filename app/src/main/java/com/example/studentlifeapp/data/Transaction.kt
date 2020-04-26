package com.example.studentlifeapp.data

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import org.threeten.bp.LocalDate
import com.example.studentlifeapp.toTimeStamp
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.lang.Exception


data class Transaction(
    var name: String = "", var amount:Double, var date: LocalDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX), var completed:Boolean = true,
    var type:TransactionType, var repeatNumber:Int = 0, var repeatType: RepeatType = RepeatType.NEVER, var transactionRef:String = ""
){
    fun addToDatabase(activity: Activity? = null){
        val data = hashMapOf(
            "name" to name,
            "amount" to amount,
            "type" to type,
            "date" to date.toTimeStamp(),
            "completed" to completed,
            "repeat_number" to repeatNumber,
            "repeat_type" to repeatType
        )
        DatabaseManager().getDatabase().collection("transactions").add(data)
            .addOnSuccessListener {
                transactionRef = it.id
                Log.d(TAG, "Transaction added")

                if(activity != null){
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
                }
                activity?.onBackPressed()
            }
            .addOnFailureListener { e->
                Log.w(TAG, "Transaction add error: $e")
                if(activity != null){
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
                }
                activity?.onBackPressed()
            }
    }

    fun update(){
        val data = mapOf("name" to name,
            "amount" to amount,
            "type" to type,
            "date" to date.toTimeStamp(),
            "completed" to completed,
            "repeat_number" to repeatNumber,
            "repeat_type" to repeatType)
        DatabaseManager().getDatabase().collection("transactions").document(transactionRef).update(data)
    }

    fun delete(){
        if (transactionRef.isBlank()){
            throw Exception("no databse reference")
        }else{
            DatabaseManager().getDatabase().collection("transactions").document(transactionRef).delete()
        }
    }
}

enum class TransactionType {
    EXPENSE, INCOME
}
enum class RepeatType {
    NEVER, DAYS, WEEKS, MONTHS, YEARS
}