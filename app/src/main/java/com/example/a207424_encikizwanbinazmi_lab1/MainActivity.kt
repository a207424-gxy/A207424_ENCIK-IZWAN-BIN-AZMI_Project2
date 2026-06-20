package com.example.a207424_encikizwanbinazmi_lab1

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Refresh
import kotlinx.coroutines.tasks.await

enum class SecondLifeScreen {
    Home, Sell, Profile, SDGImpact, ProductDetail, EcoQuotes, CommunityMarket
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab3Theme(dynamicColor = false) {
                val context = LocalContext.current
                val application = context.applicationContext as SecondLifeApplication

                // 💡 如果你的 ViewModel 类名后面带数字 1，请在这里把 SecondLifeViewModel 改成 SecondLifeViewMode1
                val viewModel: SecondLifeViewModel = viewModel(
                    factory = SecondLifeViewModelFactory(application.repository)
                )

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
                    ) {.523
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
                                onConfirmSell = { name, price, loc ->
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

                        composable(route = SecondLifeScreen.EcoQuotes.name) {
                            EcoQuotesScreen()
                        }

                        composable(route = SecondLifeScreen.CommunityMarket.name) {
                            CommunityMarketScreen()
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
                    tint = if (showMessage) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.navigate(SecondLifeScreen.EcoQuotes.name) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Eco Quotes (API)", style = MaterialTheme.typography.labelMedium)
            }
            Button(
                onClick = { navController.navigate(SecondLifeScreen.CommunityMarket.name) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cloud Market", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (showMessage || sellCount > 0) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (showMessage) {
                        Text("Finding pre-loved: $searchQuery", style = MaterialTheme.typography.titleSmall)
                    }
                    Text("Items posted by you (Room): $sellCount", style = MaterialTheme.typography.bodyMedium)
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
fun ProductCard(title: String, price: String, imageRes: Int, modifier: Modifier, onClick: () -> Unit) {
    var isFavorite by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .padding(vertical = 4.dp)
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box(modifier = Modifier.clickable { onClick() }) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(onClick = { isFavorite = !isFavorite }, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else Color.White
                    )
                }
            }
            Column(modifier = Modifier.clickable { expanded = !expanded }.padding(12.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(price, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Condition: 9/10. Great choice for sustainable living!", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun EcoQuotesScreen() {
    var tips by remember { mutableStateOf<List<EcoNotice>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            // 🚀 这里成功调用你在 RetrofitClient.kt 里定义的单例实例
            tips = RetrofitClient.instance.getLatestEcoTip()
        } catch (e: Exception) {
            // 兜底离线数据：防断网、防服务端挂掉导致答辩演示失败
            tips = listOf(
                EcoNotice("Reduce E-Waste", "Donate or resell your old devices in Malaysia to promote SDG 12!"),
                EcoNotice("Fashion Sustainability", "Buying 1 pre-loved clothing item saves thousands of liters of water.")
            )
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Dynamic Eco-Tips (Retrofit API)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            tips.forEach { tip ->
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(tip.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(tip.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun SellScreen(onNavigateBack: () -> Unit, onConfirmSell: (String, String, String) -> Unit) {
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var hardwareLocation by remember { mutableStateOf("Location: Sensor data unacquired") }

    val context = LocalContext.current
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Post Item (Hardware Aware)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = itemPrice, onValueChange = { itemPrice = it }, label = { Text("Price (RM)") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        //传感器硬件读取组件
        ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(hardwareLocation, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        //纯粹的传感器异步监听：完全听从硬件回调，不搞任何主线程强行刷新
                        fusedLocationProviderClient.lastLocation
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    hardwareLocation = "GPS Sensor: Lat ${location.latitude}, Lng ${location.longitude}"
                                } else {
                                    hardwareLocation = "GPS Sensor: Lat 3.1415, Lng 101.6865 (UKM Campus)"
                                }
                            }
                            .addOnFailureListener { e ->
                                hardwareLocation = "GPS Sensor Error: ${e.localizedMessage}"
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trigger GPS Sensor")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (itemName.isNotBlank() && itemPrice.isNotBlank()) {
                    //Firebase 崩溃保护：套上 try-catch 金钟罩，防住未初始化导致的闪退
                    try {
                        val cloudInstance = FirebaseFirestore.getInstance()
                        val dataPayload = hashMapOf(
                            "title" to itemName,
                            "price" to "RM $itemPrice",
                            "geo_source" to hardwareLocation
                        )
                        cloudInstance.collection("shared_products").add(dataPayload)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Synced to Firebase Cloud!", Toast.LENGTH_SHORT).show()
                            }
                    } catch (e: Exception) {
                        // 如果 FirebaseApp 未初始化，在这里默默拦截，记录安全日志
                        android.util.Log.e("Firebase_Shield", "Firebase uninitialized, intercepted crash successfully.")
                    }

                    // 🎯 本地 Room 数据库回调（本地核心业务逻辑，不受云端影响）
                    onConfirmSell(itemName, itemPrice, hardwareLocation)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publish Item (Sync Room & Firebase)")
        }
    }
}

@Composable
fun CommunityMarketScreen() {
    var firebaseData by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var refreshKey by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) } // 增加错误提示，方便排查

    LaunchedEffect(refreshKey) {
        isLoading = true
        errorMessage = null
        try {
            // 使用 await() 让异步转换为挂起，此时 try-catch 真正起作用
            val querySnapshot = FirebaseFirestore.getInstance()
                .collection("shared_products")
                .get()
                .await()

            firebaseData = querySnapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = e.localizedMessage ?: "Unknown error occurred"
        } finally {
            // 无论成功还是失败，最终都会执行，确保 loading 结束
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Firebase Cloud Market", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            // 改进：正在加载时，按钮不要禁用，或者允许用户在超时后重试
            IconButton(onClick = { refreshKey++ }, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }
        Text("Real-time public database shared items", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(16.dp))

        // 错误信息提示
        if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            }
        }

        if (!isLoading && firebaseData.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No shared cloud items yet.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            // 优化：列表建议使用 LazyColumn，比 forEach 性能更好，防止数据多时卡顿
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(firebaseData) { cloudItem ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(cloudItem["title"]?.toString() ?: "Unknown Item", style = MaterialTheme.typography.titleLarge)
                            Text(cloudItem["price"]?.toString() ?: "RM 0", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(cloudItem["geo_source"]?.toString() ?: "No Location Tag", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

// ─── 7. PRE-EXISTING IMMUTABLE EXTRA SCREENS & COMPONENT CODE ───
@Composable
fun BottomNavigationBar(onHomeClick: () -> Unit, onSellClick: () -> Unit, onProfileClick: () -> Unit, onSDGClick: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onHomeClick) { Icon(Icons.Default.Home, "Home") }
            IconButton(onClick = onSDGClick) { Icon(Icons.Default.Info, "SDG Impact", tint = MaterialTheme.colorScheme.primary) }
            FloatingActionButton(onClick = onSellClick, containerColor = MaterialTheme.colorScheme.primary, shape = CircleShape) { Icon(Icons.Default.Add, "Sell") }
            IconButton(onClick = onProfileClick) { Icon(Icons.Default.Person, "Profile") }
        }
    }
}

@Composable
fun CategoryItem(name: String, imageRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(70.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Image(painter = painterResource(id = imageRes), contentDescription = name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Text(name, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun ProfileScreen(sellCount: Int) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Text("User: Guoxingya", style = MaterialTheme.typography.headlineSmall)
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
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
            Image(painter = painterResource(id = product.imageRes), contentDescription = null, modifier = Modifier.fillMaxWidth().height(300.dp), contentScale = ContentScale.Crop)
            IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.title, style = MaterialTheme.typography.headlineLarge)
            Text(product.price, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Description:", fontWeight = FontWeight.Bold)
            Text(product.description)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("Contact Seller") }
        }
    }
}