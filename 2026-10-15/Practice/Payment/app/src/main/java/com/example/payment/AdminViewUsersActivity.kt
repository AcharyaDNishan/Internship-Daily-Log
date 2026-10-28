package com.example.payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class AdminViewUsersActivity : BaseSecureActivity() {

    private lateinit var adapter: UserAdapter
    private val displayList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view_users)

        val rvUsers = findViewById<RecyclerView>(R.id.rvUsers)
        val etSearchUser = findViewById<TextInputEditText>(R.id.etSearchUser)

        displayList.clear()
        displayList.addAll(SessionManager.usersList)

        adapter = UserAdapter(displayList) { selectedUser ->
            // Admin Action: Add Rs. 500 Credit to selected user
            lifecycleScope.launch {
                val newBal = selectedUser.creditBalance + 500.0
                val idx = SessionManager.usersList.indexOfFirst { it.uid == selectedUser.uid }
                if (idx != -1) {
                    SessionManager.usersList[idx] = selectedUser.copy(creditBalance = newBal)
                }

                try {
                    SupabaseClient.client.from("users").update({
                        set("credit_balance", newBal)
                    }) {
                        filter {
                            eq("uid", selectedUser.uid)
                        }
                    }
                } catch (e: Exception) {
                    // Fallback
                }

                fetchUsersFromSupabase()
                Toast.makeText(this@AdminViewUsersActivity, "Granted Rs. 500 Credit to ${selectedUser.firstName}!", Toast.LENGTH_SHORT).show()
            }
        }

        rvUsers?.layoutManager = LinearLayoutManager(this)
        rvUsers?.adapter = adapter

        etSearchUser?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim()?.lowercase().orEmpty()
                val filtered = if (query.isEmpty()) {
                    SessionManager.usersList
                } else {
                    SessionManager.usersList.filter {
                        it.firstName.lowercase().contains(query) ||
                        it.lastName.lowercase().contains(query) ||
                        it.phone.contains(query) ||
                        it.uid.contains(query)
                    }
                }
                adapter.updateUsers(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        fetchUsersFromSupabase()
    }

    private fun fetchUsersFromSupabase() {
        lifecycleScope.launch {
            try {
                val dbUsers = SupabaseClient.client.from("users").select().decodeList<User>()
                if (dbUsers.isNotEmpty()) {
                    SessionManager.usersList.clear()
                    SessionManager.usersList.addAll(dbUsers)
                }
            } catch (e: Exception) {
                // Fallback to local session
            }
            adapter.updateUsers(SessionManager.usersList)
        }
    }

    private class UserAdapter(
        private var users: List<User>,
        private val onUserClick: (User) -> Unit
    ) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        fun updateUsers(newUsers: List<User>) {
            this.users = newUsers
            notifyDataSetChanged()
        }

        class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvUserName: TextView = view.findViewById(R.id.tvUserName)
            val tvUserPhone: TextView = view.findViewById(R.id.tvUserPhone)
            val tvUserUid: TextView = view.findViewById(R.id.tvUserUid)
            val tvUserBalance: TextView = view.findViewById(R.id.tvUserBalance)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            return UserViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val u = users[position]
            holder.tvUserName.text = "${u.firstName} ${u.lastName}".trim().ifEmpty { "User" }
            holder.tvUserPhone.text = "Phone: ${u.phone}"
            holder.tvUserUid.text = "UID: ${u.uid}"
            holder.tvUserBalance.text = "Rs. ${u.creditBalance}"

            holder.itemView.setOnClickListener {
                onUserClick(u)
            }
        }

        override fun getItemCount(): Int = users.size
    }
}
