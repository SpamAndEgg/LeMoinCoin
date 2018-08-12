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


        sharedFun = SharedFun(this, this@AddressPage, savedInstanceState)
        sharedFun.setDrawer()








    }



}
