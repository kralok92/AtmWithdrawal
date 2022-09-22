package com.ak.atm.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ak.atm.entity.Bank
import com.ak.atm.entity.Transactions
import com.ak.atm.utils.BankData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AtmViewModel@Inject constructor(private val atmRepository: AtmRepository) : ViewModel() {

    private var currentNotesOf100: Int = 0
    private var currentNotesOf200: Int = 0
    private var currentNotesOf500: Int = 0
    private var currentNotesOf2000: Int = 0

    private var notesOf100 = 0
    private var notesOf200 = 0
    private var notesOf500 = 0
    private var notesOf2000 = 0

    private var totalAmountInBank = 0


    /**
     * bank data
     */
    private val _bankData = MutableLiveData<Bank>()
    val bankData : LiveData<Bank>
      get() = _bankData

    /**
     * transaction data
     */
    private val _transactionsData = MutableLiveData<List<Transactions>>()
    val transactionsData : LiveData<List<Transactions>>
    get() = _transactionsData

    /**
     * valid withdrawal
     */
    private val _validWithdrawAmount = MutableLiveData<String>()
    val validWithdrawAmount : LiveData<String>
    get() = _validWithdrawAmount


    val etWithdrawAmount = MutableLiveData<String>()
    val errorMessage = MutableLiveData("")


    fun validateAmount() {
       if (isValid()){
           withDrawCalculator(getWithdrawAmount())
           etWithdrawAmount.postValue("")
           _validWithdrawAmount.postValue(getWithdrawAmount().toString())
           errorMessage.value = ""
       }

    }

    fun setupInitialBankDetails() {

        viewModelScope.launch(Dispatchers.IO) {

            val initialBankData = atmRepository.getBankDetails()

            if (initialBankData == null) {

                atmRepository.addNotesToBank(
                    Bank(
                        BankData.BANK_ID,
                        BankData.BANK_BALANCE,
                        BankData.NOTES_OF_100,
                        BankData.NOTES_OF_200,
                        BankData.NOTES_OF_500,
                        BankData.NOTES_OF_2000
                    )
                )

            }
            val updatedBankData = atmRepository.getBankDetails()
            notesOf100 = updatedBankData.notesOf100
            notesOf200 = updatedBankData.notesOf200
            notesOf500 = updatedBankData.notesOf500
            notesOf2000 = updatedBankData.notesOf2000
            totalAmountInBank = updatedBankData.totalAmount
            _bankData.postValue(updatedBankData)

        }

    }

    fun fetchTransactionsList() {
        viewModelScope.launch(Dispatchers.IO) {
            _transactionsData.postValue(atmRepository.getAllTransactions())
        }
    }



    fun addNewTransactionToDB(withdrawAmount: Int) {

        viewModelScope.launch(Dispatchers.IO) {

            atmRepository.addTransactions(
                Transactions(
                    transAmount = withdrawAmount,
                    notesOf100 = currentNotesOf100,
                    notesOf200 = currentNotesOf200,
                    notesOf500 = currentNotesOf500,
                    notesOf2000 = currentNotesOf2000
                )
            )
            clearCurrentNotes()
            fetchTransactionsList()
        }

    }

    fun updateBankDetails(withdrawAmount: Int) {

        viewModelScope.launch(Dispatchers.IO) {

            totalAmountInBank -= withdrawAmount

            atmRepository.addNotesToBank(
                Bank(
                    BankData.BANK_ID,
                    totalAmountInBank,
                    notesOf100,
                    notesOf200,
                    notesOf500,
                    notesOf2000
                )
            )
            setupInitialBankDetails()
        }

    }

    private fun withDrawCalculator(withdrawAmount: Int) {

        var updatedAmount = withdrawAmount

        when {
            updatedAmount >= 2000 && notesOf2000 > 0 -> {
                updatedAmount -= 2000
                notesOf2000 -= 1
                currentNotesOf2000 += 1
                withDrawCalculator(updatedAmount)
            }
            updatedAmount >= 500 && notesOf500 > 0 -> {
                updatedAmount -= 500
                notesOf500 -= 1
                currentNotesOf500 += 1
                withDrawCalculator(updatedAmount)
            }
            updatedAmount >= 200 && notesOf200 > 0 -> {
                updatedAmount -= 200
                notesOf200 -= 1
                currentNotesOf200 += 1
                withDrawCalculator(updatedAmount)
            }
            updatedAmount >= 100 && notesOf100 > 0 -> {
                updatedAmount -= 100
                notesOf100 -= 1
                currentNotesOf100 += 1
                withDrawCalculator(updatedAmount)
            }
        }
    }

    private fun clearCurrentNotes() {
        currentNotesOf2000 = 0
        currentNotesOf500 = 0
        currentNotesOf200 = 0
        currentNotesOf100 = 0
    }


    private fun isValid():Boolean{
        var valid = true
        if (etWithdrawAmount.value.isNullOrEmpty()){
            errorMessage.value = "Please enter withdraw amount."
            valid = false
        }else if (getWithdrawAmount() >= totalAmountInBank){
            errorMessage.value = "insufficient balance in your account."
            valid = false
        }else if (getWithdrawAmount() == 0 || getWithdrawAmount() % 100 != 0){
            errorMessage.value = "Please enter amount in multiples of 100."
            valid = false
        }
       return  valid
    }

    private fun getWithdrawAmount():Int = Integer.parseInt(etWithdrawAmount.value.toString())

}