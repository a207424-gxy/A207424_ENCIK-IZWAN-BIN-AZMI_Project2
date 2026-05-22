package com.example.a207424_encikizwanbinazmi_lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.a207424_encikizwanbinazmi_lab1.ui.theme.Lab3Theme
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class SecondLifeScreen {
    Home, Sell, Profile, SDGImpact, ProductDetail
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab3Theme(dynamicColor = false) {
                val context = LocalContext.current
                val application = context.applicationContext as SecondLifeApplication

                // ✅ Use Factory to inject repository into ViewModel
                val viewModel: SecondLifeViewModel = viewModel(
                    factory = SecondLifeViewModelFactory(application.repository)
                )

                // ✅ Collect StateFlow as Compose State
                val uiState by viewModel.uiState.collectAsState()

                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        BottomNavigationBar(
                            onHomeClick = { navController.navigate(SecondLifeScreen.Home.name) },
                            onSellClick = { navController.navigate(SecondLifeScreen.Sell.name) },
                            onProfileClick = { navController.navigate(SecondLifeScreen.Profile.name) },
                            onSDGClick = { navController.navigate(SecondLifeScreen.SDGImpact.name) }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = SecondLifeScreen.Home.name,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(route = SecondLifeScreen.Home.name) {
                            SecondLifeHome(
                                sellCount = uiState.sellCount,
                                productList = uiState.productList,
                                navController = navController
                            )
                        }

                        composable(route = SecondLifeScreen.Sell.name) {
                            SellScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onConfirmSell = { name, price ->
                                    viewModel.addProduct(name, price)
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(route = SecondLifeScreen.Profile.name) {
                            ProfileScreen(sellCount = uiState.sellCount)
                        }

                        composable(route = SecondLifeScreen.SDGImpact.name) {
                            SDGImpactScreen()
                        }

                        composable(route = "${SecondLifeScreen.ProductDetail.name}/{index}") { backStackEntry ->
                            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
                            val product = uiState.productList.getOrNull(index)
                            if (product != null) {
                                ProductDetailScreen(
                                    product = product,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Screens ────────────────────────────────────────────────

@Composable
fun SecondLifeHome(
    sellCount: Int,
    productList: List<Product>,
    navController: NavHostController
) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search...") },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            IconButton(onClick = { if (searchQuery.isNotEmpty()) showMessage = !showMessage }) {
                Icon(
                    Icons.Default.ShoppingCart,
                    null,
                    tint = if (showMessage) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showMessage || sellCount > 0) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (showMessage) {
                        Text(
                            "Finding pre-loved: $searchQuery",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    // ✅ sellCount now reflects actual Room DB count
                    Text(
                        "Items posted by you: $sellCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Text("Categories", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { CategoryItem("Fashion", R.drawable.fashion) }
            item { CategoryItem("Electronics", R.drawable.electronics) }
            item { CategoryItem("Home", R.drawable.home) }
            item { CategoryItem("Books", R.drawable.books) }
            item { CategoryItem("Toys", R.drawable.toys) }
        }

        Text("Recent Items", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            productList.chunked(2).forEachIndexed { rowIndex, pair ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    val index1 = rowIndex * 2
                    ProductCard(
                        title = pair[0].title,
                        price = pair[0].price,
                        imageRes = pair[0].imageRes,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("${SecondLifeScreen.ProductDetail.name}/$index1") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (pair.size > 1) {
                        val index2 = rowIndex * 2 + 1
                        ProductCard(
                            title = pair[1].title,
                            price = pair[1].price,
                            imageRes = pair[1].imageRes,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate("${SecondLifeScreen.ProductDetail.name}/$index2") }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ProductCard(
    title: String,
    price: String,
    imageRes: Int,
    modifier: Modifier,
    onClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .padding(vertical = 4.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box(modifier = Modifier.clickable { onClick() }) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else Color.White
                    )
                }
            }
            Column(
                modifier = Modifier.clickable { expanded = !expanded }.padding(12.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(price, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Condition: 9/10. Great choice for sustainable living!",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Click image for more details >",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onSellClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSDGClick: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onHomeClick) { Icon(Icons.Default.Home, "Home") }
            IconButton(onClick = onSDGClick) {
                Icon(Icons.Default.Info, "SDG Impact", tint = MaterialTheme.colorScheme.primary)
            }
            FloatingActionButton(
                onClick = onSellClick,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) { Icon(Icons.Default.Add, "Sell") }
            IconButton(onClick = onProfileClick) { Icon(Icons.Default.Person, "Profile") }
        }
    }
}

@Composable
fun CategoryItem(name: String, imageRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(70.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(name, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun SellScreen(onNavigateBack: () -> Unit, onConfirmSell: (String, String) -> Unit) {
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("List New Item", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = itemPrice,
            onValueChange = { itemPrice = it },
            label = { Text("Price (RM)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        // ✅ Saves to Room on click
        Button(
            onClick = {
                if (itemName.isNotBlank() && itemPrice.isNotBlank()) {
                    onConfirmSell(itemName, itemPrice)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm Post")
        }
    }
}

@Composable
fun ProfileScreen(sellCount: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text("User: Guoxingya", style = MaterialTheme.typography.headlineSmall)
        // ✅ Reflects actual Room DB count
        Text("Total Items Posted: $sellCount", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean = false) {
    val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, label, tint = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
fun SDGImpactScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SDG 12: Responsible Consumption", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("The Problem:", fontWeight = FontWeight.Bold)
                Text("Excessive waste from discarded electronics and fashion in Malaysia.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Our Solution:", fontWeight = FontWeight.Bold)
                Text("SecondLife encourages re-selling pre-loved items to extend product lifecycles.")
            }
        }
    }
}

@Composable
fun ProductDetailScreen(product: Product, onNavigateBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box {
            androidx.compose.foundation.Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentScale = ContentScale.Crop
            )
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.title, style = MaterialTheme.typography.headlineLarge)
            Text(product.price, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Description:", fontWeight = FontWeight.Bold)
            Text(product.description)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Text("Contact Seller")
            }
        }
    }
}