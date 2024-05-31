package com.leo.nopasswordforyou.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.helper.PassAdapter.MyViewHolder

class PassAdapter(
    private val listData: ArrayList<PassAdapterData>,
    private val uid: String,
    var db: FirebaseFirestore
) : RecyclerView.Adapter<MyViewHolder?>() {
    var clickListner: ItemClickListner? = null

    fun setClickListener(clickListener: ItemClickListner?) {
        this.clickListner = clickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = inflater.inflate(R.layout.custom_pass_list, parent, false)
        return MyViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //   final PassAdapterData myListData = listData.get(position);
        holder.passTitle.text = listData[position].title
        holder.passDesc.text = listData[position].description
    }


    override fun getItemCount(): Int {
        return listData.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var passTitle: TextView = itemView.findViewById(R.id.passTitle)
        var passDesc: TextView = itemView.findViewById(R.id.passDesc)
        var passParent: MaterialCardView = itemView.findViewById(R.id.passParent)


        init {
            itemView.setOnClickListener(this)
        }


        override fun onClick(v: View) {
            if (clickListner != null) {
                clickListner!!.onClick(
                    v,
                    listData[adapterPosition].id,
                    listData[adapterPosition].title,
                    listData[adapterPosition].description,
                    listData[adapterPosition].alias
                )
            }
        }
    }
}
