package com.example.studentlifeapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Transaction
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MoneyManagerPagerAdapter(fragment:Fragment):FragmentStateAdapter(fragment){
    val fragments:MutableList<Fragment> = mutableListOf()

    override fun getItemCount():Int = 2

    override fun createFragment(position: Int): Fragment {
        val returnFragment = when(position){
            0 -> MoneyTabFragment()
            else -> TransactionTabFragment()
        }
        fragments.add(returnFragment)
        return returnFragment
    }
}
class MoneyManagerFragment : Fragment() {

    private lateinit var moneyManagerAdapter: MoneyManagerPagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_money_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout: TabLayout = view.findViewById(R.id.money_manager_tabs)
        moneyManagerAdapter = MoneyManagerPagerAdapter(this)
        viewPager = view.findViewById(R.id.money_manager_pager)
        viewPager.adapter = moneyManagerAdapter
        viewPager.isSaveEnabled = false

        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            tab.text = when(position){
                0->"Money"
                1->"History"
                else ->"error"
            }
        }.attach()


    }

}

class TransactionsViewModel : ViewModel(){
    val transactions: MutableLiveData<MutableList<Transaction>> = MutableLiveData()
    init {
        setTransactions(mutableListOf())
    }
    fun getTransactions() = transactions.value
    fun setTransactions(transactions:MutableList<Transaction>){
        this.transactions.value = transactions
    }
//    fun addTransaction(transaction:Transaction){
//        this.transactions.value.add(transaction)
//    }
}
