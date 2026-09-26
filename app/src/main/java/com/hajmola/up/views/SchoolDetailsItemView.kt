package com.hajmola.up.views

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.hajmola.up.R
import com.hajmola.up.utils.AppConstants

class SchoolDetailsItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val label: AppCompatTextView
    private val inputField: AppCompatEditText


    init {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.item_details_field, this, true)
        label = findViewById(R.id.itemTV)
        inputField = findViewById(R.id.itemET)

        attrs?.let {
            val styled = context.obtainStyledAttributes(it, R.styleable.LabeledEditText, 0, 0)
            label.text = styled.getString(R.styleable.LabeledEditText_labelText) ?: "Label"
            inputField.hint = styled.getString(R.styleable.LabeledEditText_hintText) ?: "Enter text"
            val inputType = styled.getString(R.styleable.LabeledEditText_inputType) ?: "Text"

            val type = when(inputType){
                AppConstants.INPUT_TYPE_NUMBER -> InputType.TYPE_CLASS_NUMBER
                else -> InputType.TYPE_CLASS_TEXT
            }

            inputField.inputType = type
            styled.recycle()
        }


    }

    fun setLabel(text: String) {
        label.text = text
    }

    fun getInput(): String {
        return inputField.text.toString()
    }

    fun setInput(text: String) {
        inputField.setText(text)
    }
}
