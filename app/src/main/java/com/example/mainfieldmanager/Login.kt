package com.example.mainfieldmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mainfieldmanager.databinding.ActivityLoginBinding
import com.example.mainfieldmanager.model.AppDatabase
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {


            if (binding.inputMail.text.toString().isNotEmpty() && binding.inputPass.text.toString().isNotEmpty()) {

                val email = binding.inputMail.text.toString()
                val password = binding.inputPass.text.toString()
                val database = AppDatabase.getInstance(this)

                lifecycleScope.launch {
                    val user = database.userDao().getUserByEmail(email)

                    if (user == null) {
                        // L'email n'existe pas dans la base de données
                        Toast.makeText(this@Login, "Aucun compte trouvé pour cet email", Toast.LENGTH_SHORT).show()
                    } else if (user.password == password) {
                        // Email trouvé et mot de passe correct
                        Toast.makeText(this@Login, "Connexion réussie", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Login, MainActivity::class.java)
                        intent.putExtra("USER_ID", user.id)
                        intent.putExtra("USER_ROLE", user.role)
                        startActivity(intent)
                    } else {
                        // Email trouvé mais mot de passe incorrect
                        Toast.makeText(this@Login, "Mot de passe incorrect", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Entrez votre email et mot de passe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        logDatabaseContent()
    }

    private fun logDatabaseContent() {
        val database = AppDatabase.getInstance(this)
        database.userDao().getAllUsers().observe(this) { users ->
            Log.d("LoginActivity", "Contenu de la table User :")
            users.forEach { user ->
                Log.d("Login", "ID: ${user.id}, username: ${user.username}, Banque: ${user.banque}")
            }
        }
    }

}
