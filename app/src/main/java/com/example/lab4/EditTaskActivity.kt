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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class EditTaskActivity : AppCompatActivity() {

    private lateinit var currentTask: Task

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var dueDateTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: ImageButton

    private var selectedDueDate: LocalDate? = null

    companion object {
        const val EXTRA_TASK_TO_EDIT = "EXTRA_TASK_TO_EDIT"
        const val RESULT_EDITED_TASK = "RESULT_EDITED_TASK"
        const val RESULT_DELETED_TASK_ID = "RESULT_DELETED_TASK_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        currentTask = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TASK_TO_EDIT, Task::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TASK_TO_EDIT)
        } ?: run {
            Toast.makeText(this, "Ошибка: Задача для редактирования не найдена.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        titleEditText = findViewById(R.id.editTextEditTaskTitle)
        descriptionEditText = findViewById(R.id.editTextEditTaskDescription)
        dueDateTextView = findViewById(R.id.textViewEditTaskDueDate)
        saveButton = findViewById(R.id.button_save_task)
        deleteButton = findViewById(R.id.button_delete_task_edit_form)
        backButton = findViewById(R.id.button_back_edit_task)

        populateFields()

        backButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        dueDateTextView.setOnClickListener {
            showDatePickerDialog()
        }

        saveButton.setOnClickListener {
            saveTask()
        }

        deleteButton.setOnClickListener {
            confirmAndDeleteTask()
        }
    }

    private fun populateFields() {
        titleEditText.setText(currentTask.title)
        descriptionEditText.setText(currentTask.description)
        selectedDueDate = currentTask.dueDate
        updateDueDateDisplay()
    }

    private fun updateDueDateDisplay() {
        if (selectedDueDate != null) {
            dueDateTextView.text = selectedDueDate!!.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        } else {
            dueDateTextView.text = "Дата не выбрана (обязательно)"
        }
    }

    private fun showDatePickerDialog() {
        val initialDate = selectedDueDate ?: LocalDate.now()
        val year = initialDate.year
        val month = initialDate.monthValue - 1
        val day = initialDate.dayOfMonth

        DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                selectedDueDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth)
                updateDueDateDisplay()
            }, year, month, day).show()
    }

    private fun saveTask() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Поле 'Название *' обязательно для заполнения", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedDueDate == null) {
            Toast.makeText(this, "Поле 'Срок выполнения *' обязательно для заполнения", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedTask = currentTask.copy(
            title = title,
            description = description,
        )

        val resultIntent = Intent()
        resultIntent.putExtra(RESULT_EDITED_TASK, updatedTask)
        setResult(Activity.RESULT_OK, resultIntent)
        Toast.makeText(this, "Задача обновлена", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun confirmAndDeleteTask() {
        AlertDialog.Builder(this)
            .setTitle("Удаление задачи")
            .setMessage("Вы уверены, что хотите удалить эту задачу окончательно?")
            .setPositiveButton("Удалить") { _, _ ->
                val resultIntent = Intent()
                resultIntent.putExtra(RESULT_DELETED_TASK_ID, currentTask.id)
                setResult(Activity.RESULT_OK, resultIntent)
                Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}