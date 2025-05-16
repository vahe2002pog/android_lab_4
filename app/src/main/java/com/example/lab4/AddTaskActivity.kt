package com.example.lab4

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class AddTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var addButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var dueDateTextView: TextView

    private var selectedDueDate: LocalDate = LocalDate.now()

    companion object {
        const val NEW_TASK_EXTRA_KEY = "NEW_TASK_RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        titleEditText = findViewById(R.id.editTextAddTaskTitle)
        descriptionEditText = findViewById(R.id.editTextAddTaskDescription)
        addButton = findViewById(R.id.button_add_task_form)
        backButton = findViewById(R.id.button_back_add_task)
        dueDateTextView = findViewById(R.id.textViewAddTaskDueDate)

        updateDueDateDisplay()

        backButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        dueDateTextView.setOnClickListener {
            showDatePickerDialog()
        }

        addButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Поле 'Название *' обязательно для заполнения", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newTask = Task(
                title = title,
                description = description,
                dueDate = selectedDueDate
            )

            val resultIntent = Intent()
            resultIntent.putExtra(NEW_TASK_EXTRA_KEY, newTask)
            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, "Задача добавлена!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateDueDateDisplay() {
        dueDateTextView.text = selectedDueDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    }

    private fun showDatePickerDialog() {
        val year = selectedDueDate.year
        val month = selectedDueDate.monthValue - 1
        val day = selectedDueDate.dayOfMonth

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                selectedDueDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth)
                updateDueDateDisplay()
            }, year, month, day)

        // datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // -1000 для учета текущего дня

        datePickerDialog.show()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}