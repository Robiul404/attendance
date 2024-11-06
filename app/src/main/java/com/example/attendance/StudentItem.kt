data class StudentItem(
    val roll: String,
    val name: String,
    var status: String
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "roll" to roll,
            "name" to name,
            "status" to status
        )
    }
}
