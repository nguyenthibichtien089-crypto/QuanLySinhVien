package com.ute.studentmvvm.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ute.studentmvvm.databinding.DialogAddStudentBinding
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.viewmodel.StudentViewModel

/** Optional enhancement: editing an existing Room record, beyond the PDF's required add/delete UI. */
class EditStudentDialogFragment : DialogFragment() {
    private val viewModel: StudentViewModel by activityViewModels {
        (requireActivity() as MainActivity).studentViewModelFactory
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val student = requireNotNull(studentToEdit)
        val binding = DialogAddStudentBinding.inflate(LayoutInflater.from(requireContext()))
        binding.etFullName.setText(student.fullName)
        binding.etStudentCode.setText(student.studentCode)
        binding.etEmail.setText(student.email)
        binding.etGpa.setText(student.gpa.toString())
        binding.etMajor.setText(student.major)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Sửa thông tin sinh viên")
            .setView(binding.root)
            .setPositiveButton("Lưu thay đổi", null)
            .setNegativeButton("Hủy", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = binding.etFullName.text?.toString()?.trim().orEmpty()
                val code = binding.etStudentCode.text?.toString()?.trim().orEmpty()
                val email = binding.etEmail.text?.toString()?.trim().orEmpty()
                val major = binding.etMajor.text?.toString()?.trim().orEmpty()
                val gpaText = binding.etGpa.text?.toString()?.trim().orEmpty()
                val gpa = gpaText.toDoubleOrNull()

                when {
                    name.isBlank() -> binding.etFullName.error = "Vui lòng nhập họ tên"
                    code.isBlank() -> binding.etStudentCode.error = "Vui lòng nhập MSSV"
                    gpa == null || gpa !in 0.0..4.0 ->
                        binding.etGpa.error = "GPA phải từ 0 đến 4"
                    else -> {
                        viewModel.update(
                            student.copy(
                                fullName = name,
                                studentCode = code,
                                email = email.ifBlank { "${code.lowercase()}@ute.udn.vn" },
                                gpa = gpa,
                                major = major.ifBlank { "Công Nghệ Thông Tin" }
                            )
                        )
                        dialog.dismiss()
                    }
                }
            }
        }
        return dialog
    }

    companion object {
        // Arguments ensure state survives configuration changes/recreation.
        private const val KEY_ID = "id"
        private const val KEY_FULL_NAME = "name"
        private const val KEY_CODE = "code"
        private const val KEY_EMAIL = "email"
        private const val KEY_GPA = "gpa"
        private const val KEY_MAJOR = "major"

        fun newInstance(student: Student): EditStudentDialogFragment =
            EditStudentDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_ID, student.id)
                    putString(KEY_FULL_NAME, student.fullName)
                    putString(KEY_CODE, student.studentCode)
                    putString(KEY_EMAIL, student.email)
                    putDouble(KEY_GPA, student.gpa)
                    putString(KEY_MAJOR, student.major)
                }
            }
    }

    private val studentToEdit: Student?
        get() = arguments?.let { args ->
            Student(
                id = args.getInt(KEY_ID),
                fullName = args.getString(KEY_FULL_NAME).orEmpty(),
                studentCode = args.getString(KEY_CODE).orEmpty(),
                email = args.getString(KEY_EMAIL).orEmpty(),
                gpa = args.getDouble(KEY_GPA),
                major = args.getString(KEY_MAJOR).orEmpty()
            )
        }
}
