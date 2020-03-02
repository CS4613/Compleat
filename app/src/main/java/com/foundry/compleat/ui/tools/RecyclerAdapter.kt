package com.foundry.compleat.ui.tools

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foundry.compleat.R

data class Item(
    val title: String,
    val detail: String,
    val image: Int
)

class RecyclerAdapter(private val items: ArrayList<Item>) :
    RecyclerView.Adapter<RecyclerAdapter.ItemHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.tool_layout_listitem, false)
        return ItemHolder(inflatedView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val itemPhoto = photos[position]
        holder.bindPhoto(itemPhoto)
    }

    class ItemHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var item: Item? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val context = itemView.context
            val showPhotoIntent = Intent(context, ItemActivity::class.java)
            showPhotoIntent.putExtra(PHOTO_KEY, photo)
            context.startActivity(showPhotoIntent)
        }

        companion object {
            private val ITEM_KEY = "ITEM"
        }

        fun bindItem(item: Item) {
            this.item = item
            Picasso.with(view.context).load(photo.url).into(view.itemImage)
            view.itemDate.text = photo.humanDate
            view.itemDescription.text = photo.explanation
        }
    }

}

/*class RecyclerAdapter (private val items: ArrayList<Item>):
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private val items2 = arrayOf(
        Item("Order Picker",
            "Pull Current and Pending Orders",
            R.drawable.ic_launcher_foreground),
        Item("Inventory Builder",
            "Build and Push inventory items to ERP",
            R.drawable.ic_dashboard_black_24dp),
        Item("Label Printer",
            "Print Pricing and Inventory Labels to Print Server",
            R.drawable.ic_launcher_background)
    )

    private val titles = arrayOf(
        "Order Picker",
        "Inventory Builder", "Label Printer"
    )

    private val details = arrayOf(
        "Pull Current and Pending Orders",
        "Build and Push inventory items to ERP",
        "Print Pricing and Inventory Labels to Print Server"
    )

    private val images = intArrayOf(
        R.drawable.ic_launcher_foreground,
        R.drawable.ic_dashboard_black_24dp, R.drawable.ic_launcher_background
    )

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView

        interface onUserAction {
            fun onItemClick(item : Item)
        }

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)


            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition

                Snackbar.make(
                    v, "Click detected on item $position",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tool_layout_listitem, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemImage.contentDescription = titles[i]
        viewHolder.itemTitle.text = titles[i]
        viewHolder.itemDetail.text = details[i]
        viewHolder.itemImage.setImageResource(images[i])
    }

    override fun getItemCount(): Int {
        return titles.size
    }

}*/