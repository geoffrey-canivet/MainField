package com.example.mainfieldmanager

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mainfieldmanager.databinding.ActivityMainBinding
import com.example.mainfieldmanager.model.AppDatabase
import com.example.mainfieldmanager.viewModel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels() // Initialisation de la ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation du View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuration de la barre d'outils
        setSupportActionBar(binding.appBarMain.toolbar)

        // Configuration du menu latéral
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Injecter info user dans nav_header_main.xml
        val navHeaderView = navView.getHeaderView(0)
        val database = AppDatabase.getInstance(this)
        val userId = intent.getIntExtra("USER_ID", -1) // -1 en cas d'absence
        val userRole = intent.getStringExtra("USER_ROLE")
        lifecycleScope.launch {
            val user = database.userDao().getUserById(userId)

            if (user != null) {

                navHeaderView.findViewById<TextView>(R.id.menuUserName).text = user.username
                navHeaderView.findViewById<TextView>(R.id.menuUserRole).text = user.role

            }
        }



        // Configuration de la barre d'application avec le menu latéral
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_field,
                R.id.nav_updateUser,
                R.id.nav_admin,
                R.id.nav_trophee
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (userId != -1) {
            // Passer l'ID utilisateur à la ViewModel
            userViewModel.userId.value = userId
            if (userRole == "Administrateur") {
                userViewModel.isAdmin.value = true
            } else {
                userViewModel.isAdmin.value = false
            }
            Toast.makeText(this, "Utilisateur connecté avec ID: $userId", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Aucun ID utilisateur trouvé", Toast.LENGTH_SHORT).show()
        }

        // Cacher ou afficher nav_admin selon le rôle
        userViewModel.isAdmin.observe(this) { isAdmin ->
            val menu = navView.menu

            // Modifier les titres des sections en blanc et gras
            for (i in 0 until menu.size()) {
                val menuItem = menu.getItem(i)
                if (menuItem.hasSubMenu()) { // Vérifie si l'item est une section (a un sous-menu)
                    val title = menuItem.title.toString()
                    val spannableTitle = SpannableString(title)

                    // Appliquer couleur blanche
                    spannableTitle.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)),
                        0,
                        title.length,
                        0
                    )

                    // Appliquer style gras
                    spannableTitle.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, 0)

                    // Modifier le titre
                    menuItem.title = spannableTitle
                }
            }

            val navAdminItem = menu.findItem(R.id.nav_admin)

            if (isAdmin == true) {
                // Restaurer l'apparence normale
                navAdminItem.isEnabled = true
                navAdminItem.icon?.alpha = 255 // Pleine opacité
            } else {
                // Réduire l'opacité et désactiver
                navAdminItem.isEnabled = false
                navAdminItem.icon?.alpha = 100 // Réduire l'opacité de l'icône
            }
        }

        // Déconnexion

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.deconnexion -> {
                    // Supprimer l'ID utilisateur de la ViewModel
                    userViewModel.userId.value = -1
                    userViewModel.isAdmin.value = false
                    Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show()

                    // Rediriger vers l'activité de connexion
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Fermer l'activité actuelle
                    true
                }
                else -> {
                    // Laisser la navigation gérer les autres items du menu
                    val navController = findNavController(R.id.nav_host_fragment_content_main)
                    navController.navigate(menuItem.itemId)

                    // Fermer le menu latéral
                    binding.drawerLayout.closeDrawers()
                    true
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
