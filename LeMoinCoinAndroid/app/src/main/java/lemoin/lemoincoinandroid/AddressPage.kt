package lemoin.lemoincoinandroid

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_address_page.*
import kotlinx.android.synthetic.main.toolbar.*

class AddressPage : AppCompatActivity() {

    private lateinit var sharedFun: SharedFun

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_page)
        setSupportActionBar(toolbar_page)


        sharedFun = SharedFun(this, this@AddressPage, savedInstanceState)
        sharedFun.setDrawer()

        // Address page inspired by
        // https://www.youtube.com/watch?v=jS0buQyfJfs&list=PL0dzCUj1L5JGfHj1lwxOq67zAJV3e1S9S
        recyclerView_address_book.layoutManager = LinearLayoutManager(this)
        // Helps rendering items in list.
        recyclerView_address_book.adapter = AdapterAdressBook()








    }



}
