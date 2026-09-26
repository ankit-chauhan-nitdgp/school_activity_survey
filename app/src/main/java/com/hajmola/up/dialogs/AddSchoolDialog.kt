package com.hajmola.up.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.hajmola.up.databinding.DialogAddSchoolNameBinding

class AddSchoolDialog(
    private val onSchoolNameAdded: (String) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogAddSchoolNameBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        val inflater = requireActivity().layoutInflater

        binding = DialogAddSchoolNameBinding.inflate(inflater, null,false)
        dialog.setContentView(binding.root)

        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 16, -8,16,-8)
        dialog.window!!.setBackgroundDrawable(inset)

        binding.okButton.setOnClickListener {
            val input = binding.addSchoolET.text.toString().trim()
            if (input.isNotEmpty()){
                onSchoolNameAdded(input)
                dialog.dismiss()
            }else{
                Toast.makeText(requireContext(), " Enter School Name", Toast.LENGTH_LONG).show()
            }
        }
        binding.cancelButton.setOnClickListener { dialog.dismiss() }

        return dialog
    }

}