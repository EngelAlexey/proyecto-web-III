package com.example.clocker

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clocker.Util.MermaidUtils
import java.io.BufferedReader
import java.io.InputStreamReader

data class OpcionDiagrama(val titulo: String, val nombreArchivo: String)

class DocumentacionActivity : AppCompatActivity() {

    private var webView: WebView? = null
    private var spinner: Spinner? = null

    private val listaDiagramas = listOf(
        OpcionDiagrama("Seleccionar...", ""),
        OpcionDiagrama("Módulo de Reloj (Marcas)", "reloj_flow.txt"),
        OpcionDiagrama("Flujo de Login", "login_flow.txt"),
        OpcionDiagrama("Esquema Base de Datos", "db_schema.txt"),
        OpcionDiagrama("Proceso de Asistencia", "asistencia_flow.txt")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_documentacion)
        } catch (e: Exception) {
            Toast.makeText(this, "Error cargando Layout: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        webView = findViewById(R.id.webViewDoc)
        spinner = findViewById(R.id.spinnerDiagramas)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        if (webView == null || spinner == null) {
            Toast.makeText(this, "Error: Vistas no encontradas", Toast.LENGTH_LONG).show()
            return
        }

        setupWebView()
        setupSpinner()

        btnVolver?.setOnClickListener {
            finish()
        }
    }

    private fun setupWebView() {
        try {
            webView?.settings?.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupSpinner() {
        val titulos = listaDiagramas.map { it.titulo }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, titulos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val opcionSeleccionada = listaDiagramas[position]
                if (opcionSeleccionada.nombreArchivo.isNotEmpty()) {
                    cargarDiagrama("diagramas/${opcionSeleccionada.nombreArchivo}")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun cargarDiagrama(rutaAssets: String) {
        try {
            val list = assets.list("diagramas")
            val nombreArchivo = rutaAssets.substringAfterLast("/")

            if (list != null && list.contains(nombreArchivo)) {
                val inputStream = assets.open(rutaAssets)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val contenido = reader.use { it.readText() }
                renderizar(contenido)
            } else {
                Toast.makeText(this, "Archivo no encontrado en assets", Toast.LENGTH_SHORT).show()
                renderizar("graph TD; Error[Archivo No Encontrado] --> VerificarAssets;")
            }
        } catch (e: Exception) {
            renderizar("graph TD; Error[Excepcion] --> ${e.javaClass.simpleName};")
        }
    }

    private fun renderizar(codigoMermaid: String) {
        try {
            val html = MermaidUtils.getHtmlTemplate(codigoMermaid)
            webView?.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            Toast.makeText(this, "Error renderizando", Toast.LENGTH_SHORT).show()
        }
    }
}