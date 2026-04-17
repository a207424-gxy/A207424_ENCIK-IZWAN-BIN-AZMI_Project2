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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //让 UI 延伸到状态栏/导航栏
        setContent {
            // 1. 使用你刚修改的自定义主题，并关闭动态变色
            Lab3Theme(dynamicColor = false) {

                var sellCount by remember { mutableIntStateOf(0) }
                var showSellDialog by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // 2. 这里的背景色现在会自动读取你 lightScheme 里的 background 颜色
                    //使用主题背景色
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        BottomNavigationBar(onSellClick = { showSellDialog = true })
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // 确保 SecondLifeHome 内部也使用了 MaterialTheme.colorScheme
                        SecondLifeHome(sellCount = sellCount)

                        if (showSellDialog) {
                            AlertDialog(
                                onDismissRequest = { showSellDialog = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        sellCount++
                                        showSellDialog = false
                                    }) { Text("Confirm") }
                                },
                                // 这里的文字也会自动变成主题定义的 onSurface 颜色
                                title = { Text("List a New Item") },
                                text = { Text("Do you want to add a new pre-loved item to SecondLife?") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
//sellCount 从外部传进来（MainActivity）
fun SecondLifeHome(sellCount: Int) {
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

        // 2. 状态提示卡片 (使用 Material3 Card)
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
            style = MaterialTheme.typography.titleLarge // 使用主题字体
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

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // 1. 定义数据列表，包含：标题, 价格, 图片资源ID
            val products = listOf(
                Triple("Vintage Camera", "RM 120", R.drawable.camera1),
                Triple("Used Textbook", "RM 25", R.drawable.textbook1),
                Triple("Retro Watch", "RM 85", R.drawable.watch1),
                Triple("Coffee Mug", "RM 15", R.drawable.mug1),
                Triple("Desk Lamp", "RM 40", R.drawable.lamp1),
                Triple("Gaming Mouse", "RM 90", R.drawable.mouse1)
            )

            // 2. 这里的 pair[0].third 就会对应上面列表里的图片 ID 了
            //每2个一组 → 一行
            products.chunked(2).forEach { pair ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    // 第一列
                    ProductCard(pair[0].first, pair[0].second, pair[0].third, Modifier.weight(1f))

                    Spacer(modifier = Modifier.width(8.dp))

                    // 第二列（检查是否有第二个元素）
                    if (pair.size > 1) {
                        ProductCard(pair[1].first, pair[1].second, pair[1].third, Modifier.weight(1f))
                    } else {
                        // 如果是奇数个，放一个透明的 Spacer 占位以保持比例
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
fun ProductCard(title: String, price: String, imageRes: Int, modifier: Modifier) {
    var isFavorite by remember { mutableStateOf(false) }
    // Lab 3 新增：展开状态
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        //接收外部传进来的布局规则
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded } // 点击切换展开
            .animateContentSize( // 平滑改变高度
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
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

            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = price,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )

                // 展开后显示的详细信息
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Condition: 9/10. Great choice for sustainable living! This pre-loved item helps reduce waste.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(onSellClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp // 增加阴影感
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Search, "Explore", true)
            BottomNavItem(Icons.Default.FavoriteBorder, "For You")

            // Sell 按钮适配主题色
            FloatingActionButton(
                onClick = { onSellClick() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(56.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Sell")
            }

            BottomNavItem(Icons.Default.Notifications, "Updates")
            BottomNavItem(Icons.Default.Person, "Me")
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
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean = false) {
    val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, label, tint = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}