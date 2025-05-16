package com.example.lab4

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView

    private val addTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newTask = result.data?.getParcelableExtra<Task>(AddTaskActivity.NEW_TASK_EXTRA_KEY)
            if (newTask != null) {
                taskList.add(0, newTask)
                sortTasksAndNotifyAdapter()
            }
        }
    }

    private val viewTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val returnedTask = result.data?.getParcelableExtra<Task>(TaskDetailActivity.RESULT_TASK_OBJECT_KEY)
            val action = result.data?.getIntExtra(TaskDetailActivity.RESULT_TASK_ACTION_KEY, TaskDetailActivity.ACTION_NO_CHANGE)

            if (returnedTask != null) {
                val taskIndex = taskList.indexOfFirst { it.id == returnedTask.id }

                when (action) {
                    TaskDetailActivity.ACTION_COMPLETED_TOGGLED, TaskDetailActivity.ACTION_UPDATED -> {
                        if (taskIndex != -1) {
                            taskList[taskIndex] = returnedTask
                        } else {
                            Toast.makeText(this, "Ошибка обновления задачи", Toast.LENGTH_SHORT).show()
                        }
                    }
                    TaskDetailActivity.ACTION_DELETED -> {
                        if (taskIndex != -1) {
                            taskList.removeAt(taskIndex)
                        } else {
                            Toast.makeText(this, "Ошибка удаления задачи", Toast.LENGTH_SHORT).show()
                        }
                    }
                    TaskDetailActivity.ACTION_NO_CHANGE -> {
                    }
                }
                sortTasksAndNotifyAdapter()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter(taskList) { task ->
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra(TaskDetailActivity.TASK_EXTRA_KEY, task)
            viewTaskLauncher.launch(intent)
        }
        recyclerView.adapter = taskAdapter

        sortTasksAndNotifyAdapter()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val customToolbarView = layoutInflater.inflate(R.layout.toolbar_custom, toolbar, false)
        toolbar.addView(customToolbarView)

        val btnAdd: ImageButton = customToolbarView.findViewById(R.id.button_add_toolbar)
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            addTaskLauncher.launch(intent)
        }
    }
    

    @SuppressLint("NotifyDataSetChanged")
    private fun sortTasksAndNotifyAdapter() {
        taskList.sortWith(
            compareBy<Task> { it.isCompleted }
                .thenComparing(compareBy<Task> {
                    if (it.isCompleted) null else it.dueDate == null
                }.thenByDescending {
                    if (it.isCompleted) null else it.dueDate?.isBefore(LocalDate.now()) ?: false
                }.thenBy {
                    if (it.isCompleted) null else it.dueDate
                }.thenBy {
                    if (it.isCompleted) null else it.createdAt
                })
                .thenComparing(compareBy<Task> {if (it.isCompleted) it.createdAt else null})

        )
        taskAdapter.updateTasks(taskList)
    }
}