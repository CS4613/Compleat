package com.foundry.compleat.ui.tools

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foundry.compleat.tools.InventoryBuilder
import com.foundry.compleat.MainActivity
import com.foundry.compleat.R

class ToolsFragment : Fragment() {
    private val menuItems: ArrayList<MenuItem> = arrayListOf(
        MenuItem(
            "Order Picker",
            "Pull Current and Pending Orders",
            R.drawable.ic_launcher_foreground,
            MainActivity::class.java
        ),
        MenuItem(
            "Inventory Builder",
            "Build and Push inventory items to ERP",
            R.drawable.ic_dashboard_black_24dp,
            InventoryBuilder::class.java
        ),
        MenuItem(
            "Label Printer",
            "Print Pricing and Inventory Labels to Print Server",
            R.drawable.ic_launcher_background,
            MainActivity::class.java
        )
    )

    private lateinit var toolsViewModel: ToolsViewModel

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ItemHolder>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        layoutManager = LinearLayoutManager(context)
        adapter = RecyclerAdapter(menuItems)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        toolsViewModel =
            ViewModelProvider(this).get(ToolsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tools, container, false)

        val recyclerView = root.findViewById<RecyclerView>(R.id.tool_recycler_view)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter

        return root
    }
}