package dev.javierfuentes.geminitest

/**
 * Una jerarquía sellada que describe el estado de la generación de texto.
 */
sealed interface UiState {

    /**
     * Estado vacío cuando la pantalla se muestra por primera vez
     */
    object Initial : UiState

    /**
     * Todavía cargando
     */
    object Loading : UiState

    /**
     * El texto ha sido generado
     */
    data class Success(val outputText: String) : UiState

    /**
     * Hubo un error al generar el texto
     */
    data class Error(val errorMessage: String) : UiState
}
