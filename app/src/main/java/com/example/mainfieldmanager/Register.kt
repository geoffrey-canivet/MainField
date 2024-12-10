package com.example.mainfieldmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mainfieldmanager.view.adapter.AvatarAdapter
import com.example.mainfieldmanager.view.adapter.RoleAdapter
import com.example.mainfieldmanager.databinding.ActivityRegisterBinding
import com.example.mainfieldmanager.model.AppDatabase
import com.example.mainfieldmanager.model.User
import com.example.mainfieldmanager.model.UserDao
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userDao: UserDao

    // Variable pour stocker le rôle sélectionné
    private var selectedRole: String = "Joueur"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialiser la base de données et le DAO
        val database = AppDatabase.getInstance(this)
        userDao = database.userDao()

        // Configurer les Spinners
        setupAvatarSpinner()
        setupRoleSpinner()

        // Configurer les actions des boutons
        addUser()
    }

    private fun setupAvatarSpinner() {
        val avatarChoices = listOf("Avatar 1", "Avatar 2", "Avatar 3", "Avatar 4")

        val avatarAdapter = AvatarAdapter(this, avatarChoices)
        binding.spinnerAvat.adapter = avatarAdapter

        binding.spinnerAvat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Mettre à jour l'image selon la sélection
                val avatarResId = when (position) {
                    0 -> R.drawable.user1
                    1 -> R.drawable.user2
                    2 -> R.drawable.user3
                    3 -> R.drawable.user4
                    else -> R.drawable.user1
                }
                binding.imgUser.setImageResource(avatarResId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.imgUser.setImageResource(R.drawable.user1)
            }
        }
    }

    private fun setupRoleSpinner() {
        val roleChoices = listOf("Joueur", "Administrateur")

        val roleAdapter = RoleAdapter(this, roleChoices)
        binding.spinnerRole.adapter = roleAdapter

        binding.spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Mettre à jour la variable selon la sélection
                selectedRole = roleChoices[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRole = "Joueur" // Valeur par défaut
            }
        }
    }

    private fun addUser() {
        binding.btnRegister.setOnClickListener {
            // TODO rajouter verif
            val randomId = (1..100).random()
            lifecycleScope.launch {
                val newUser = User(
                    id = randomId,
                    username = binding.editUsername.text.toString(),
                    email = binding.editEmail.text.toString(),
                    banque = 40,
                    password = binding.passwordUser1.text.toString(),
                    role = selectedRole,
                    avatar = binding.spinnerAvat.selectedItem.toString(),
                    nbPlots = 1,
                    trophee = mutableListOf(1)
                )
                userDao.insertUser(newUser)
                Toast.makeText(this@Register, "Inscription réussie vous avez l'id ${newUser.id}", Toast.LENGTH_LONG).show()
                val intent = Intent(this@Register, Login::class.java)
                startActivity(intent)
            }
        }
    }
}
