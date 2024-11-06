import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {
    private val firestore = FirebaseFirestore.getInstance()

    fun getClassroomRef(teacherId: String): CollectionReference {
        return firestore.collection("Teachers").document(teacherId).collection("Classrooms")
    }

    fun getStudentRef(teacherId: String, classroomId: String): CollectionReference {
        return getClassroomRef(teacherId).document(classroomId).collection("Students")
    }

    fun getAttendanceRef(teacherId: String, classroomId: String, studentId: String): CollectionReference {
        return getStudentRef(teacherId, classroomId).document(studentId).collection("Attendance")
    }
}
