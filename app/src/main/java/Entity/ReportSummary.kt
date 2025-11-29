package Entity

data class ReportSummary(
    val personId: String,
    val personName: String,
    val totalHours: String,
    val totalDays: Int,
    val totalLate: Int
)
