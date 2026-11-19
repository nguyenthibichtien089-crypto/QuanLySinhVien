package com.ute.studentmvvm.repository

import com.ute.studentmvvm.data.StudentDao
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.network.RetrofitClient
import com.ute.studentmvvm.network.StudentApiService
import com.ute.studentmvvm.network.toEntity
import kotlinx.coroutines.flow.Flow

class StudentRepository(
    private val studentDao: StudentDao,
    private val apiService: StudentApiService = RetrofitClient.apiService
) {
    // Single Source of Truth: UI chỉ quan sát dữ liệu lấy từ Room.
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()

    suspend fun insert(student: Student): Long {
        require(studentDao.getStudentByCode(student.studentCode) == null) {
            "Mã sinh viên ${student.studentCode} đã tồn tại!"
        }
        return studentDao.insertStudent(student)
    }

    suspend fun delete(student: Student) = studentDao.deleteStudent(student)

    suspend fun update(student: Student) {
        val other = studentDao.getStudentByCode(student.studentCode)
        require(other == null || other.id == student.id) {
            "Mã sinh viên ${student.studentCode} đã tồn tại!"
        }
        studentDao.updateStudent(student)
    }

    suspend fun syncFromCloud(): Result<Int> = try {
        val remoteList = apiService.getStudents()
        // Giữ ID cục bộ nếu đã đồng bộ trước đó, tránh nhân bản sau mỗi lần nhấn.
        val entities = remoteList.map { dto ->
            val student = dto.toEntity()
            val old = studentDao.getStudentByCode(student.studentCode)
            if (old != null) student.copy(id = old.id) else student
        }
        studentDao.insertAll(entities)
        Result.success(entities.size)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
