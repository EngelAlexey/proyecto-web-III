package com.example.clocker.Util

object MermaidUtils {
    fun getHtmlTemplate(graphDefinition: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <script src="https://cdnjs.cloudflare.com/ajax/libs/mermaid/10.6.1/mermaid.min.js"></script>
                <script>
                    mermaid.initialize({ startOnLoad: true, theme: 'default' });
                </script>
            </head>
            <body>
                <div class="mermaid" style="display: flex; justify-content: center;">
                    $graphDefinition
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}