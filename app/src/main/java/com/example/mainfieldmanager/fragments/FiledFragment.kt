package com.example.mainfieldmanager.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mainfieldmanager.R
import com.example.mainfieldmanager.view.adapter.FieldAdapter
import com.example.mainfieldmanager.databinding.FragmentFieldBinding
import com.example.mainfieldmanager.model.AppDatabase
import com.example.mainfieldmanager.model.User
import com.example.mainfieldmanager.model.Plot
import com.example.mainfieldmanager.viewModel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FieldFragment : Fragment() {
    private var _binding: FragmentFieldBinding? = null
    private val binding get() = _binding!!
    private var selectedCrop: String? = null // Stocke la culture sélectionnée
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFieldBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observer l'ID utilisateur dans le ViewModel
        userViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId != -1) {
                Log.d("FieldFragment", "ID utilisateur observé : $userId")
                // Charger les données utilisateur
                loadUserData(userId)

            } else {
                Toast.makeText(requireContext(), "Aucun ID utilisateur trouvé", Toast.LENGTH_SHORT).show()
            }
        }

        // Gestion des clics sur les boutons des cultures
        setupCropButtons()
    }


    private fun loadUserData(userId: Int) {
        val database = AppDatabase.getInstance(requireContext())

        lifecycleScope.launch {
            try {
                val user = database.userDao().getUserById(userId)
                if (user != null) {
                    binding.textViewBanque.text = "${user.banque} €"
                    binding.txtPrixPlot.text = "${50 * user.nbPlots} €"
                    setupRecyclerView(user)
                    addPlot(user)
                } else {
                    Toast.makeText(requireContext(), "Utilisateur introuvable", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("FieldFragment", "Erreur lors de la récupération des données utilisateur : ${e.message}")
                Toast.makeText(requireContext(), "Erreur lors du chargement des données", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView(user: User) {
        // Initialiser les plots
        var nbPlots = user.nbPlots
        val fields = MutableList(nbPlots) { index -> Plot(id = index, imageRes = R.drawable.plot_empty2) }
        val spanCount = 7 // Nombre de colonnes

        // Configurer l'adaptateur et le RecyclerView
        val adapter = FieldAdapter(
            fields = fields,
            user = user,
            spanCount = spanCount,
            getSelectedCrop = { selectedCrop },
            showToast = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            },
            updateBanqueTextView = { newBanque ->
                binding.textViewBanque.text = "$newBanque €" // Met à jour le TextView
            },
            scope = viewLifecycleOwner.lifecycleScope // Passe le scope lié au fragment
        )
        binding.recyclerField.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.recyclerField.adapter = adapter
    }

    private fun setupCropButtons() {
        binding.btnCarrot.setOnClickListener { showPopup("carrot") }
        binding.btnPotato.setOnClickListener { showPopup("potato") }
        binding.btnTomato.setOnClickListener { showPopup("tomato") }
        binding.btnTurnip.setOnClickListener { showPopup("turnip") }
    }

    private fun showPopup(vegetable: String) {
        // Associer le layout au légume sélectionné
        val layoutId = when (vegetable.lowercase()) {
            "carrot" -> R.layout.popup_carrot
            "potato" -> R.layout.popup_potato
            "tomato" -> R.layout.popup_tomato
            "turnip" -> R.layout.popup_turnip
            else -> null
        }

        if (layoutId == null) {
            Toast.makeText(requireContext(), "Aucun popup disponible pour $vegetable", Toast.LENGTH_SHORT).show()
            return
        }

        val popupView = LayoutInflater.from(requireContext()).inflate(layoutId, null)

        // Créer et afficher le popup
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(popupView)
            .setPositiveButton("OK") { dialog, _ ->
                selectedCrop = vegetable
                updateCropSelection(vegetable)
                dialog.dismiss()
            }
            .setNegativeButton("Annuler") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun updateCropSelection(vegetable: String) {
        // Mappage des images pour les boutons
        val images = mapOf(
            "carrot" to R.drawable.plot_carrot_ripe,
            "potato" to R.drawable.plot_potato_ripe,
            "tomato" to R.drawable.plot_tomato_ripe,
            "turnip" to R.drawable.plot_turnip_ripe
        )

        val buttons = listOf(
            binding.btnCarrot to R.drawable.btnmin_carrot,
            binding.btnPotato to R.drawable.btnmin_potato,
            binding.btnTomato to R.drawable.btnmin_tomato,
            binding.btnTurnip to R.drawable.btnmin_turnip
        )

        // Mettre à jour le bouton sélectionné
        images[vegetable]?.let { selectedImage ->
            buttons.forEach { (button, defaultImage) ->
                button.setImageResource(if (button == getButton(vegetable)) selectedImage else defaultImage)
            }
        }
    }

    private fun getButton(vegetable: String) = when (vegetable) {
        "carrot" -> binding.btnCarrot
        "potato" -> binding.btnPotato
        "tomato" -> binding.btnTomato
        "turnip" -> binding.btnTurnip
        else -> null
    }

    private fun addPlot(user: User) {
        binding.addPlot.setOnClickListener {
            var prixPlot = 50*user.nbPlots
            if (user.banque < prixPlot) {
                Toast.makeText(requireContext(), "Vous n'avez pas assez d'argent.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            user.banque -= prixPlot
            user.nbPlots++

            // Trophees
            when (user.nbPlots) {
                2 -> popupTrophee(user, 2)
                10 -> popupTrophee(user, 3)
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val database = AppDatabase.getInstance(binding.root.context)
                database.userDao().updateUser(user)

                val newPlot = Plot(id = user.nbPlots - 1, imageRes = R.drawable.plot_empty2)

                withContext(Dispatchers.Main) {
                    Toast.makeText(binding.root.context, "Banque mise à jour", Toast.LENGTH_SHORT).show()

                    (binding.recyclerField.adapter as? FieldAdapter)?.addPlot(newPlot)
                    binding.textViewBanque.text = "${user.banque} €"
                    val affichePrixPlot = 50 * user.nbPlots
                    binding.txtPrixPlot.text = "$affichePrixPlot €"
                }
            }
        }
    }

    private fun popupTrophee(user: User, trophyId: Int) {
        user.trophee.add(trophyId)
        AlertDialog.Builder(requireContext())
            .setTitle("Félicitations !")
            .setMessage("Vous avez débloqué un nouveau trophée.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
