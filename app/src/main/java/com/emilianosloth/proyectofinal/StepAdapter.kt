package com.emilianosloth.proyectofinal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

class StepAdapter(private var steps: List<String>, private var stepImg: String) :
    RecyclerView.Adapter<StepAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var stepText : TextView
        var image : ImageView

        init {
            stepText = itemView.findViewById(R.id.stepTV)
            image = itemView.findViewById(R.id.stepIMG)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.steprow, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.stepText.text = steps[position]
        loadImg(holder.image, "https://firebasestorage.googleapis.com/v0/b/proyectofinalmoviles-e98e6.appspot.com/o/images%2Fmolletes.jpg?alt=media&token=171c0d48-f92d-446f-b50b-7a258411d7a8")
    }

    override fun getItemCount(): Int {
        return steps.size
    }

    fun loadImg(view: ImageView, url: String){
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        var image: Bitmap? = null


        // Only for Background process (can take time depending on the Internet speed)
        executor.execute {
            // Tries to get the image and post it in the ImageView
            // with the help of Handler
            try {
                val `in` = java.net.URL(url).openStream()
                image = BitmapFactory.decodeStream(`in`)
                // Only for making changes in UI
                handler.post {
                    view.setImageBitmap(image)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}