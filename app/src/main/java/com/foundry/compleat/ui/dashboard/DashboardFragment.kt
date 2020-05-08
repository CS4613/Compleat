package com.foundry.compleat.ui.dashboard

import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.foundry.compleat.R
import com.squareup.square.Environment
import com.squareup.square.SquareClient
import com.squareup.square.exceptions.ApiException
import com.squareup.square.http.client.HttpContext
import com.squareup.square.models.Error
import com.squareup.square.models.Location
import java.io.IOException
import java.text.DecimalFormat


class DashboardFragment : Fragment() {
//    private val dashboardItems: ArrayList<dashboardItem> = arrayListOf(
//        dashboardItem(
//            title = "Daily Sales",
//            detail = "",
//            chartView = null,
//            intentClass = MainActivity::class.java
//        )
//    )

    private lateinit var dashboardViewModel: DashboardViewModel
    var client: SquareClient = SquareClient.Builder()
        .environment(Environment.PRODUCTION)
        .accessToken("ADD APP KEY HERE")
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        //val textView: TextView = root.findViewById(R.id.text_dashboard)
        //dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        //}


//        val pie = AnyChart.pie()
//
//        val data: MutableList<DataEntry> = ArrayList()
//        data.add(ValueDataEntry("John", 10000))
//        data.add(ValueDataEntry("Jake", 12000))
//        data.add(ValueDataEntry("Peter", 18000))
//
//        pie.data(data)
//
//        val anyChartView = root.findViewById<AnyChartView>(R.id.any_chart_view)
//
//        anyChartView.setChart(pie )

        val monthlyRevenue = getPaymentsTotal(
            "2020-05-01T00:00:00Z", "2020-05-31T00:00:00Z"
        ).toDouble() / 100
        val dailyRevenue = getPaymentsTotal(
            "2020-05-03T00:00:00Z", "2020-05-04T00:00:00Z"
        ).toDouble() / 100
        val dailyRevenueView = root.findViewById<TextView>(R.id.daily_revenue_number)
        val monthlyRevenueView = root.findViewById<TextView>(R.id.monthly_revenue_number)
        val dec = DecimalFormat("$#,###.00")
        monthlyRevenueView.setText(dec.format(monthlyRevenue))
        dailyRevenueView.setText(dec.format(dailyRevenue))

        return root
    }

    fun getLocations(){
        var api = client.locationsApi
        try {
            val locations: List<Location> = api.listLocations().locations
            for (location in locations)
            {
                println(location.address)
            }
            println("calling listLocations successfully")
        } catch (e: ApiException) {
            val errors: List<Error> = e.getErrors()
            val statusCode: Int = e.getResponseCode()
            val httpContext: HttpContext = e.getHttpContext()

            // Your error handling code
            System.err.println("ApiException when calling API")
            e.printStackTrace()
        } catch (e: IOException) {
            // Your error handling code
            System.err.println("IOException when calling API")
            e.printStackTrace()
        }
    }

    fun getPaymentsTotal(
        from: kotlin.String,
        to: kotlin.String
        ): Long {
        var total:Long = 0
        var api = client.paymentsApi
        var payments = api.listPayments(
            from.toString(),
            to.toString(),
            null,
            null,
            null,
            null,
            null,
            null
        ).payments
        for(payment in payments)
            total += payment.amountMoney.amount
        return total
    }

}
