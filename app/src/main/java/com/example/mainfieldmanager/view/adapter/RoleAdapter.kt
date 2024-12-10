package com.example.mainfieldmanager.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.mainfieldmanager.R

class RoleAdapter(
    context: Context,
    roleChoices: List<String>
) : ArrayAdapter<String>(
    context,
    R.layout.spinner_item, // Layout personnalisé pour l'élément sélectionné
    roleChoices
) {
    init {
        setDropDownViewResource(R.layout.spinner_item) // Layout pour les options déroulantes
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Personnaliser l'apparence de l'élément sélectionné
        val view = super.getView(position, convertView, parent)
        (view as TextView).apply {
            setTextColor(ContextCompat.getColor(context, R.color.white)) // Texte blanc
            textSize = 18f
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Personnaliser l'apparence des options déroulantes
        val view = super.getDropDownView(position, convertView, parent)
        (view as TextView).apply {
            setTextColor(ContextCompat.getColor(context, R.color.white)) // Texte blanc
            setBackgroundColor(ContextCompat.getColor(context, R.color.couleurPerso)) // Fond vert
            textSize = 18f
        }
        return view
    }
}
