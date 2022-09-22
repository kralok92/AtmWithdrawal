package com.ak.atm

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ak.atm.adapter.TransactionsAdapter
import com.ak.atm.databinding.ActivityHomeBinding
import com.ak.atm.entity.Bank
import com.ak.atm.entity.Transactions
import com.ak.atm.viewModel.AtmViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val atmViewModel : AtmViewModel by viewModels()
    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    private lateinit var mCtx : Context
    private val transactionsList by lazy {
        arrayListOf<Transactions>()
    }
    private lateinit var transactionsAdapter : TransactionsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mCtx = this
        setUpViewModel()
        setupObserver()
    }

    private fun setUpViewModel(){

        /**
         * initialize transaction adapter
         */
        transactionsAdapter = TransactionsAdapter(mCtx,transactionsList)
        binding.rvTransactions.layoutManager =LinearLayoutManager(mCtx,LinearLayoutManager.VERTICAL,false)
        binding.rvTransactions.adapter = transactionsAdapter

        /**
         * setup viewModel
         */
        binding.viewModel = atmViewModel
        binding.lifecycleOwner = this
        atmViewModel.setupInitialBankDetails()
        atmViewModel.fetchTransactionsList()


        binding.btnWithdraw.setOnClickListener {
            atmViewModel.validateAmount()
        }


    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setupObserver() {
        /**
         * observe bank data
         */
        atmViewModel.bankData.observe(this, Observer {
            binding.bankNotesLayout.bankData = it
        })


        /**
         * observe transaction
         */
        atmViewModel.transactionsData.observe(this, Observer {

            if (it.isNotEmpty()) {
                transactionsList.clear()
                val trans = it.last()
                val bank = Bank(
                    2,
                    trans.transAmount,
                    trans.notesOf100,
                    trans.notesOf200,
                    trans.notesOf500,
                    trans.notesOf2000
                )

                binding.rlLastTransactions.visibility = View.VISIBLE
                binding.rlAllTransactions.visibility = View.VISIBLE


                binding.lastTransactionLayout.bankData = bank

                /**
                 * update last transaction
                 */
                transactionsList.addAll(it)
                transactionsAdapter.notifyDataSetChanged()
            }

        })

        /**
         * observe notes
          */

        atmViewModel.validWithdrawAmount.observe(this, Observer {

            val withdrawAmount = Integer.parseInt(it)
            atmViewModel.updateBankDetails(withdrawAmount)
            atmViewModel.addNewTransactionToDB(withdrawAmount)
        })

    }



}