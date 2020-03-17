package com.foundry.compleat.ui.tools

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.foundry.compleat.R
import kotlinx.android.synthetic.main.tool_layout_listitem.view.*

data class MenuItem(
    val title: String,
    val detail: String,
    val image: Int,
    val intentClass: Class<*>
)

class RecyclerAdapter(private val MenuItems: ArrayList<MenuItem>) :
    RecyclerView.Adapter<RecyclerAdapter.ItemHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.tool_layout_listitem, false)
        return ItemHolder(inflatedView)
    }

    override fun getItemCount(): Int = MenuItems.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = MenuItems[position]
        holder.bindItem(item)
    }

    class ItemHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var MenuItem: MenuItem? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val context = itemView.context
            val menuIntent = Intent(context, MenuItem?.intentClass)
            context.startActivity(menuIntent)
            Log.d("RecyclerView", "CLICK!")
        }

        companion object {
            private val ITEM_KEY = "ITEM"
        }

        fun bindItem(MenuItem: MenuItem) {
            this.MenuItem = MenuItem
            view.item_title.text = MenuItem.title
            view.item_detail.text = MenuItem.detail
            view.item_image.setImageResource(MenuItem.image)
        }
    }
}