package com.example.lab4

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter(taskList) { task ->
            // Обработка нажатия — откроем карточку задачи
            //val intent = Intent(this, TaskDetailActivity::class.java)
            //intent.putExtra("task_title", task.title)
            //intent.putExtra("task_description", task.description)
            //intent.putExtra("task_due_date", task.dueDate?.toString())
            //startActivity(intent)
        }
        recyclerView.adapter = taskAdapter
        taskList.add(Task("Купить молоко", "Сходить в магазин и купить молоко", LocalDate.now()))
        taskList.add(Task("Купить молоко", "Сходить в магазин и купить молоко", null))

        taskAdapter.notifyDataSetChanged()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val customView = layoutInflater.inflate(R.layout.toolbar_custom, toolbar, false)
        toolbar.addView(customView)

        val btnAdd: ImageButton = customView.findViewById(R.id.button_add)
        btnAdd.setOnClickListener {
            // переход к экрану "Форма добавления дела"
        }
    }
}

