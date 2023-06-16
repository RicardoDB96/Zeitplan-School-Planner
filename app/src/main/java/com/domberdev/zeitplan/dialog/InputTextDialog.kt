package com.domberdev.zeitplan.dialog

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.domberdev.zeitplan.R

class InputTextDialog(private val context: Context, private val onInputResult: OnInputResult) {

    interface OnInputResult{
        fun getInputValue(inputValue: String)
    }

    fun showDialog(stringResource: Int){
        // Set up the input
        val input = EditText(context)

        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(64, 0, 64, 0)
        input.layoutParams = lp
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.minHeight = 48
        input.setLines(1)
        input.maxLines = 1
        container.addView(input, lp)
        val builder = AlertDialog.Builder(context)
            .setTitle(stringResource)
            .setView(container)
            .setPositiveButton(R.string.set, null)
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            .create()

        builder.setOnShowListener {
            val positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val text = input.text.toString()
                when {
                    text.isEmpty() -> {
                        Toast.makeText(context, R.string.enterAGoal, Toast.LENGTH_SHORT).show()
                    }
                    text.toInt() > 100 -> {
                        Toast.makeText(context, R.string.theObjectiveLessOrEqual100, Toast.LENGTH_SHORT).show()
                    }
                    text.toInt() == 0 ->{
                        onInputResult.getInputValue("-")
                        builder.dismiss()
                    }
                    else -> {
                        onInputResult.getInputValue(input.text.toString())
                        builder.dismiss()
                    }
                }
            }
        }
        builder.show()
    }

    fun showDialogText(stringResource: Int){
        // Set up the input
        val input = EditText(context)

        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(64, 0, 64, 0)
        input.layoutParams = lp
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.minHeight = 48
        input.setLines(1)
        input.maxLines = 1
        container.addView(input, lp)
        val builder = AlertDialog.Builder(context)
            .setTitle(stringResource)
            .setView(container)
            .setPositiveButton(R.string.set, null)
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            .create()

        builder.setOnShowListener {
            val positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val text = input.text.toString()
                onInputResult.getInputValue(text)
                builder.dismiss()
            }
        }
        builder.show()
    }
}