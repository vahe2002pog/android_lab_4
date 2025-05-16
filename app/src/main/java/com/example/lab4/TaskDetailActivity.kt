package com.example.lab4

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var task: Task
    private var taskHasChanged: Boolean = false
    private var originalIsCompleted: Boolean = false

    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var dueDateTextView: TextView
    private lateinit var dueDateLabelTextView: TextView
    private lateinit var completeButton: Button
    private lateinit var editButton: Button
    private lateinit var backButton: ImageButton

    companion object {
        const val TASK_EXTRA_KEY = "TASK_EXTRA"
        const val RESULT_TASK_OBJECT_KEY = "RESULT_TASK_OBJECT_KEY"
        const val RESULT_TASK_ACTION_KEY = "RESULT_TASK_ACTION_KEY"

        const val ACTION_NO_CHANGE = 0
        const val ACTION_COMPLETED_TOGGLED = 1
        const val ACTION_UPDATED = 2
        const val ACTION_DELETED = 3
    }

    private var currentAction = ACTION_NO_CHANGE

    private val editTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val editedTask = data.getParcelableExtra<Task>(EditTaskActivity.RESULT_EDITED_TASK)
                val deletedTaskId = data.getStringExtra(EditTaskActivity.RESULT_DELETED_TASK_ID)

                if (editedTask != null) {
                    this.task = editedTask
                    populateTaskDetails()
                    currentAction = ACTION_UPDATED
                    taskHasChanged = true
                } else if (deletedTaskId != null) {
                    currentAction = ACTION_DELETED
                    taskHasChanged = true
                    prepareAndFinishActivity()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val initialTask = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TASK_EXTRA_KEY, Task::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TASK_EXTRA_KEY)
        } ?: run {
            Toast.makeText(this, "Ошибка: задача не найдена.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        this.task = initialTask
        this.originalIsCompleted = task.isCompleted

        titleTextView = findViewById(R.id.textViewDetailTitle)
        descriptionTextView = findViewById(R.id.textViewDetailDescription)
        dueDateTextView = findViewById(R.id.textViewDetailDueDate)
        dueDateLabelTextView = findViewById(R.id.labelDetailDueDate)
        completeButton = findViewById(R.id.button_toggle_complete_detail)
        editButton = findViewById(R.id.button_edit_task_detail)
        backButton = findViewById(R.id.button_back_task_detail)

        populateTaskDetails()

        backButton.setOnClickListener {
            prepareAndFinishActivity()
        }

        completeButton.setOnClickListener {
            val newCompletedState = !task.isCompleted
            task = task.copy(isCompleted = newCompletedState)
            taskHasChanged = true
            currentAction = ACTION_COMPLETED_TOGGLED

            populateTaskDetails()

            if (newCompletedState && !originalIsCompleted) {
                showCompletionDialog()
            }
        }
        editButton.setOnClickListener {
            val intent = Intent(this, EditTaskActivity::class.java)
            intent.putExtra(EditTaskActivity.EXTRA_TASK_TO_EDIT, task)
            editTaskLauncher.launch(intent)
        }
    }

    private fun populateTaskDetails() {
        titleTextView.text = task.title
        descriptionTextView.text = task.description

        if (task.dueDate != null) {
            dueDateLabelTextView.visibility = View.VISIBLE
            dueDateTextView.visibility = View.VISIBLE
            dueDateTextView.text = task.dueDate!!.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

            if (!task.isCompleted && task.dueDate!!.isBefore(LocalDate.now())) {
                dueDateTextView.setTextColor(Color.RED)
                dueDateTextView.paintFlags = dueDateTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            } else {
                dueDateTextView.setTextColor(titleTextView.currentTextColor)
                dueDateTextView.paintFlags = dueDateTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        } else {
            dueDateLabelTextView.visibility = View.GONE
            dueDateTextView.visibility = View.GONE
        }

        if (task.isCompleted) {
            completeButton.text = "Отметить как невыполненную"
            val completedColor = getColor(R.color.completed_task_text_color)
            titleTextView.applyStrikeThrough(true, completedColor)
            descriptionTextView.applyStrikeThrough(true, completedColor)
            if (task.dueDate != null) dueDateTextView.applyStrikeThrough(true, completedColor)
        } else {
            completeButton.text = "Отметить как выполненную"
            titleTextView.applyStrikeThrough(false, titleTextView.currentTextColor)
            descriptionTextView.applyStrikeThrough(false, descriptionTextView.currentTextColor)
            if (task.dueDate != null) {
                dueDateTextView.applyStrikeThrough(false, if(task.dueDate!!.isBefore(LocalDate.now())) Color.RED else titleTextView.currentTextColor)
            }
        }
    }

    private fun TextView.applyStrikeThrough(shouldStrike: Boolean, color: Int) {
        paintFlags = if (shouldStrike) {
            paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        setTextColor(color)
    }

    private fun showCompletionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Задача выполнена")
            .setMessage("Поздравляем. Задача выполнена!")
            .setPositiveButton("ОК") { dialog, _ ->
                dialog.dismiss()
                prepareAndFinishActivity()
            }
            .setCancelable(false)
            .show()
    }


    private fun prepareAndFinishActivity() {
        val resultIntent = Intent()
        if (taskHasChanged || currentAction != ACTION_NO_CHANGE) {
            resultIntent.putExtra(RESULT_TASK_OBJECT_KEY, task)
            resultIntent.putExtra(RESULT_TASK_ACTION_KEY, currentAction)
            setResult(Activity.RESULT_OK, resultIntent)
        } else {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        }
        finish()
    }

    override fun onBackPressed() {
        prepareAndFinishActivity()
    }
}