package com.example.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminViewUsersActivity : BaseSecureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view_users)

        val rvUsers = findViewById<RecyclerView>(R.id.rvUsers)
        rvUsers?.layoutManager = LinearLayoutManager(this)
        rvUsers?.adapter = UserAdapter(SessionManager.usersList)
    }

    private class UserAdapter(private val users: List<User>) :
        RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

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
        }

        override fun getItemCount(): Int = users.size
    }
}
