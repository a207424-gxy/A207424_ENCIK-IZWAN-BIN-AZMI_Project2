package com.example.a207424_encikizwanbinazmi_lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207424_encikizwanbinazmi_lab1.ui.theme.Lab3Theme
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController

// 定义“一组固定的选项”
enum class SecondLifeScreen {
    Home,
    Sell,
    Profile,
    SDGImpact, // 新增：SDG 影响页
    ProductDetail // 新增：商品详情页
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab3Theme(dynamicColor = false) {
                //控制页面跳转
                val navController = rememberNavController()
                val viewModel: SecondLifeViewModel = viewModel()
                val uiState = viewModel.uiState

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        //这里的底部栏现在控制导航跳转
                        BottomNavigationBar(
                            onHomeClick = { navController.navigate(SecondLifeScreen.Home.name) },
                            onSellClick = { navController.navigate(SecondLifeScreen.Sell.name) },
                            onProfileClick = { navController.navigate(SecondLifeScreen.Profile.name) },
                            onSDGClick = { navController.navigate(SecondLifeScreen.SDGImpact.name) }
                        )
                    }
                ) { innerPadding ->
                    // NavHost管理页面切换
                    NavHost(
                        navController = navController,
                        //默认打开home
                        startDestination = SecondLifeScreen.Home.name,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 页面一：首页
                        composable(route = SecondLifeScreen.Home.name) {
                            // 传入 viewModel 中的数据
                            SecondLifeHome(
                                sellCount = uiState.sellCount,
                                productList = uiState.productList,
                                navController = navController
                            )
                        }

                        // 页面二：发布商品页面 (new)
                        composable(route = SecondLifeScreen.Sell.name) {
                            SellScreen(
                                //返回上一页
                                onNavigateBack = { navController.popBackStack() },
                                onConfirmSell = { name, price ->
                                    viewModel.addProduct(name, price)
                                    navController.popBackStack() // 发布后返回首页
                                }
                            )
                        }

                        // 页面三：个人资料页
                        composable(route = SecondLifeScreen.Profile.name) {
                            ProfileScreen(sellCount = uiState.sellCount)
                        }
                        // 页面四：SDG Impact 页面
                        composable(route = SecondLifeScreen.SDGImpact.name) {
                            SDGImpactScreen()
                        }

                        // 页面五：商品详情页
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
//sellCount 从外部传进来（MainActivity）
fun SecondLifeHome(sellCount: Int,productList: List<Product>,navController: NavHostController) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
        // 移除了硬编码的背景色，Scaffold 会自动处理
    ) {
        // 1. 顶部搜索栏
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search...") },
                modifier = Modifier.weight(1f),
                // 使用主题定义的圆角
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    // 使用主题色作为容器背景
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

        // 2. 状态提示卡片
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
                    Text(
                        "Total items listed by Guoxingya: $sellCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        //  3. Categories
        Text(
            text = "Categories",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        //横向滚动列表
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item { CategoryItem("Fashion", R.drawable.fashion) }
            item { CategoryItem("Electronics", R.drawable.electronics) }
            item { CategoryItem("Home", R.drawable.home) }
            item { CategoryItem("Books", R.drawable.books) }
            item { CategoryItem("Toys", R.drawable.toys) }
        }

        // 4. Recent Items
        Text(
            text = "Recent Items",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )

        // 在 SecondLifeHome 内部
        // 在 SecondLifeHome 内部
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            productList.chunked(2).forEachIndexed { rowIndex, pair ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    // 第一个商品
                    val index1 = rowIndex * 2
                    val product1 = pair[0]
                    ProductCard(
                        title = product1.title,
                        price = product1.price,
                        imageRes = product1.imageRes,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // 导航到详情页
                            navController.navigate("${SecondLifeScreen.ProductDetail.name}/$index1")
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (pair.size > 1) {
                        val index2 = rowIndex * 2 + 1
                        val product2 = pair[1]
                        ProductCard(
                            title = product2.title,
                            price = product2.price,
                            imageRes = product2.imageRes,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate("${SecondLifeScreen.ProductDetail.name}/$index2")
                            }
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
            // 1. 图片区域：点击跳转到详情页
            Box(modifier = Modifier.clickable { onClick() }) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )
                // 收藏按钮保持原样
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

            // 2. 文字区域：点击切换
            Column(
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(12.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = price,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )

                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Condition: 9/10. Great choice for sustainable living! This pre-loved item helps reduce waste.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    // 提示用户可以查看详情
                    Text(
                        text = "Click image for more details >",
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
            // 首页图标
            IconButton(onClick = onHomeClick) {
                Icon(Icons.Default.Home, "Home") // 建议改回 Home 图标更直观
            }

            // 2.SDG Impact 按钮
            IconButton(onClick = onSDGClick) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "SDG Impact",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Sell 按钮
            FloatingActionButton(
                onClick = onSellClick,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Sell")
            }

            // 个人中心图标
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, "Profile")
            }
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
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun SellScreen(onNavigateBack: () -> Unit, onConfirmSell: (String, String) -> Unit) {
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("List New Item", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = itemPrice, onValueChange = { itemPrice = it }, label = { Text("Price (RM)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { onConfirmSell(itemName, itemPrice) }, modifier = Modifier.fillMaxWidth()) {
            Text("Confirm Post")
        }
    }
}

@Composable
fun ProfileScreen(sellCount: Int) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Text("User: Guoxingya", style = MaterialTheme.typography.headlineSmall)
        Text("Total Items Listed: $sellCount", style = MaterialTheme.typography.bodyLarge)
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
            Image(
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
            Button(onClick = { /* 购买逻辑 */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Contact Seller")
            }
        }
    }
}