package com.ute.studentmvvm.network

import com.ute.studentmvvm.model.Student

// JSONPlaceholder trả dữ liệu 'users', không phải sinh viên thật.
// Chỉ dùng làm nguồn REST demo khi thầy/cô chưa cung cấp API sinh viên.
data class StudentDto(
    val id: Int,
    val name: String,
    val email: String
)

fun StudentDto.toEntity(): Student = Student(
    fullName = name,
    studentCode = "API" + id.toString().padStart(4, '0'),
    email = email,
    gpa = 0.0,
    major = "Đồng bộ từ REST demo"
)
