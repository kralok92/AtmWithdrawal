package com.ak.atm.viewModel


import com.ak.atm.dao.AtmDao
import com.ak.atm.entity.Bank
import com.ak.atm.entity.Transactions
import javax.inject.Inject

class AtmRepository@Inject constructor(private val atmDao: AtmDao) {

    suspend fun getBankDetails() : Bank = atmDao.getBankDetails()

    suspend fun addNotesToBank(bank: Bank) = atmDao.addNotesToBank(bank)

    suspend fun getAllTransactions() : List<Transactions> = atmDao.getAllTransactions()

    suspend fun addTransactions(transactions: Transactions) = atmDao.addTransactions(transactions)

}