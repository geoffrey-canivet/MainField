package com.example.mainfieldmanager.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.mainfieldmanager.R
import com.example.mainfieldmanager.model.User
import com.example.mainfieldmanager.model.UserDao
import kotlinx.coroutines.launch

class UserAdapter(
    private val context: Context,
    private val users: List<User>,
    private val lifecycleOwner: LifecycleOwner,
    private val userDao: UserDao
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // ViewHolder : Récupère les éléments de l'item
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.cardUserName)
        val userRole: TextView = itemView.findViewById(R.id.cardUserRole)
        val userBanque: TextView = itemView.findViewById(R.id.cardUserBanque)
        val btnSupprimer: ImageView = itemView.findViewById(R.id.icoSupprimer)
        val btnAddMoney: ImageView = itemView.findViewById(R.id.addMoney)
    }

    // Crée vue pour chaque card
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.usercard, parent, false)
        return UserViewHolder(view)
    }

    // Vue de la card
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        // Données user
        holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        holder.userName.text = user.username ?: "Nom inconnu"
        holder.userRole.text = user.role ?: "Rôle inconnu"
        holder.userBanque.text = user.banque?.toString() ?: "0"

        // Ajouter de l'argent
        holder.btnAddMoney.setOnClickListener {
            val userId = user.id
            lifecycleOwner.lifecycleScope.launch {
                val user = userDao.getUserById(userId)
                if (user != null) {
                    user.banque = user.banque + 100
                    userDao.updateUser(user)
                    holder.userBanque.text = user.banque.toString()
                } else {
                    Toast.makeText(context, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show()
                }

            }
        }

        // Supprimer user
        holder.btnSupprimer.setOnClickListener {
            val userId = user.id
            lifecycleOwner.lifecycleScope.launch {
                val userDel = userDao.getUserById(userId) // Récupérer l'utilisateur par ID
                if (userDel != null) {
                    userDao.deleteUser(userDel)
                    Toast.makeText(context, "Utilisateur supprimé : ID ${user.id}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Nombre total d'éléments dans la liste
    override fun getItemCount() = users.size
}
