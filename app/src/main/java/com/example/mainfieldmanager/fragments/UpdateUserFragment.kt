package com.example.mainfieldmanager.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.mainfieldmanager.R
import com.example.mainfieldmanager.view.adapter.AvatarAdapter
import com.example.mainfieldmanager.databinding.FragmentUpdateUserBinding
import com.example.mainfieldmanager.model.AppDatabase
import com.example.mainfieldmanager.viewModel.UserViewModel
import kotlinx.coroutines.launch

class UpdateUserFragment : Fragment() {

    private var _binding: FragmentUpdateUserBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels() // activityViewModels -> cycle de vie de l'activity qui contient les fragments
    private var selectedAvatar: Int = R.drawable.user1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observer l'ID utilisateur
        userViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId != -1) {
                loadUserData(userId)
                setupUpdateButton(userId)
                Log.d("UpdateUserFragment", "ID utilisateur observé : $userId")
            } else {
                Toast.makeText(requireContext(), "Aucun ID utilisateur trouvé", Toast.LENGTH_SHORT).show()
            }
        }

        setupAvatarSpinner()
    }

    private fun loadUserData(userId: Int) {
        val database = AppDatabase.getInstance(requireContext())

        lifecycleScope.launch {
            val user = database.userDao().getUserById(userId)

            // Remplir les champs avec les données de l'utilisateur
            if (user != null) {
                binding.editUsername.setText(user.username)
                binding.editEmail.setText(user.email)
            } else {
                Toast.makeText(requireContext(), "Utilisateur introuvable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAvatarSpinner() {
        val avatarChoices = listOf("Avatar 1", "Avatar 2", "Avatar 3", "Avatar 4")
        val avatarAdapter = AvatarAdapter(requireContext(), avatarChoices)
        binding.spinnerAvat.adapter = avatarAdapter

        binding.spinnerAvat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Mettre à jour l'image selon la sélection
                selectedAvatar = when (position) {
                    0 -> R.drawable.user1
                    1 -> R.drawable.user2
                    2 -> R.drawable.user3
                    3 -> R.drawable.user4
                    else -> R.drawable.user1
                }
                binding.imgUser.setImageResource(selectedAvatar)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.imgUser.setImageResource(R.drawable.user1)
            }
        }
    }

    private fun setupUpdateButton(userId: Int) {
        binding.btnUpdate.setOnClickListener {
            val database = AppDatabase.getInstance(requireContext())
            val username = binding.editUsername.text.toString()
            val email = binding.editEmail.text.toString()

            if (username.isBlank() || email.isBlank()) {
                Toast.makeText(requireContext(), "Les champs ne peuvent pas être vides", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = database.userDao().getUserById(userId)

                if (user != null) {
                    user.username = username
                    user.email = email

                    database.userDao().updateUser(user)

                    Toast.makeText(requireContext(), "Utilisateur mis à jour", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Utilisateur introuvable", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
