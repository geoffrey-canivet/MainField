package com.example.mainfieldmanager.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// transit de l'id
class UserViewModel : ViewModel() {

    val userId = MutableLiveData<Int>() // liveData donnée en temps reel par raport au cycle de vie
    val isAdmin = MutableLiveData<Boolean>()

}