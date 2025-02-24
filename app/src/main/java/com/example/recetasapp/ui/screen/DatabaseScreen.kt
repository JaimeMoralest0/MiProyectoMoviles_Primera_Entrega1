package com.example.recetasapp.ui.screen

import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun DatabaseScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

    var selectedRecipeId by remember { mutableStateOf<String?>(null) }
    var selectedRecipeTitle by remember { mutableStateOf("") }
    var selectedRecipeIngredients by remember { mutableStateOf("") }
    var showAddForm by remember { mutableStateOf(false) }
    var showUpdateForm by remember { mutableStateOf(false) }
    var showDeleteList by remember { mutableStateOf(false) }
    var recipeList by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    // Cargar recetas desde Firestore cuando se inicia la pantalla
    LaunchedEffect(Unit) {
        loadRecipes(db) { recipes -> recipeList = recipes }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Gestión de Recetas", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showAddForm = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Añadir Receta", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showUpdateForm = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text("Modificar Receta", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showDeleteList = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Eliminar Receta", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Volver", color = Color.White)
        }
    }

    // FORMULARIO PARA AÑADIR RECETA
    if (showAddForm) {
        RecipeForm(
            title = selectedRecipeTitle,
            ingredients = selectedRecipeIngredients,
            onDismiss = { showAddForm = false },
            onConfirm = { title, ingredients ->
                scope.launch {
                    addRecipeToDatabase(title, ingredients, context, db)
                    loadRecipes(db) { recipes -> recipeList = recipes }
                    showAddForm = false
                }
            }
        )
    }

    // LISTADO DE RECETAS PARA MODIFICAR
    if (showUpdateForm) {
        AlertDialog(
            onDismissRequest = { 
                // Limpiar el estado al cerrar
                showUpdateForm = false
                selectedRecipeId = null
                selectedRecipeTitle = ""
                selectedRecipeIngredients = ""
            },
            title = { Text("Modificar Receta") },
            text = {
                Column {
                    // Lista de recetas para seleccionar
                    LazyColumn {
                        items(recipeList) { (id, title) ->
                            Text(
                                text = title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedRecipeId = id
                                        selectedRecipeTitle = title
                                        // Cargar los ingredientes de la receta seleccionada
                                        scope.launch {
                                            loadRecipeIngredients(db, id) { ingredients ->
                                                selectedRecipeIngredients = ingredients
                                            }
                                        }
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }

                    if (selectedRecipeId != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = selectedRecipeTitle,
                            onValueChange = { selectedRecipeTitle = it },
                            label = { Text("Título") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = selectedRecipeIngredients,
                            onValueChange = { selectedRecipeIngredients = it },
                            label = { Text("Ingredientes") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedRecipeId != null) {
                            scope.launch {
                                updateRecipe(
                                    db,
                                    selectedRecipeId!!,
                                    selectedRecipeTitle,
                                    selectedRecipeIngredients,
                                    context
                                ) {
                                    showUpdateForm = false
                                    selectedRecipeId = null
                                    loadRecipes(db) { recipes -> recipeList = recipes }
                                }
                            }
                        }
                    },
                    enabled = selectedRecipeId != null
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showUpdateForm = false
                        selectedRecipeId = null
                        selectedRecipeTitle = ""
                        selectedRecipeIngredients = ""
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }

    // LISTADO DE RECETAS PARA ELIMINAR
    if (showDeleteList) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteList = false 
                selectedRecipeId = null
            },
            title = { Text("Eliminar Receta") },
            text = {
                LazyColumn {
                    items(recipeList) { (id, title) ->
                        Text(
                            text = title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRecipeId = id
                                    selectedRecipeTitle = title
                                }
                                .padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedRecipeId != null) {
                            scope.launch {
                                deleteRecipe(db, selectedRecipeId!!, context) {
                                    showDeleteList = false
                                    selectedRecipeId = null
                                    loadRecipes(db) { recipes -> recipeList = recipes }
                                }
                            }
                        }
                    },
                    enabled = selectedRecipeId != null
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { 
                        showDeleteList = false
                        selectedRecipeId = null
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}


@Composable
fun RecipeForm(
    title: String,
    ingredients: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var newTitle by remember { mutableStateOf(title) }
    var newIngredients by remember { mutableStateOf(ingredients) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Receta") },
        text = {
            Column {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Título de la Receta") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newIngredients,
                    onValueChange = { newIngredients = it },
                    label = { Text("Ingredientes (separados por comas)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(newTitle, newIngredients) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun RecipeList(
    recipeList: List<Pair<String, String>>, // Aquí solo se recibe el título de la receta
    onRecipeSelected: (String, String) -> Unit
) {
    // Lista para almacenar recetas con ingredientes
    val recipesWithIngredients = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    // Cargar los ingredientes junto con los títulos de las recetas
    LaunchedEffect(recipeList) {
        val updatedRecipes = mutableListOf<Pair<String, String>>()
        for (recipe in recipeList) {
            val recipeId = recipe.first
            val recipeTitle = recipe.second
            // Obtener los ingredientes de Firestore
            FirebaseFirestore.getInstance().collection("recipes")
                .document(recipeId)
                .get()
                .addOnSuccessListener { document ->
                    val ingredients = document.get("ingredients") as? List<String> ?: emptyList()
                    // Combinar título e ingredientes
                    updatedRecipes.add(Pair(recipeTitle, ingredients.joinToString(", ")))
                    // Actualizar el estado con los nuevos datos
                    recipesWithIngredients.value = updatedRecipes
                }
        }
    }

    // Mostrar la lista con los ingredientes al lado
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Selecciona una Receta") },
        text = {
            LazyColumn {
                items(recipesWithIngredients.value) { recipe ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onRecipeSelected(recipe.first, recipe.second) }
                    ) {
                        // Mostrar el título de la receta
                        Text(
                            text = recipe.first,
                            modifier = Modifier.weight(1f) // Para que ocupe el espacio disponible
                        )

                        // Mostrar los ingredientes al lado
                        Text(
                            text = recipe.second,
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color.Gray // Opcional: poner los ingredientes en gris
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {}) { Text("Cerrar") }
        }
    )
}




// Añadir receta a Firestore
suspend fun addRecipeToDatabase(
    title: String,
    ingredients: String,
    context: android.content.Context,
    db: FirebaseFirestore
) {
    val recipe = hashMapOf(
        "title" to title, // Título de la receta
        "ingredients" to ingredients.split(",").map { it.trim() } // Ingredientes separados por comas
    )

    db.collection("recipes")
        .add(recipe) // Añade la receta a la colección
        .addOnSuccessListener {
            Toast.makeText(context, "Receta añadida correctamente", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al añadir receta", Toast.LENGTH_SHORT).show()
        }
}

// Modificar receta en Firestore
suspend fun modifyRecipeInDatabase(
    recipeId: String,
    title: String,
    ingredients: String,
    context: android.content.Context,
    db: FirebaseFirestore
) {
    val updatedRecipe = mapOf(
        "title" to title,
        "ingredients" to ingredients.split(",").map { it.trim() }
    )

    db.collection("recipes").document(recipeId)
        .update(updatedRecipe)
        .addOnSuccessListener {
            Toast.makeText(context, "Receta modificada correctamente", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al modificar receta", Toast.LENGTH_SHORT).show()
        }
}

// Eliminar receta de Firestore
suspend fun deleteRecipeFromDatabase(recipeId: String, context: android.content.Context, db: FirebaseFirestore) {
    db.collection("recipes").document(recipeId)
        .delete()
        .addOnSuccessListener {
            Toast.makeText(context, "Receta eliminada correctamente", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al eliminar receta", Toast.LENGTH_SHORT).show()
        }
}

// Cargar recetas desde Firestore
fun loadRecipes(db: FirebaseFirestore, onComplete: (List<Pair<String, String>>) -> Unit) {
    db.collection("recipes")
        .get()
        .addOnSuccessListener { documents ->
            val recipes = documents.map { it.id to it.getString("title").orEmpty() }
            onComplete(recipes)
        }
}

// Cargar ingredientes de una receta desde Firestore
suspend fun loadRecipeIngredients(db: FirebaseFirestore, recipeId: String, onComplete: (String) -> Unit) {
    db.collection("recipes").document(recipeId)
        .get()
        .addOnSuccessListener { document ->
            val ingredients = document.get("ingredients") as? List<String> ?: emptyList()
            onComplete(ingredients.joinToString(", "))
        }
}

// Actualizar receta en Firestore
suspend fun updateRecipe(
    db: FirebaseFirestore,
    recipeId: String,
    title: String,
    ingredients: String,
    context: android.content.Context,
    onComplete: () -> Unit
) {
    val updatedRecipe = mapOf(
        "title" to title,
        "ingredients" to ingredients.split(",").map { it.trim() }
    )

    db.collection("recipes").document(recipeId)
        .update(updatedRecipe)
        .addOnSuccessListener {
            Toast.makeText(context, "Receta actualizada correctamente", Toast.LENGTH_SHORT).show()
            onComplete()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al actualizar receta", Toast.LENGTH_SHORT).show()
        }
}

// Eliminar receta de Firestore
suspend fun deleteRecipe(
    db: FirebaseFirestore,
    recipeId: String,
    context: android.content.Context,
    onComplete: () -> Unit
) {
    db.collection("recipes").document(recipeId)
        .delete()
        .addOnSuccessListener {
            Toast.makeText(context, "Receta eliminada correctamente", Toast.LENGTH_SHORT).show()
            onComplete()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al eliminar receta", Toast.LENGTH_SHORT).show()
        }
}
