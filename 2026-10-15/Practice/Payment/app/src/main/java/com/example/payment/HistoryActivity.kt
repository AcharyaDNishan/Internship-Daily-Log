package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history)

        val currentUser = SessionManager.currentUser
        val rvTransactions = findViewById<RecyclerView>(R.id.rvTransactions)

        val userTransactions = SessionManager.transactionsList.filter {
            currentUser == null || it.senderUid == currentUser.uid || it.receiverUid == currentUser.uid || it.senderPhone == currentUser.phone || it.receiverPhone == currentUser.phone
        }

        rvTransactions?.layoutManager = LinearLayoutManager(this)
        rvTransactions?.adapter = TransactionAdapter(userTransactions)

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

    private class TransactionAdapter(private val list: List<TransactionItem>) :
        RecyclerView.Adapter<TransactionAdapter.TxViewHolder>() {

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
            holder.tvAmount.text = "₹${tx.amount}"
            holder.tvStatus.text = "Completed"

            if (tx.type == "QR_REDEEM" || tx.type == "RECEIVED") {
                holder.tvAmount.setTextColor(0xFF22C55E.toInt())
                holder.tvAmount.text = "+ ₹${tx.amount}"
            } else {
                holder.tvAmount.setTextColor(0xFFEF4444.toInt())
                holder.tvAmount.text = "- ₹${tx.amount}"
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
