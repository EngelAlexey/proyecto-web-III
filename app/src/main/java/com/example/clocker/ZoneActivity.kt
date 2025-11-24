package com.example.clocker

import Controller.ZoneController
import Entity.Zone
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.Locale

class ZoneActivity : AppCompatActivity() {

    private lateinit var TextId: EditText
    private lateinit var TextCode: EditText
    private lateinit var TextName: EditText
    private lateinit var TextDescription: EditText
    private lateinit var TextDays: EditText
    private lateinit var TextStartTime: EditText
    private lateinit var TextEndTime: EditText
    private lateinit var SwitchStatus: Switch
    private lateinit var zoneController: ZoneController
    private var isEditMode: Boolean = false
    private lateinit var btnSearchZone: ImageButton

    private val selectedDays = mutableListOf<DayOfWeek>()

    private val daysOfWeekArray = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    private val dayOfWeekEnums = arrayOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    )
    private val checkedDays = BooleanArray(7)

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_zone)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        zoneController = ZoneController(this)
        TextId = findViewById(R.id.TextID)
        TextCode = findViewById(R.id.TextCode)
        TextName = findViewById(R.id.TextName)
        TextDescription = findViewById(R.id.TextDescription)
        TextDays = findViewById(R.id.TextDays)
        TextStartTime = findViewById(R.id.TextStartTime)
        TextEndTime = findViewById(R.id.TextEndTime)
        SwitchStatus = findViewById(R.id.swStatus)

        TextDays.setOnClickListener { showDaySelectionDialog() }

        setupTimePicker(TextStartTime)
        setupTimePicker(TextEndTime)

        val btnSearchZone: ImageButton = findViewById(R.id.btnSearchId_zone)
        btnSearchZone.setOnClickListener { searchZone() }
    }

    private fun setupTimePicker(editText: EditText) {
        editText.keyListener = null
        editText.isFocusable = false
        editText.isClickable = true

        editText.setOnClickListener {
            showTimePickerDialog(editText)
        }
    }

    private fun showTimePickerDialog(editText: EditText) {
        val currentTime = try {
            if (editText.text.isNotBlank()) {
                LocalTime.parse(editText.text.toString(), timeFormatter)
            } else {
                LocalTime.now()
            }
        } catch (e: Exception) {
            LocalTime.now()
        }

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->

                val selectedTime = LocalTime.of(hourOfDay, minute)
                editText.setText(selectedTime.format(timeFormatter))
            },
            currentTime.hour,
            currentTime.minute,
            true
        )
        timePickerDialog.show()
    }

    private fun showDaySelectionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione los días")
        builder.setMultiChoiceItems(daysOfWeekArray, checkedDays) { _, which, isChecked ->
            checkedDays[which] = isChecked
        }
        builder.setPositiveButton("OK") { _, _ ->
            updateSelectedDaysList()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun updateSelectedDaysList() {
        selectedDays.clear()
        val displayList = mutableListOf<String>()
        for (i in checkedDays.indices) {
            if (checkedDays[i]) {
                selectedDays.add(dayOfWeekEnums[i])
                displayList.add(daysOfWeekArray[i].substring(0, 3))
            }
        }
        TextDays.setText(displayList.joinToString(", "))
    }

    private fun loadDaysIntoUI(days: List<DayOfWeek>) {
        selectedDays.clear()
        selectedDays.addAll(days)

        for (i in checkedDays.indices) checkedDays[i] = false

        for (day in days) {
            val index = dayOfWeekEnums.indexOf(day)
            if (index != -1) checkedDays[index] = true
        }

        val displayList = selectedDays.map { day ->
            day.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
        }
        TextDays.setText(displayList.joinToString(", "))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnSave -> {
                saveZone()
                true
            }
            R.id.btnDelete -> {
                deleteZone()
                true
            }
            R.id.btnCancel -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun clear() {
        TextId.text.clear()
        TextCode.text.clear()
        TextName.text.clear()
        TextDescription.text.clear()
        TextDays.text.clear()
        TextStartTime.text.clear()
        TextEndTime.text.clear()
        SwitchStatus.isChecked = false
        isEditMode = false
        selectedDays.clear()
        for(i in checkedDays.indices) checkedDays[i] = false
    }

    fun isValidate(): Boolean =
        TextId.text.isNotBlank() &&
                TextCode.text.isNotBlank() &&
                TextName.text.isNotBlank() &&
                TextDescription.text.isNotBlank() &&
                selectedDays.isNotEmpty() &&
                TextStartTime.text.isNotBlank() &&
                TextEndTime.text.isNotBlank() &&
                SwitchStatus.isChecked

    fun searchZone() {
        try {
            val id = TextId.text.toString().trim()
            if (id.isBlank()) {
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
                return
            }

            val zone = zoneController.getByIdZone(id)

            if (zone == null) {
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
                clear()
            } else {
                TextCode.setText(zone.Code)
                TextName.setText(zone.Name)
                TextDescription.setText(zone.Description)
                loadDaysIntoUI(zone.Days)
                TextStartTime.setText(zone.StartTime.format(timeFormatter))
                TextEndTime.setText(zone.EndTime.format(timeFormatter))
                SwitchStatus.isChecked = zone.Status
                isEditMode = true
            }

        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: getString(R.string.ErrorMsgGetById), Toast.LENGTH_LONG).show()
        }
    }

    fun saveZone() {
        try {
            if (isValidate()) {
                val id = TextId.text.toString().trim()
                val existingZone = zoneController.getByIdZone(id)

                if (existingZone != null && !isEditMode) {
                    Toast.makeText(this, getString(R.string.MsgDuplicateDate), Toast.LENGTH_LONG).show()
                } else {
                    val startTime = LocalTime.parse(TextStartTime.text.toString(), timeFormatter)
                    val endTime = LocalTime.parse(TextEndTime.text.toString(), timeFormatter)

                    val zone = Zone().apply {
                        ID = id
                        Code = TextCode.text.toString()
                        Name = TextName.text.toString()
                        Description = TextDescription.text.toString()
                        StartTime = startTime
                        EndTime = endTime
                        Days = ArrayList(selectedDays)
                        Status = SwitchStatus.isChecked
                    }

                    if (isEditMode) {
                        zoneController.updateZone(zone)
                        Toast.makeText(this, getString(R.string.MsgUpdate), Toast.LENGTH_LONG).show()
                    } else {
                        zoneController.addZone(zone)
                        Toast.makeText(this, getString(R.string.MsgSave), Toast.LENGTH_LONG).show()
                    }

                    clear()
                }
            } else {
                Toast.makeText(this, "Debe llenar todos los campos y seleccionar al menos un día", Toast.LENGTH_LONG).show()
            }
        } catch (e: DateTimeParseException) {
            Toast.makeText(this, "Formato de hora inválido", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun deleteZone(){
        try {
            val zone = zoneController.getByIdZone(TextId.text.toString().trim())
            val id = TextId.text.toString().trim()
            if (id.isBlank()){
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
            } else if (zone == null){
                Toast.makeText(this, R.string.ErrorMsgRemove, Toast.LENGTH_LONG).show()
            } else {
                zoneController.removeZone(id)
            }
            Toast.makeText(this, R.string.MsgDelete, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
        clear()
    }
}