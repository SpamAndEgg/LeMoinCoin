package lemoin.lemoincoinandroid

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.toolbar.*

class AddressPage : AppCompatActivity() {

    private lateinit var sharedFun: SharedFun

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_page)
        setSupportActionBar(toolbar_page)

        // Get the state if user is logged in.
        val isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)
        sharedFun = SharedFun(this, this@AddressPage, savedInstanceState)

        sharedFun.setDrawer()



        val listView = findViewById<ListView>(R.id.main_listview)
        val redColor = Color.parseColor("#FF0000")
        listView.setBackgroundColor(redColor)

        // "this" is the current activity.
        listView.adapter = MyCustomAdapter(this)




    }


    private class MyCustomAdapter(context: Context): BaseAdapter(){

        private val mContext: Context

        init {
            mContext = context
        }

        override fun getCount(): Int {
            return 5
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getItem(p0: Int): Any {
            return "TestString"
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val textView = TextView(mContext)
            textView.text = "Here is something"
            return textView

        }
    }

}
