package lemoin.lemoincoinandroid

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.content_add.*
import lemoin.lemoincoinandroid.R.id.txt_key
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class AddActivity : AppCompatActivity() ,ZBarScannerView.ResultHandler {

    //private var sDb: StoredDataBase? = null
    //private lateinit var sDbWorkerThread: DbWorkerThread

    private lateinit var mScannerView: ZBarScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add)
        mScannerView = ZBarScannerView(this)
        //mScannerView.stopCameraPreview()
        setContentView(mScannerView)
        //setSupportActionBar(toolbar)
        /*
        sDbWorkerThread = DbWorkerThread("dbWorkerThread")
        sDbWorkerThread.start()

        sDb = StoredDataBase.getInstance(this)

        button_add.setOnClickListener({insertStoredDataInDb()})
        val intent = Intent(this, QrCodeScanner::class.java)
        startActivity(intent)

        mScannerView = ZBarScannerView(this)
        mScannerView.stopCameraPreview()

        button_cam.setOnClickListener({
            setContentView(mScannerView)
        })
    }

    private fun insertStoredDataInDb() {

        var key = txt_key.text.toString();

        var newData = StoredData()

        if (key.length == 64){
            newData.privateKey = key;
        } else {
            if (key.length == 128) {
                newData.publicKey = key;
            } else {
                txt_key.setBackgroundColor(0xff0000)
                return
            }
        }

        newData.walletName = txt_name.text.toString();

        val task = Runnable { sDb?.storedDataDao()?.insert(newData) }
        sDbWorkerThread.postTask(task) */

    }

    /*
    * It is required to start and stop camera in lifecycle methods
    * (onResume and onPause)
    * */
    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this)
        mScannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }

    /*
    * Barcode scanning result is displayed here.
    * (For demo purposes only toast is shown here)
    * For understanding what more can be done with the result,
    * visit the GitHub README(https://github.com/dm77/barcodescanner)
    * */
    override fun handleResult(result: Result?) {
        //Toast.makeText(this, result?.contents, Toast.LENGTH_SHORT).show()

        //Camera will stop after scanning result, so we need to resume the
        //preview in order scan more codes
        //mScannerView.resumeCameraPreview(this)

        //this.txt_key
        //txt_key.hint = result?.contents
    }

}
