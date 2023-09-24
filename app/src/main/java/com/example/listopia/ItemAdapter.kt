package com.example.listopia

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class ItemAdapter(private val items: ArrayList<Item>, private val adaptKey: String): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    private lateinit var context: Context
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemNameTV: TextView
        val itemPriceTV: TextView
        val itemURLTV: TextView

        init {
            itemNameTV = itemView.findViewById(R.id.itemNameTV)
            itemPriceTV = itemView.findViewById(R.id.itemPriceTV)
            itemURLTV = itemView.findViewById(R.id.itemURLTV)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list_item = items.get(position)
        holder.itemNameTV.text = list_item.itemName
        holder.itemPriceTV.text = "$" + String.format("%.2f", list_item.itemPrice.toFloat())
        holder.itemURLTV.text = list_item.itemURL

        holder.itemView.isLongClickable = true
        holder.itemView.setOnLongClickListener {
            items.removeAt(position)
            notifyItemRemoved(position)

            // Saves List Data
            val sharedPrefs = context.getSharedPreferences("shared preferences", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            val gson = Gson()
            val json = gson.toJson(items)
            editor.putString(adaptKey, json)
            editor.apply()

            Toast.makeText(context, holder.itemNameTV.text.toString() + " removed!", Toast.LENGTH_SHORT).show()
            true
        }

        holder.itemURLTV.setOnClickListener(View.OnClickListener {
            try {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(holder.itemURLTV.text.toString()))
                context.startActivity(browserIntent)
                Toast.makeText(context, "URL for " + holder.itemNameTV.text + " opened in browser!", Toast.LENGTH_SHORT).show()
            }
            catch (e: ActivityNotFoundException) {
                Toast.makeText(it.context, "Invalid URL for " + holder.itemNameTV.text, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun getItemCount(): Int {
        return items.size
    }
}