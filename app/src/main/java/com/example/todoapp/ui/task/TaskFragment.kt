package com.example.todoapp.ui.task

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentTaskBinding
import com.example.todoapp.util.Constants
import com.example.todoapp.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TaskFragment : Fragment() {
    private val viewModel: TaskViewModel by viewModels()

    private val taskNameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {
            viewModel.taskName = p0.toString()
        }
    }

    private var binding: FragmentTaskBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {
            textInputEditTextTaskName.apply {
                setText(viewModel.taskName)
                addTextChangedListener(taskNameTextWatcher)
            }
            checkboxTaskImportant.apply {
                isChecked = viewModel.taskIsImportant
                setOnCheckedChangeListener { _, isChecked ->
                    viewModel.taskIsImportant = isChecked
                }
                jumpDrawablesToCurrentState() // skip animations
            }
            textViewTaskCreatedDate.apply {
                isVisible = viewModel.task != null
                text = getString(R.string.created_at, viewModel.task?.createdDateFormatted)
            }

            floatingActionButtonSaveTask.setOnClickListener {
                viewModel.onSaveTaskClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskEventFlow.collect { event ->
                when (event) {
                    is TaskViewModel.TaskEvent.NavigateBackWithResult -> {
                        binding!!.textInputEditTextTaskName.clearFocus()
                        setFragmentResult(
                            Constants.REQUEST_KEY_TASK_OPERATION_RESULT,
                            bundleOf(Constants.KEY_TASK_OPERATION_RESULT to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is TaskViewModel.TaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.textInputEditTextTaskName?.removeTextChangedListener(taskNameTextWatcher)
        binding = null
    }
}