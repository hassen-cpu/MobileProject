package com.example.myapplication.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.*

class PlantAdapter(
    val context: MainActivity,
    private val plantList : List<PlantModel>,
    private val layoutId: Int) : RecyclerView.Adapter<PlantAdapter.ViewHolder>() {

    //boite pour ranger tout les cmpo à controler
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val plantImage = view.findViewById<ImageView>(R.id.image_item)

        val plantName:TextView? = view.findViewById(R.id.name_item)
        val plantDescription:TextView? = view.findViewById(R.id.description_item)
        val starIcon = view.findViewById<ImageView>(R.id.star_icon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutId,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //rec info plante
        val currentPlant = plantList[position]

        // recuperer repo
        val repo = PlantRepository()


        //use glide
        Glide.with(context).load(Uri.parse(currentPlant.imageUrl)).into(holder.plantImage)

        //update plant name
        holder.plantName?.text = currentPlant.name

        //update plant desc
        holder.plantDescription?.text = currentPlant.description

        //if plant liked
        if(currentPlant.liked){
            holder.starIcon.setImageResource(R.drawable.ic_star)
        }
        else{
            holder.starIcon.setImageResource(R.drawable.ic_unstar)
        }

        // rajouter interaction sur etoile
        holder.starIcon.setOnClickListener{
            //inverser
            currentPlant.liked = !currentPlant.liked

            // update object plant
            repo.updatePlant(currentPlant)

        }

        // interact click on plant
        holder.itemView.setOnClickListener{
            // afficher popup
            PlantPopup(this,currentPlant).show()
        }


    }

    override fun getItemCount(): Int = plantList.size
}