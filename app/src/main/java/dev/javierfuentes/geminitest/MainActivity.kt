package dev.javierfuentes.geminitest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dev.javierfuentes.geminitest.ui.theme.GeminiTestTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Aplicamos el tema personalizado de la aplicación
            GeminiTestTheme {
                // Contenedor de superficie usando el color de fondo del tema
                Surface(
                    modifier = Modifier.fillMaxSize(), // La superficie ocupa todo el tamaño disponible
                    color = MaterialTheme.colorScheme.background, // Usamos el color de fondo del tema
                ) {
                    // Llamamos a la función composable BakingScreen para mostrar su contenido
                    BakingScreen()
                }
            }
        }
    }
}
