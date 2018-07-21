package lemoin.lemoincoinandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
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

        // Get the state if user is logged in.
        val isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)

        result = drawer {

            toolbar = this@AddressPage.toolbar_page
            translucentStatusBar = true
            hasStableIds = true
            savedInstance = savedInstanceState
            showOnFirstLaunch = true

            primaryItem("Home") {
                icon = R.drawable.ic_home
                onClick (openActivity(MainActivity::class, isLoggedIn))

            }
            // Divider places a line as visual dividing element.
            divider {  }

            primaryItem("Send coin") {
                icon = R.drawable.ic_list
                onClick (openActivityLoggedIn(SendCoin::class, isLoggedIn))
            }
            divider {  }
            primaryItem("Addresses") {
                icon = R.drawable.ic_list

                selectable = false
            }
            divider {  }
            primaryItem("Logout") {
                icon = R.drawable.ic_logout
                onClick(openActivityLogOut(MainActivity::class))
            }

        }




        val listView = findViewById<ListView>(R.id.main_listview)
        val redColor = Color.parseColor("#FF0000")
        listView.setBackgroundColor(redColor)

        // "this" is the current activity.
        listView.adapter = MyCustomAdapter(this)




    }

    // Function to open other screens when chosen in toolbar.
    private fun <T : Activity> openActivity(activity: KClass<T>, isLoggedIn: Boolean): (View?) -> Boolean = {
        val intent = Intent(this@AddressPage, activity.java)
        intent.putExtra("isLoggedIn", isLoggedIn)
        startActivity(intent)
        false
    }

    // Function to open other screens only when logged in.
    private fun <T : Activity> openActivityLoggedIn(activity: KClass<T>, isLoggedIn: Boolean): (View?) -> Boolean = {
        if(isLoggedIn) {
            val intent = Intent(this@AddressPage, activity.java)
            intent.putExtra("isLoggedIn", isLoggedIn)
            startActivity(intent)
        }
        false
    }

    private fun <T : Activity> openActivityLogOut(activity: KClass<T>): (View?) -> Boolean = {
        val intent = Intent(this@AddressPage, activity.java)
        intent.putExtra("isLoggedIn", false)
        startActivity(intent)
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
