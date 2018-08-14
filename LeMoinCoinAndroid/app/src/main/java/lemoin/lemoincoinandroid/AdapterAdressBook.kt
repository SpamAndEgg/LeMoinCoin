package lemoin.lemoincoinandroid

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.contact_row.view.*
import javax.xml.transform.Templates

class AdapterAdressBook: RecyclerView.Adapter<CustomViewHolder>() {

    // Dummy list of contact addresses.
    val contactName = listOf("Alice", "Bob", "Crowman", "Dilan", "Eric", "Fabsl", "Gustav")
    val contactAddress = listOf("x0a02w3fj9aw30", "x0b02w3fj9aw30", "x0c02w3fj9aw30",
            "x0d02w3fj9aw30", "x0e02w3fj9aw30", "x0f02w3fj9aw30", "x0g02w3fj9aw30")

    // Number of items in the list.
    override fun getItemCount(): Int {
        return contactName.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.contact_row, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        // Change properties of your rows on create here.
        holder.itemView.textView_contact_address.text = contactAddress[position]
        holder.itemView.textView_contact_name.text = contactName[position]
        // Get information of the clicked object for further use in the onClickListener.
        holder.this_address = contactAddress[position]
    }
}

class CustomViewHolder(view: View, var this_address: String = "not given"): RecyclerView.ViewHolder(view) {
    init {
        view.setOnClickListener {
            // Define what happens if click on a contact.
            println("This address is " + this_address)
        }
    }

}