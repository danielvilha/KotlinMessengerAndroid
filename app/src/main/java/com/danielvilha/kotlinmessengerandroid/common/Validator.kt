package com.danielvilha.kotlinmessengerandroid.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Created by danielvilha on 2019-06-25
 */
class Validator {

    fun EditText.validate(validator: (String) -> Boolean, message: String) {
        this.afterTextChanged {
            this.error = if (validator(it)) null else message
        }
        this.error = if (validator(this.text.toString())) null else message
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }
}