package com.example.lab4

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskAdapter(
    private val tasks: List<Task>,
    private val onClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.taskTitle)
        val description: TextView = view.findViewById(R.id.taskDescription)
        val dueDate: TextView = view.findViewById(R.id.taskDueDate)
        val cardView: CardView = view as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Обрезаем название и описание при необходимости
        holder.title.text = task.title.take(15).let {
            if (task.title.length > 15) "$it..." else it
        }

        holder.description.text = task.description.take(70).let {
            if (task.description.length > 70) "$it..." else it
        }

        // Дата выполнения задачи
        task.dueDate?.let {
            val formattedDate = it.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            holder.dueDate.text = "До: $formattedDate"

            if (it.isBefore(LocalDate.now())) {
                holder.dueDate.setTextColor(Color.RED)
            } else {
                holder.dueDate.setTextColor(Color.GRAY)
            }
        } ?: run {
            holder.dueDate.text = ""
        }

        // Обработка клика
        holder.cardView.setOnClickListener {
            onClick(task)
        }
    }
}