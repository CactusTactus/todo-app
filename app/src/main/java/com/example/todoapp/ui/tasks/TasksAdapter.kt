package com.example.todoapp.ui.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.Task
import com.example.todoapp.databinding.ListItemTaskBinding

class TasksAdapter(private val onItemClickListener: OnItemClickListener) : ListAdapter<Task, TasksAdapter.TaskViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTaskBinding.inflate(layoutInflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnItemClickListener{
        fun onItemClick(task: Task)
        fun onCheckboxClick(task: Task, isChecked: Boolean)
    }

    inner class TaskViewHolder(private val binding: ListItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            // Not bind listeners inside onBindViewHolder() (IT CALLS MANY TIMES) -> MEMORY LEAK
            // Instead, place listeners inside init {}, because that way it calls JUST ONE TIME -> NO MEMORY LEAK
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) { // allows to prevent crash when clicking on deleted item while it's still animates
                        val task = getItem(position)
                        onItemClickListener.onItemClick(task)
                    }
                }
                checkboxCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) { // allows to prevent crash when clicking on deleted item while it's still animates
                        val task = getItem(position)
                        onItemClickListener.onCheckboxClick(task, checkboxCompleted.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.isCompeted
                checkboxCompleted.isChecked = task.isCompeted
                imageViewImportantLabel.visibility =
                    if (task.isImportant) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}

