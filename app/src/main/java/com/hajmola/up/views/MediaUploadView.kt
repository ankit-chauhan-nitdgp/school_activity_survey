package com.hajmola.up.views

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.hajmola.up.R

class MediaUploadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val label: AppCompatTextView
    private val preview: AppCompatImageView
    private val uploadButton: AppCompatTextView


    init {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.item_media_upload, this, true)
        label = findViewById(R.id.mediaTV)
        preview = findViewById(R.id.mediaPreview)
        uploadButton = findViewById(R.id.uploadButton)

        attrs?.let {
            val styled = context.obtainStyledAttributes(it, R.styleable.MediaUploadAttributes, 0, 0)
            label.text = styled.getString(R.styleable.MediaUploadAttributes_mediaLabel) ?: "Label"
            styled.recycle()
        }


    }

    fun setLabel(text: String) {
        label.text = text
    }

    fun setOnUploadClickListener(listener: () -> Unit) {
        uploadButton.setOnClickListener { listener() }
    }

    fun setImagePreviewUri(uri: Uri){
        preview.setImageURI(uri)
    }

    fun setImageBitmap(bmp : Bitmap){
        preview.setImageBitmap(bmp)
    }

}