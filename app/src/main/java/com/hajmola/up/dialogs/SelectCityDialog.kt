package com.hajmola.up.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import com.hajmola.up.databinding.DialogSelectCityBinding
import com.hajmola.up.utils.AppConstants

class SelectCityDialog(
    private val cityList: List<String>,
    private val onCitySelect:(String) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogSelectCityBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        val inflater = requireActivity().layoutInflater
        var selectedCity = "None"

        binding = DialogSelectCityBinding.inflate(inflater, null,false)
        dialog.setContentView(binding.root)

        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 16, -8,16,-8)
        dialog.window!!.setBackgroundDrawable(inset)

        binding.okButton.setOnClickListener {
//            val bundle = bundleOf(
//                AppConstants.CITY_NAME to selectedCity
//            )
//            findNavController().navigateWithSlideAnim(R.id.action_mainMenu_to_AddSchool, args = bundle)
            onCitySelect(selectedCity)
            dialog.dismiss()
        }

        val spinner: AppCompatSpinner = binding.selectCityTV
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cityList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                 selectedCity = parent.getItemAtPosition(position) as String
              //  Toast.makeText(this@MainActivity, "Selected: $selected", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.okButton.isEnabled = false
            }
        }

        binding.cancelButton.setOnClickListener { dialog.dismiss() }

        return dialog
    }

}