package com.hajmola.up.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hajmola.up.R
import com.hajmola.up.data.SchoolDataResponse
import com.hajmola.up.databinding.ItemSchoolDataBinding

class SchoolDataListAdapter(
    private val context: Context,
    private var list: List<SchoolDataResponse>,
    private val onClick: (SchoolDataResponse) -> Unit,
    private val onDelete: (Int) -> Unit
): RecyclerView.Adapter<SchoolDataListAdapter.SchoolDataListVH>() {

    inner class SchoolDataListVH(private val binding: ItemSchoolDataBinding): RecyclerView.ViewHolder(binding.root){

        fun onBind(item : SchoolDataResponse){
            binding.schoolName.text = item.name
            binding.citySchool.text =  context.getString(R.string.school_city_str,item.city)
            binding.addressSchool.text =  context.getString(R.string.school_address_str,item.address)
            binding.stateSchool.text =  context.getString(R.string.school_state_str,item.state)
            binding.uploadedBySchool.text = context.getString(R.string.school_uploaded_by_str,item.uploaded_by)
            binding.unitDistributedSchool.text =  context.getString(R.string.school_units_distributed, item.units_distributed)
            binding.eventDateSchool.text =  context.getString(R.string.school_date_str,item.activity_date)
            binding.viewDetails.setOnClickListener {
                onClick(item)
            }
            binding.deleteData.setOnClickListener {
                onDelete(item.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolDataListVH {
        val binding = ItemSchoolDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SchoolDataListVH(binding)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: SchoolDataListVH, position: Int) {
        holder.onBind(list[position])
    }

    fun updateList(schoolList: List<SchoolDataResponse>){
        list = schoolList
        notifyDataSetChanged()
    }
}