package com.example.lab4

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TaskAdapter(
    private var tasks: List<Task>,
    private val onClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.taskTitleItem)
        val description: TextView = view.findViewById(R.id.taskDescriptionItem)
        val dueDate: TextView = view.findViewById(R.id.taskDueDateItem)
        val itemLayout: LinearLayout = view.findViewById(R.id.task_item_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        val context = holder.itemView.context

        holder.title.text = if (task.title.length > 15) {
            task.title.substring(0, 15) + "..."
        } else {
            task.title
        }

        holder.description.text = if (task.description.length > 70) {
            task.description.substring(0, 70) + "..."
        } else {
            task.description
        }
        holder.description.visibility = if (task.description.isEmpty()) View.GONE else View.VISIBLE

        if (task.dueDate != null) {
            holder.dueDate.visibility = View.VISIBLE
            val formattedDate = task.dueDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
            holder.dueDate.text = "Срок: $formattedDate"

            if (!task.isCompleted && task.dueDate.isBefore(LocalDate.now())) {
                holder.dueDate.setTextColor(Color.RED)
            } else {
                holder.dueDate.setTextColor(ContextCompat.getColor(context, R.color.grey_text_color_item))
            }
        } else {
            holder.dueDate.visibility = View.GONE
            holder.dueDate.text = ""
        }

        if (task.isCompleted) {
            val completedColor = ContextCompat.getColor(context, R.color.completed_task_text_color)
            holder.title.setTextColor(completedColor)
            holder.description.setTextColor(completedColor)
            holder.dueDate.setTextColor(completedColor)

            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.description.paintFlags = holder.description.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.dueDate.paintFlags = holder.dueDate.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.title.setTextColor(ContextCompat.getColor(context, android.R.color.primary_text_light))
            holder.description.setTextColor(ContextCompat.getColor(context, android.R.color.secondary_text_light))

            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.description.paintFlags = holder.description.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.dueDate.paintFlags = holder.dueDate.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            if (task.dueDate != null && !task.dueDate.isBefore(LocalDate.now())) {
                holder.dueDate.setTextColor(ContextCompat.getColor(context, R.color.grey_text_color_item))
            }
        }


        holder.itemLayout.setOnClickListener {
            onClick(task)
        }
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}