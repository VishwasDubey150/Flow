package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flow.R
import model.Board

open class BoardItemsAdapter(private val context: Context, private var list: ArrayList<Board>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener:OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board,parent,false))
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){

            holder.itemView.findViewById<TextView>(R.id.tv_boardname).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_createdby).text = "Created by:"+model.createdBy

            holder.itemView.setOnClickListener {

                if(onClickListener != null){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int,model: Board)
    }

    fun setOnCLickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    private class MyViewHolder(view : View):RecyclerView.ViewHolder(view){
    }

}