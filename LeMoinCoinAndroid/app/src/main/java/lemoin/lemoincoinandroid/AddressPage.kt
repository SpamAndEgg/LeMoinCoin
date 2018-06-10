package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.mikepenz.materialdrawer.Drawer
import kotlinx.android.synthetic.main.activity_address_page.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.reflect.KClass

class AddressPage : AppCompatActivity() {

    private lateinit var result: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_page)
        setSupportActionBar(toolbar_page)

        result = drawer {

            toolbar = this@AddressPage.toolbar_page
            translucentStatusBar = true
            hasStableIds = true
            savedInstance = savedInstanceState
            showOnFirstLaunch = true

            primaryItem("Home") {
                icon = R.drawable.ic_home
                onClick (openActivity(MainActivity::class))

            }
            // Divider places a line as visual dividing element.
            divider {  }

            primaryItem("Send coin") {
                icon = R.drawable.ic_list
                onClick (openActivity(SendCoin::class))
            }
            divider {  }
            primaryItem("Addresses") {
                icon = R.drawable.ic_list

                selectable = false
            }
            divider {  }
            primaryItem("Logout") {
                icon = R.drawable.ic_logout
            }

        }




        val listView = findViewById<ListView>(R.id.main_listview)
        val redColor = Color.parseColor("#FF0000")
        listView.setBackgroundColor(redColor)

        // "this" is the current activity.
        listView.adapter = MyCustomAdapter(this)




    }

    private fun <T : Activity> openActivity(activity: KClass<T>): (View?) -> Boolean = {
        startActivity(Intent(this@AddressPage, activity.java))
        false
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
