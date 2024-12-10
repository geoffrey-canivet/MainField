package com.example.mainfieldmanager.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mainfieldmanager.R
import com.example.mainfieldmanager.databinding.FragmentTropheeBinding
import com.example.mainfieldmanager.databinding.FragmentUpdateUserBinding
import com.example.mainfieldmanager.model.AppDatabase
import com.example.mainfieldmanager.model.Trophee
import com.example.mainfieldmanager.view.adapter.TropheeAdapter
import com.example.mainfieldmanager.viewModel.UserViewModel
import kotlinx.coroutines.launch

class TropheeFragment : Fragment() {

    private var _binding: FragmentTropheeBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels() // activityViewModels -> cycle de vie de l'activity qui contient les fragments

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTropheeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observer l'ID utilisateur
        userViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId != -1) {
                loadUserData(userId)
                Log.d("Trophees", "ID utilisateur observé : $userId")
            } else {
                Toast.makeText(requireContext(), "Aucun ID utilisateur trouvé", Toast.LENGTH_SHORT).show()
            }
        }



    }



    private fun loadUserData(userId: Int) {
        val database = AppDatabase.getInstance(requireContext())

        lifecycleScope.launch {
            // Récupérer les données de l'utilisateur
            val user = database.userDao().getUserById(userId)

            // Récupérer les IDs de trophées associés à l'utilisateur
            val userTropheeIds = user?.trophee ?: emptyList()

            // Liste complète de trophées disponibles
            val allTrophees = listOf(
                Trophee(1, "Bienvenue!", "Première connexion, bienvenue.", "2023-01-01", "t1"),
                Trophee(2, "Fermette", "Vous avez acheté votre première parcelle", "2023-01-01", "t2"),
                Trophee(3, "Ranch", "Vous avez acheté 10 parcelles", "2023-01-01", "t3"),
                Trophee(4, "Prise de risque", "Vous avez dépensé 500 euros", "2023-01-01", "t4"),
                Trophee(5, "Prise de risque 2", "Vous avez dépensé 1000 euros", "2023-01-01", "t5"),
                Trophee(6, "Vendeur", "Vous avez gagné 500 euros", "2023-01-01", "t6"),
                Trophee(7, "Chef d'entreprise", "Vous avez gagné 1000 euros", "2023-01-01", "t7"),
                Trophee(8, "Spécialiste des carrottes", "Vous avez récolté 10 carrottres", "2023-01-01", "t8")
            )

            // Filtrer les trophées correspondant aux IDs de l'utilisateur
            val userTrophees = allTrophees.filter { it.id in userTropheeIds }

            // Afficher les trophées dans le RecyclerView
            afficheTrophee(userTrophees)
        }
    }

    // config recyclerView
    private fun afficheTrophee(trophees: List<Trophee>) {
        binding.recyclerTrophee.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TropheeAdapter(trophees)
        binding.recyclerTrophee.adapter = adapter
    }


}