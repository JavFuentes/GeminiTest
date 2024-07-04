package dev.javierfuentes.geminitest

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// Definimos un array de recursos de imágenes
val images = arrayOf(
    R.drawable.baked_goods_1, // Imagen generada con Gemini usando el prompt "cupcake image"
    R.drawable.baked_goods_2, // Imagen generada con Gemini usando el prompt "cookies images"
    R.drawable.baked_goods_3, // Imagen generada con Gemini usando el prompt "cake images"
)

// Definimos un array de descripciones de imágenes
val imageDescriptions = arrayOf(
    R.string.image1_description,
    R.string.image2_description,
    R.string.image3_description,
)

@Composable
fun BakingScreen(
    bakingViewModel: BakingViewModel = viewModel() // Obtenemos una instancia de BakingViewModel
) {
    // Variable para almacenar la imagen seleccionada
    val selectedImage = remember { mutableIntStateOf(0) }
    // Variables para almacenar los placeholders de los textos de prompt y resultado
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val placeholderResult = stringResource(R.string.results_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    // Obtenemos el estado de la UI desde el ViewModel
    val uiState by bakingViewModel.uiState.collectAsState()
    // Obtenemos el contexto actual
    val context = LocalContext.current

    // Comenzamos a construir la columna principal de la pantalla
    Column(
        modifier = Modifier.fillMaxSize() // La columna ocupa todo el tamaño disponible
    ) {
        // Título de la pantalla
        Text(
            text = stringResource(R.string.baking_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        // Fila de imágenes desplazables horizontalmente
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Iteramos sobre las imágenes y sus índices
            itemsIndexed(images) { index, image ->
                var imageModifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .requiredSize(200.dp)
                    .clickable {
                        selectedImage.intValue = index // Actualizamos la imagen seleccionada
                    }
                if (index == selectedImage.intValue) {
                    // Si la imagen es la seleccionada, añadimos un borde
                    imageModifier =
                        imageModifier.border(BorderStroke(4.dp, MaterialTheme.colorScheme.primary))
                }
                // Mostramos la imagen
                Image(
                    painter = painterResource(image),
                    contentDescription = stringResource(imageDescriptions[index]),
                    modifier = imageModifier
                )
            }
        }

        // Fila con el campo de texto y el botón
        Row(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            // Campo de texto para el prompt
            TextField(
                value = prompt,
                label = { Text(stringResource(R.string.label_prompt)) },
                onValueChange = { prompt = it },
                modifier = Modifier
                    .weight(0.8f)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            )

            // Botón para enviar el prompt
            Button(
                onClick = {
                    val bitmap = BitmapFactory.decodeResource(
                        context.resources,
                        images[selectedImage.intValue]
                    )
                    // Llamamos al ViewModel para enviar el prompt con la imagen seleccionada
                    bakingViewModel.sendPrompt(bitmap, prompt)
                },
                enabled = prompt.isNotEmpty(), // El botón solo está habilitado si el prompt no está vacío
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = stringResource(R.string.action_go))
            }
        }

        // Si el estado de la UI es Loading, mostramos un indicador de carga
        if (uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            var textColor = MaterialTheme.colorScheme.onSurface
            // Si el estado de la UI es Error, mostramos el mensaje de error
            if (uiState is UiState.Error) {
                textColor = MaterialTheme.colorScheme.error
                result = (uiState as UiState.Error).errorMessage
            } else if (uiState is UiState.Success) {
                // Si el estado de la UI es Success, mostramos el resultado
                textColor = MaterialTheme.colorScheme.onSurface
                result = (uiState as UiState.Success).outputText
            }
            val scrollState = rememberScrollState()
            // Mostramos el texto del resultado
            Text(
                text = result,
                textAlign = TextAlign.Start,
                color = textColor,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            )
        }
    }
}
