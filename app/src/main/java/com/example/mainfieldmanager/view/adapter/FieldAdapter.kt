package com.example.mainfieldmanager.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.mainfieldmanager.R
import com.example.mainfieldmanager.model.AppDatabase
import com.example.mainfieldmanager.model.Plot
import com.example.mainfieldmanager.model.User
import kotlinx.coroutines.*

class FieldAdapter(
    private val fields: MutableList<Plot>,
    private val user: User,
    private val spanCount: Int, // nb de colonne dans MainField
    private val getSelectedCrop: () -> String?, // recup valeur click de MainField
    private val showToast: (String) -> Unit, // Unit -> void
    private val updateBanqueTextView: (Long) -> Unit, // maj textView banque MainField
    private val scope: CoroutineScope // coroutines
) : RecyclerView.Adapter<FieldAdapter.FieldViewHolder>() {

    // ViewHolder pour la vue
    inner class FieldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plotImg: ImageView = itemView.findViewById(R.id.item_plot)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plot, parent, false)

        // Ajuste les dimensions pour rendre chaque item carré
        val layoutParams = view.layoutParams // Récupère les paramètres du parent
        val parentWidth = parent.measuredWidth // Largeur du parent
        val itemSize = parentWidth / spanCount // taille plot
        layoutParams.width = itemSize // largeur
        layoutParams.height = itemSize // heuteur
        view.layoutParams = layoutParams // Applique les modif

        return FieldViewHolder(view)
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) {
        val plot = fields[position] // plot en cours

        holder.plotImg.setImageResource(plot.imageRes)
        holder.progressBar.visibility = View.GONE

        // gestion du clic
        holder.itemView.setOnClickListener {
            val selectedCrop = getSelectedCrop() // click de MainField

            if (selectedCrop == null) {
                showToast("Veuillez sélectionner une culture avant de semer.")
                return@setOnClickListener // Interrompt le clic
            }

            if (plot.imageRes == R.drawable.plot_empty2) { // Si le plot est vide
                var tempsCroissance = 0L
                var cout = 0
                when (selectedCrop) {
                    "carrot" -> {
                        plot.imageRes = R.drawable.plot_carrot_seeded
                        tempsCroissance = 5000L
                        cout = 10
                    }
                    "potato" -> {
                        plot.imageRes = R.drawable.plot_potato_seeded
                        tempsCroissance = 6000L
                        cout = 50
                    }
                    "tomato" -> {
                        plot.imageRes = R.drawable.plot_tomato_seeded
                        tempsCroissance = 7000L
                        cout = 100
                    }
                    "turnip" -> {
                        plot.imageRes = R.drawable.plot_turnip_seeded
                        tempsCroissance = 8000L
                        cout = 200
                    }
                    else -> {
                        plot.imageRes = R.drawable.plot_empty2
                    }
                }

                // gestion banque
                if (user.banque < cout) {
                    showToast("Vous n'avez pas assez d'argent pour planter cette culture.")
                    return@setOnClickListener
                }
                user.banque -= cout

                // thread pour maj db
                scope.launch(Dispatchers.IO) {
                    val database = AppDatabase.getInstance(holder.itemView.context)
                    database.userDao().updateUser(user)
                }

                updateBanqueTextView(user.banque) // maj banque dans MainField
                plot.cropType = selectedCrop // stock la culture dans le plot
                holder.plotImg.setImageResource(plot.imageRes) // maj image du plot
                holder.progressBar.visibility = View.VISIBLE // Affiche la ProgressBar

                // coroutine pour simuler la croissance
                scope.launch(Dispatchers.Main) { // scope (type de cycle de vie) // Dispatcher.Main -> thread principal (ui)
                    delay(tempsCroissance) // temps de coissance
                    plot.imageRes = when (plot.cropType) { // modifie la valeur img quand terminé
                        "carrot" -> R.drawable.plot_carrot_ripe
                        "potato" -> R.drawable.plot_potato_ripe
                        "tomato" -> R.drawable.plot_tomato_ripe
                        "turnip" -> R.drawable.plot_turnip_ripe
                        else -> R.drawable.plot_empty2
                    }
                    holder.plotImg.setImageResource(plot.imageRes) // maj image
                    holder.progressBar.visibility = View.GONE // masque la ProgressBar
                }
            } else if (plot.imageRes != R.drawable.plot_empty2) { // si plot contien deja un légume
                val revenu = when (plot.cropType) { // récolte et calcul des gains
                    "carrot" -> 15
                    "potato" -> 80
                    "tomato" -> 150
                    "turnip" -> 300
                    else -> 0
                }
                user.banque += revenu // ajouter a la banque

                // GlobalScope? pour ne pas interrompre le thread ???
                // thread pour maj db et ui
                scope.launch(Dispatchers.IO) { // scope (type de cycle de vie) défini par Dispatcher ex.Io -> thread de stockage
                    val database = AppDatabase.getInstance(holder.itemView.context)
                    database.userDao().updateUser(user)
                }
                updateBanqueTextView(user.banque) // maj banque dans MainField

                showToast("Vous avez récolté des ${plot.cropType}s.")

                // réinitialise le plot
                plot.imageRes = R.drawable.plot_empty2
                plot.cropType = null
                holder.plotImg.setImageResource(R.drawable.plot_empty2)
            }
        }
    }

    fun addPlot(plot: Plot) {
        fields.add(plot) // Add the new plot to the list
        notifyItemInserted(fields.size - 1) // Notify RecyclerView about the new item
    }

    override fun getItemCount(): Int = fields.size // Retourne le nombre total de plots
}
