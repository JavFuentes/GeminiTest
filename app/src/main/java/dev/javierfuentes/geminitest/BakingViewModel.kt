package dev.javierfuentes.geminitest

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Definimos la clase BakingViewModel que extiende de ViewModel
class BakingViewModel : ViewModel() {
    // Creamos una MutableStateFlow para manejar el estado de la UI
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    // Exponemos el MutableStateFlow como un StateFlow inmutable
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    // Inicializamos el modelo generativo con el nombre del modelo y la clave API
    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = BuildConfig.apiKey
    )

    // Función para enviar el prompt junto con la imagen al modelo generativo
    fun sendPrompt(
        bitmap: Bitmap,
        prompt: String
    ) {
        // Actualizamos el estado de la UI a Loading
        _uiState.value = UiState.Loading

        // Lanzamos una corrutina en el dispatcher IO para manejar la operación en segundo plano
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Enviamos la imagen y el texto al modelo generativo y obtenemos una respuesta
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                // Si la respuesta contiene texto, actualizamos el estado de la UI a Success
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                // En caso de excepción, actualizamos el estado de la UI a Error con el mensaje de error
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}
