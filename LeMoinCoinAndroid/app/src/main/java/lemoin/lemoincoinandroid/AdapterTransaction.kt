package lemoin.lemoincoinandroid
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.transaction_row.view.*
import java.text.NumberFormat

class AdapterTransaction(transactionData:List<SharedFun.Transaction>): RecyclerView.Adapter<CustomViewHolderTxn>() {

    val transactionData = transactionData

    // Number of items in the list.
    override fun getItemCount(): Int {
        return transactionData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolderTxn {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.transaction_row, parent, false)
        return CustomViewHolderTxn(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolderTxn, position: Int) {
        // Only give out the last part of the address for a better overview.
        val numberToFormat = NumberFormat.getNumberInstance()
        numberToFormat.minimumFractionDigits = 2
        val this_amount = numberToFormat.format(transactionData[position].amount.toDouble()/100)

        //holder.itemView.textView_contact_address.text = add
        if (transactionData[position].txn_to_this_add) {
            holder.itemView.textView_transaction_amount.text = this_amount.toString()
            holder.itemView.textView_transaction_amount.setTextColor(Color.GREEN)
            holder.itemView.textView_transaction_address.text = "...".plus(transactionData[position].txn_from.takeLast(15))
        } else {
            holder.itemView.textView_transaction_amount.text = "- ".plus(this_amount.toString())
            holder.itemView.textView_transaction_amount.setTextColor(Color.RED)
            holder.itemView.textView_transaction_address.text = "...".plus(transactionData[position].txn_to.takeLast(15))
        }
        holder.itemView.textView_transaction_date.text = transactionData[position].date
        holder.itemView.textView_transaction_date.setTextColor(Color.GRAY)
        // Get information of the clicked object for further use in the onClickListener.
    }
}

class CustomViewHolderTxn(view: View): RecyclerView.ViewHolder(view){
    init {
    }
}


