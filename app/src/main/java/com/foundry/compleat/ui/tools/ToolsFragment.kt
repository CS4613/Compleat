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
import com.foundry.compleat.R

class ToolsFragment : Fragment() {
    private val items:ArrayList<Item> = arrayListOf(
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

    private lateinit var toolsViewModel: ToolsViewModel

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ItemHolder>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        layoutManager = LinearLayoutManager(context)
        adapter = RecyclerAdapter(items)
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