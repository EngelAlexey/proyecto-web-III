package Entity

data class ReportRow(
    val personName: String,
    val date: String,
    val timeEntry: String?,
    val timeExit: String?,
    val hoursWorked: String?
)