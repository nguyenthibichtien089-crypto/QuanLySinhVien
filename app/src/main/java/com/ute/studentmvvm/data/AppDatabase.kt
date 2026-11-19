package com.ute.studentmvvm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ute.studentmvvm.model.Student

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ute_student_room.db"
                )
                    .addCallback(SeedDatabaseCallback())
                    .build()
                    .also { INSTANCE = it }
            }
    }

    /**
     * Seeds exactly once, within the very first SQLite creation event.
     * Using the provided SupportSQLiteDatabase avoids a race where INSTANCE
     * has not been assigned when Room opens the DB for the first time.
     */
    private class SeedDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val sql = """INSERT INTO students_table
                (full_name, student_code, email, gpa, major)
                VALUES (?, ?, ?, ?, ?)""".trimIndent()
            val samples = listOf(
                arrayOf<Any>("Nguyễn Văn An", "20110001", "an.nv@ute.udn.vn", 3.85, "Công Nghệ Thông Tin"),
                arrayOf<Any>("Trần Thị Mai", "20110002", "mai.tt@ute.udn.vn", 3.92, "Kỹ Thuật Phần Mềm"),
                arrayOf<Any>("Lê Hoàng Phúc", "20110003", "phuc.lh@ute.udn.vn", 3.45, "An Toàn Thông Tin"),
                arrayOf<Any>("Phạm Quốc Dũng", "20110004", "dung.pq@ute.udn.vn", 3.60, "Mạng Máy Tính")
            )
            samples.forEach { sample -> db.execSQL(sql, sample) }
        }
    }
}
