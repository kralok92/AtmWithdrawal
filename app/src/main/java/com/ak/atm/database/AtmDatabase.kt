package com.ak.atm.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ak.atm.dao.AtmDao
import com.ak.atm.entity.Bank
import com.ak.atm.entity.Transactions

@Database(entities = [Transactions::class, Bank::class], version = 1)
abstract class AtmDatabase : RoomDatabase() {

    abstract fun getAtmDao() : AtmDao
}