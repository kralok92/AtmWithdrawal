package com.ak.atm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ak.atm.databinding.TransactionsItemBinding
import com.ak.atm.entity.Transactions


class TransactionsAdapter(private val mCtx : Context , private val transactionsList: List<Transactions>) :
    RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        TransactionsItemBinding.inflate(LayoutInflater.from(mCtx), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.transactions = transactionsList[position]
    }

    override fun getItemCount() = transactionsList.size


    class ViewHolder(val binding : TransactionsItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}