package com.example.mainfieldmanager.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mainfieldmanager.view.adapter.UserAdapter
import com.example.mainfieldmanager.databinding.FragmentAdminBinding
import com.example.mainfieldmanager.model.AppDatabase
import com.example.mainfieldmanager.model.User
import com.example.mainfieldmanager.viewModel.UserViewModel

class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupération de l'ID utilisateur via le ViewModel
        userViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId != -1) {
                Log.d("AdminFragment", "ID utilisateur observé : $userId")
            } else {
                Toast.makeText(requireContext(), "Aucun ID utilisateur trouvé", Toast.LENGTH_SHORT).show()
            }
        }

        // Récupération de la base de données et des utilisateurs
        val database = AppDatabase.getInstance(requireContext())
        val userDao = database.userDao()

        // Observer les utilisateurs dans la base de données
        userDao.getAllUsers().observe(viewLifecycleOwner) { users ->
            afficheUser(users)
        }
    }

    private fun afficheUser(users: List<User>) {
        // Configuration du RecyclerView
        binding.recyclerUser.layoutManager = LinearLayoutManager(requireContext())
        val adapter = UserAdapter(
            requireContext(),
            users,
            lifecycleOwner = viewLifecycleOwner,
            userDao = AppDatabase.getInstance(requireContext()).userDao()
        )
        binding.recyclerUser.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
