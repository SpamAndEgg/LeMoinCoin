package lemoin.lemoincoinandroid


import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var sharedFun: SharedFun


    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_page)

        sharedFun = SharedFun(this, this@MainActivity, savedInstanceState)
        sharedFun.setDrawer()

        sharedFun.updateOwnerBalance(txt_your_balance)

        // Fetch the account balance on startup.
        //getAccBalance()
        // Define action for "Get Balance" button.
        btn_show_balance.setOnClickListener{
            //getAccBalance()
            sharedFun.updateOwnerBalance(txt_your_balance)
        }

    }


}
