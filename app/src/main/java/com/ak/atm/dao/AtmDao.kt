package com.ak.atm.dao

import androidx.room.*
import com.ak.atm.entity.Bank
import com.ak.atm.entity.Transactions

@Dao
interface AtmDao {

    @Query("Select * from bank")
    suspend fun getBankDetails(): Bank

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotesToBank(bank: Bank)

    @Query("Select * from transactions")
    suspend fun getAllTransactions(): List<Transactions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTransactions(transactions: Transactions)

}