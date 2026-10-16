package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText

class HistoryActivity : ComponentActivity() {

    private lateinit var adapter: TransactionAdapter
    private val fullList = mutableListOf<TransactionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history)

        val currentUser = SessionManager.currentUser
        val rvTransactions = findViewById<RecyclerView>(R.id.rvTransactions)
        val etSearch = findViewById<TextInputEditText>(R.id.etSearch)
        val tvEmptyHistory = findViewById<TextView>(R.id.tvEmptyHistory)

        val userTransactions = SessionManager.transactionsList.filter {
            currentUser == null || it.senderUid == currentUser.uid || it.receiverUid == currentUser.uid || it.senderPhone == currentUser.phone || it.receiverPhone == currentUser.phone
        }

        fullList.clear()
        fullList.addAll(userTransactions)

        if (fullList.isEmpty()) {
            tvEmptyHistory?.visibility = View.VISIBLE
            rvTransactions?.visibility = View.GONE
        } else {
            tvEmptyHistory?.visibility = View.GONE
            rvTransactions?.visibility = View.VISIBLE
        }

        adapter = TransactionAdapter(fullList)
        rvTransactions?.layoutManager = LinearLayoutManager(this)
        rvTransactions?.adapter = adapter

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim()?.lowercase().orEmpty()
                val filtered = if (query.isEmpty()) {
                    userTransactions
                } else {
                    userTransactions.filter {
                        it.description.lowercase().contains(query) || it.receiverPhone.contains(query) || it.amount.toString().contains(query)
                    }
                }
                adapter.updateList(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        setupBottomNavigation(R.id.nav_history)
    }

    private fun setupBottomNavigation(selectedItemId: Int) {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView) ?: return
        bottomNavigation.selectedItemId = selectedItemId
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_scan -> {
                    if (selectedItemId != R.id.nav_scan) {
                        startActivity(Intent(this, ScanActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_send -> {
                    if (selectedItemId != R.id.nav_send) {
                        startActivity(Intent(this, SendActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_history -> true
                R.id.nav_profile -> {
                    if (selectedItemId != R.id.nav_profile) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private class TransactionAdapter(private var list: List<TransactionItem>) :
        RecyclerView.Adapter<TransactionAdapter.TxViewHolder>() {

        fun updateList(newList: List<TransactionItem>) {
            this.list = newList
            notifyDataSetChanged()
        }

        class TxViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTitle: TextView = view.findViewById(R.id.tvTransactionTitle)
            val tvDate: TextView = view.findViewById(R.id.tvTransactionDate)
            val tvAmount: TextView = view.findViewById(R.id.tvTransactionAmount)
            val tvStatus: TextView = view.findViewById(R.id.tvTransactionStatus)
            val ivIcon: ImageView = view.findViewById(R.id.ivTransactionIcon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TxViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
            return TxViewHolder(view)
        }

        override fun onBindViewHolder(holder: TxViewHolder, position: Int) {
            val tx = list[position]
            holder.tvTitle.text = tx.description.ifEmpty { "Transfer" }
            holder.tvDate.text = tx.createdAt.ifEmpty { "Recent" }
            holder.tvAmount.text = "Rs. ${tx.amount}"
            holder.tvStatus.text = "Completed"

            if (tx.type == "QR_REDEEM" || tx.type == "RECEIVED") {
                holder.tvAmount.setTextColor(0xFF16A34A.toInt())
                holder.tvAmount.text = "+ Rs. ${tx.amount}"
            } else {
                holder.tvAmount.setTextColor(0xFFDC2626.toInt())
                holder.tvAmount.text = "- Rs. ${tx.amount}"
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
