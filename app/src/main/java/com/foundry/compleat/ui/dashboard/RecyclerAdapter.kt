package com.foundry.compleat.ui.dashboard

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anychart.AnyChartView
import com.foundry.compleat.R
import kotlinx.android.synthetic.main.dashboard_number.view.*

data class dashboardItem(
    val title: String,
    val detail: String,
    val chartView: AnyChartView?,
    val layoutType: Int,
    val intentClass: Class<*>
)

class RecyclerAdapter(private val dashboardItems: ArrayList<dashboardItem>) :
    RecyclerView.Adapter<RecyclerAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var layout = R.layout.dashboard_number

        when(viewType) {
            0    -> layout = R.layout.dashboard_number
            else -> layout = R.layout.dashboard_number
        }
        val inflatedView = parent.inflate(layout, false)
        return ItemHolder(inflatedView)
    }

    override fun getItemCount(): Int = dashboardItems.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = dashboardItems[position]
        holder.bindItem(item)
    }

    class ItemHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var dashboardItem: dashboardItem? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val context = itemView.context
            val menuIntent = Intent(context, dashboardItem?.intentClass)
            context.startActivity(menuIntent)
            Log.d("RecyclerView", "CLICK!")
        }

        companion object {
            private val ITEM_KEY = "DASHBOARD"
        }

        fun bindItem(dashboardItem: dashboardItem) {
            this.dashboardItem = dashboardItem
            view.item_title.text = dashboardItem.title
            view.item_detail.text = dashboardItem.detail
        }
    }
}