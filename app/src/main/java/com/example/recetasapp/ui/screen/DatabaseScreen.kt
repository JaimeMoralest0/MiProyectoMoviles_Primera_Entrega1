import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.recetasapp.data.Recipe
import com.example.recetasapp.ui.viewmodel.DatabaseViewModel

@Composable
fun DatabaseScreen(
    navController: NavController,
    viewModel: DatabaseViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Pair<String, Recipe>?>(null) }

    val recipes by viewModel.recipes.collectAsState() // Escucha cambios en las recetas

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE1BEE7)) // Morado clarito
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gestión de Recetas",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Image(
                    painter = painterResource(id = com.example.recetasapp.R.drawable.puesto),
                    contentDescription = "Icono de base de datos",
                    modifier = Modifier.size(50.dp) // Tamaño ajustable
                        .clickable { navController.popBackStack()}
                )
            }
        }

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Receta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(recipes) { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(recipe.title, style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Ingredientes: ${recipe.ingredients.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { showEditDialog = recipe.id to recipe },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Modificar")
                            }
                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.deleteRecipe(recipe.id)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para agregar receta
    if (showAddDialog) {
        RecipeDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, ingredients ->
                scope.launch {
                    viewModel.addRecipe(title, ingredients.split(",").map { it.trim() }) // Convierte en lista
                    showAddDialog = false
                }
            }
        )
    }

    // Diálogo para editar receta
    showEditDialog?.let { (recipeId, recipe) ->
        RecipeDialog(
            title = recipe.title,
            ingredients = recipe.ingredients.joinToString(", "),
            onDismiss = { showEditDialog = null },
            onConfirm = { newTitle, newIngredients ->
                scope.launch {
                    viewModel.updateRecipe(recipeId, newTitle, newIngredients.split(",").map { it.trim() }) // Convierte en lista
                    showEditDialog = null
                }
            }
        )
    }
}

@Composable
fun RecipeDialog(
    title: String = "",
    ingredients: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var recipeTitle by remember { mutableStateOf(title) }
    var recipeIngredients by remember { mutableStateOf(ingredients) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Receta") },
        text = {
            Column {
                OutlinedTextField(
                    value = recipeTitle,
                    onValueChange = { recipeTitle = it },
                    label = { Text("Título de la Receta") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = recipeIngredients,
                    onValueChange = { recipeIngredients = it },
                    label = { Text("Ingredientes (separados por comas)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(recipeTitle, recipeIngredients) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
