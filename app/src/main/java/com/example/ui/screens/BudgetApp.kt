package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.*
import com.example.ui.BudgetViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetApp(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val profile by viewModel.profileState.collectAsStateWithLifecycle()
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()

    // Screen content
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!isLoggedIn) {
            AuthScreen(
                profile = profile,
                loginError = loginError,
                onRegister = { business, user, pass ->
                    viewModel.registerBusiness(business, user, pass)
                },
                onLogin = { business, user, pass ->
                    viewModel.loginUser(business, user, pass)
                }
            )
        } else {
            val businessProfile = profile ?: BusinessProfile(
                businessName = "My Business",
                userName = "Admin",
                passwordPlain = ""
            )
            MainDashboard(
                viewModel = viewModel,
                profile = businessProfile
            )
        }
    }
}

@Composable
fun AuthScreen(
    profile: BusinessProfile?,
    loginError: String?,
    onRegister: (String, String, String) -> Unit,
    onLogin: (String, String, String) -> Unit
) {
    val isRegistered = profile != null
    var businessName by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Set default when registered for easier testing
    LaunchedEffect(profile) {
        if (profile != null) {
            businessName = profile.businessName
            userName = profile.userName
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant glowing neon banner using the generated image asset
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_finance_hologram),
                    contentDescription = "Finance Planning System Art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Linear dark glow overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.82f)
                                )
                            )
                        )
                )

                // App heading overlayed beautifully
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "BIZ BUDGET",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                    )
                    Text(
                        text = "Financial Command Center",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header details
                Text(
                    text = if (isRegistered) "Welcome back, log in below" else "Setup business workspace",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                // Error handler
                if (loginError != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                            Text(
                                text = loginError,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // Input fields
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Business Name") },
                    leadingIcon = { Icon(Icons.Filled.Business, "business logo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("business_name_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Owner User Name") },
                    leadingIcon = { Icon(Icons.Filled.AccountCircle, "user icon") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Access Password") },
                    leadingIcon = { Icon(Icons.Filled.Lock, "lock icon") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        if (isRegistered) {
                            onLogin(businessName, userName, password)
                        } else {
                            if (businessName.isNotBlank() && userName.isNotBlank() && password.isNotBlank()) {
                                onRegister(businessName, userName, password)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .height(54.dp)
                        .testTag("auth_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isRegistered) Icons.Filled.Login else Icons.Filled.AppRegistration,
                            contentDescription = "icon"
                        )
                        Text(
                            text = if (isRegistered) "Verify Identity" else "Configure Register Workspace",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isRegistered) {
                    TextButton(
                        onClick = {
                            // Developer bypass/registration wipe helper
                            onRegister(businessName, userName, password)
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Or Register New Workspace",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboard(
    viewModel: BudgetViewModel,
    profile: BusinessProfile
) {
    // Collect active months
    val currentMonthId by viewModel.currentMonth.collectAsStateWithLifecycle()
    val monthDetails by viewModel.currentMonthDetails.collectAsStateWithLifecycle()
    val allMonths by viewModel.months.collectAsStateWithLifecycle()

    // Sub flows for current active month
    val capIncomes by viewModel.capitalIncomes.collectAsStateWithLifecycle()
    val capExpenses by viewModel.capitalExpenses.collectAsStateWithLifecycle()
    val treasuryTxS by viewModel.treasuryTransactions.collectAsStateWithLifecycle()

    // Selling Chamber data
    val productsList by viewModel.productsSource.collectAsStateWithLifecycle()
    val chamberTreasury by viewModel.sellingChamberTreasury.collectAsStateWithLifecycle()

    // Calculate core math
    val startingCapital = monthDetails?.startingCapital ?: 0.0

    // Main Treasury calculations
    val totalMainIncomeText = treasuryTxS.filter { it.type == "Income" }.sumOf { it.amount }
    val totalMainSettlement = treasuryTxS.filter { it.type == "Settlement" }.sumOf { it.amount }
    val totalMainSavings = treasuryTxS.filter { it.type == "Savings" }.sumOf { it.amount }

    // Capital for Net month = starting + income - settlement - savings
    val remainingCapitalNextMonth = startingCapital + totalMainIncomeText - totalMainSettlement - totalMainSavings

    // Capital budget planning calculations
    val totalCapIncome = capIncomes.sumOf { it.amount }
    val totalCapExpenses = capExpenses.sumOf { it.amount }

    // Dialog state
    var showAddMonthDialog by remember { mutableStateOf(false) }
    var rolloverInputMonth by remember { mutableStateOf("") }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Budget Plan", "Main Treasury", "Settlements & Savings", "Chamber 📦")

    Scaffold(
        topBar = {
            // Elegant Custom Header: Dynamic Neon Branding Card with gradient backgrounds
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .padding(start = 20.dp, end = 20.dp, top = 46.dp, bottom = 20.dp)
                ) {
                    // Row for Business Branding Name Header & Log-out Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Beautiful, colourful, high fidelity way on top
                            Text(
                                text = profile.businessName.uppercase(),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 28.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            // User name in small
                            Text(
                                text = "👤 active manager: @${profile.userName.lowercase()}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.85f),
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }

                        // Logout Icon
                        IconButton(
                            onClick = { viewModel.logout() },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Filled.Logout, "Exit Board")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Month Selector Arrow bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val prevIndex = allMonths.indexOfFirst { it.monthId == currentMonthId } + 1
                                if (prevIndex < allMonths.size) {
                                    viewModel.selectMonth(allMonths[prevIndex].monthId)
                                }
                            },
                            enabled = allMonths.indexOfFirst { it.monthId == currentMonthId } < allMonths.size - 1
                        ) {
                            Icon(Icons.Filled.ChevronLeft, "Previous Month", tint = Color.White)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ACTIVE FINANCIAL PERIOD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.72f),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = currentMonthId.uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }

                        IconButton(
                            onClick = {
                                val nextIndex = allMonths.indexOfFirst { it.monthId == currentMonthId } - 1
                                if (nextIndex >= 0) {
                                    viewModel.selectMonth(allMonths[nextIndex].monthId)
                                }
                            },
                            enabled = allMonths.indexOfFirst { it.monthId == currentMonthId } > 0
                        ) {
                            Icon(Icons.Filled.ChevronRight, "Next Month", tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // General Stats & Rollover Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "STARTING BALANCE",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = String.format("$%.2f", startingCapital),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        // Month to month dynamic linker
                        Button(
                            onClick = {
                                rolloverInputMonth = incrementMonthId(currentMonthId)
                                showAddMonthDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Link, "link", modifier = Modifier.size(16.dp))
                                Text("Link Month to Month", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Small summary dashboard row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Product Sales", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val totalProductTransfers = treasuryTxS.filter { it.category == "Product Selling income" }.sumOf { it.amount }
                            Text(
                                text = String.format("$%.2f", totalProductTransfers),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Treasury In", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = String.format("$%.2f", totalMainIncomeText),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF4CAF50))
                            )
                        }

                        Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.End) {
                            Text("Next Month Cap", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = String.format("$%.2f", remainingCapitalNextMonth),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.tertiary),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Centralized M3 Segmented Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = TabRowDefaults.run { Modifier.tabIndicatorOffset(tabPositions[selectedTab]) },
                        color = MaterialTheme.colorScheme.primary,
                        height = 3.dp
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Tab Content Switches
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> BudgetPlanTab(
                        monthId = currentMonthId,
                        incomes = capIncomes,
                        expenses = capExpenses,
                        totalIncome = totalCapIncome,
                        totalExpense = totalCapExpenses,
                        onAddIncome = { amt, type, note -> viewModel.addCapitalIncome(amt, type, note) },
                        onDeleteIncome = { viewModel.deleteCapitalIncome(it) },
                        onAddExpense = { amt, cat, note -> viewModel.addCapitalExpense(amt, cat, note) },
                        onDeleteExpense = { viewModel.deleteCapitalExpense(it) }
                    )
                    1 -> MainTreasuryTab(
                        transactions = treasuryTxS.filter { it.type == "Income" },
                        totalIncome = totalMainIncomeText,
                        onAddTransaction = { cat, amt, desc ->
                            viewModel.addTreasuryTransaction("Income", cat, amt, desc)
                        },
                        onDeleteTransaction = { viewModel.deleteTreasuryTransaction(it) }
                    )
                    2 -> SettlementsSavingsTab(
                        transactions = treasuryTxS.filter { it.type != "Income" },
                        totalSettlement = totalMainSettlement,
                        totalSavings = totalMainSavings,
                        onAddTransaction = { type, cat, amt, desc ->
                            viewModel.addTreasuryTransaction(type, cat, amt, desc)
                        },
                        onDeleteTransaction = { viewModel.deleteTreasuryTransaction(it) }
                    )
                    3 -> SellingChamberTab(
                        products = productsList,
                        chamberTreasury = chamberTreasury,
                        onAddProduct = { name, cost, margin, stock ->
                            viewModel.addOrUpdateProduct(name, cost, margin, stock)
                        },
                        onDeleteProduct = { viewModel.deleteProduct(it) },
                        onSellPiece = { viewModel.sellProductPiece(it) },
                        onTransferRevenue = { viewModel.transferSellingRevenueToMainTreasury() }
                    )
                }
            }
        }
    }

    // Rollover Dialog Setup
    if (showAddMonthDialog) {
        Dialog(onDismissRequest = { showAddMonthDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Link Month to Month Treasury",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Text(
                        text = "This will rollover the remaining balance from $currentMonthId ($${String.format("%.2f", remainingCapitalNextMonth)}) as the starting capital of the next planning month. Enter name of Next month below.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = rolloverInputMonth,
                        onValueChange = { rolloverInputMonth = it },
                        label = { Text("Next Month Label") },
                        placeholder = { Text("e.g. Jul 2026") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddMonthDialog = false }) {
                            Text("Hold Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (rolloverInputMonth.isNotBlank()) {
                                    viewModel.linkToNextMonth(
                                        nextMonthId = rolloverInputMonth,
                                        currentTreasuryBalance = remainingCapitalNextMonth
                                    )
                                    showAddMonthDialog = false
                                }
                            }
                        ) {
                            Text("Confirm & Rollover")
                        }
                    }
                }
            }
        }
    }
}

// Tab 1: Budget Planner Tab (Capital Incomes and Capital Expenses)
@Composable
fun BudgetPlanTab(
    monthId: String,
    incomes: List<CapitalIncome>,
    expenses: List<CapitalExpense>,
    totalIncome: Double,
    totalExpense: Double,
    onAddIncome: (Double, String, String) -> Unit,
    onDeleteIncome: (CapitalIncome) -> Unit,
    onAddExpense: (Double, String, String) -> Unit,
    onDeleteExpense: (CapitalExpense) -> Unit
) {
    var expandedIncomeForm by remember { mutableStateOf(false) }
    var expandedExpenseForm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Summary header inside Tab
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Capital Net Budget", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        Text(
                            text = String.format("$%.2f", totalIncome - totalExpense),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("In: $${String.format("%.2f", totalIncome)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                            Text("Out: $${String.format("%.2f", totalExpense)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section A: Capital Income Block
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Input, "income icon", tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Main Capital Inflow", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }

                        IconButton(onClick = { expandedIncomeForm = !expandedIncomeForm }) {
                            Icon(
                                imageVector = if (expandedIncomeForm) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "toggle form"
                            )
                        }
                    }

                    AnimatedVisibility(visible = expandedIncomeForm) {
                        AddIncomeForm(onAdd = { amt, type, note ->
                            onAddIncome(amt, type, note)
                            expandedIncomeForm = false
                        })
                    }

                    if (incomes.isEmpty()) {
                        Text(
                            text = "No Capital incoming lines defined yet for this month.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            incomes.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.type, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        if (item.note.isNotBlank()) {
                                            Text(item.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = String.format("+$%.2f", item.amount),
                                            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(onClick = { onDeleteIncome(item) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section B: Capital Expenses Block
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Output, "expense icon", tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Main Capital Expenses", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }

                        IconButton(onClick = { expandedExpenseForm = !expandedExpenseForm }) {
                            Icon(
                                imageVector = if (expandedExpenseForm) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "toggle form"
                            )
                        }
                    }

                    AnimatedVisibility(visible = expandedExpenseForm) {
                        AddExpenseForm(onAdd = { amt, cat, note ->
                            onAddExpense(amt, cat, note)
                            expandedExpenseForm = false
                        })
                    }

                    if (expenses.isEmpty()) {
                        Text(
                            text = "No operational capital allocations logged for this period.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            expenses.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.category, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        if (item.note.isNotBlank()) {
                                            Text(item.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = String.format("-$%.2f", item.amount),
                                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(onClick = { onDeleteExpense(item) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun AddIncomeForm(onAdd: (Double, String, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedTypeIndex by remember { mutableStateOf(0) }
    val incomeTypes = listOf("Business income", "Investment", "Loan", "Support Fund")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Inflow Amount ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Select Source Category", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            incomeTypes.forEachIndexed { i, type ->
                FilterChip(
                    selected = selectedTypeIndex == i,
                    onClick = { selectedTypeIndex = i },
                    label = { Text(type) }
                )
            }
        }

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Details (Note)") },
            placeholder = { Text("e.g. Seed loan from bank") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val amtVal = amount.toDoubleOrNull() ?: 0.0
                if (amtVal > 0.0) {
                    onAdd(amtVal, incomeTypes[selectedTypeIndex], note)
                    amount = ""
                    note = ""
                }
            },
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Insert Income Log")
        }
    }
}

@Composable
fun AddExpenseForm(onAdd: (Double, String, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val expenseCategories = listOf(
        "Raw Material", "Transport", "Food", "Electricity", "Other Costs", "Wastage", "Courier Costs"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Expense Amount ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Select Expense Category", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        // Wrapped row flow or simple scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            expenseCategories.forEachIndexed { i, cat ->
                FilterChip(
                    selected = selectedCategoryIndex == i,
                    onClick = { selectedCategoryIndex = i },
                    label = { Text(cat) }
                )
            }
        }

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Details (Note)") },
            placeholder = { Text("e.g. Packaging cardboard box cost") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val amtVal = amount.toDoubleOrNull() ?: 0.0
                if (amtVal > 0.0) {
                    onAdd(amtVal, expenseCategories[selectedCategoryIndex], note)
                    amount = ""
                    note = ""
                }
            },
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Insert Expense Log")
        }
    }
}

// Tab 2: Main Treasury Tab (Treasury incomes, supports, gifts, custom renamables)
@Composable
fun MainTreasuryTab(
    transactions: List<TreasuryTransaction>,
    totalIncome: Double,
    onAddTransaction: (String, Double, String) -> Unit,
    onDeleteTransaction: (TreasuryTransaction) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var customCategoryName by remember { mutableStateOf("") }

    val incomeCategories = listOf(
        "Product Selling income",
        "Extra support income",
        "Gift income",
        "Other income",
        "Renamable Custom Income"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick visual Treasury summary card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Treasury Incomes", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32))
                        Text(
                            text = String.format("$%.2f", totalIncome),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        )
                    }
                    Button(
                        onClick = { showForm = !showForm },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (showForm) Icons.Filled.ExpandLess else Icons.Filled.Add, "add")
                            Text("Record Income", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Expanded insertion entry form
        if (showForm) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Log Dynamic Treasury Inflow", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Income Amount ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Select Categorization Label", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            incomeCategories.forEachIndexed { i, cat ->
                                FilterChip(
                                    selected = selectedCategoryIndex == i,
                                    onClick = { selectedCategoryIndex = i },
                                    label = { Text(cat) }
                                )
                            }
                        }

                        // If user selects "Renamable Custom Income", show extra title textfield
                        AnimatedVisibility(visible = selectedCategoryIndex == 4) {
                            OutlinedTextField(
                                value = customCategoryName,
                                onValueChange = { customCategoryName = it },
                                label = { Text("Input Custom (Renamed) Income Label") },
                                placeholder = { Text("e.g. Website Consulting, Affiliate etc.") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Optional Records/Description") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val amtVal = amount.toDoubleOrNull() ?: 0.0
                                if (amtVal > 0.0) {
                                    val finalCat = if (selectedCategoryIndex == 4) {
                                        if (customCategoryName.isNotBlank()) customCategoryName else "Customized income"
                                    } else {
                                        incomeCategories[selectedCategoryIndex]
                                    }

                                    onAddTransaction(finalCat, amtVal, description)
                                    amount = ""
                                    description = ""
                                    customCategoryName = ""
                                    showForm = false
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("Save Inflow Record")
                        }
                    }
                }
            }
        }

        // Transactions list view
        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AccountBalanceWallet, "empty", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No income records found for this period in Treasury.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(transactions) { tx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.category,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        )
                        if (tx.description.isNotBlank()) {
                            Text(tx.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = String.format("+$%.2f", tx.amount),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                        )
                        IconButton(onClick = { onDeleteTransaction(tx) }) {
                            Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// Tab 3: Settlements and Savings (Linked to Treasury)
@Composable
fun SettlementsSavingsTab(
    transactions: List<TreasuryTransaction>,
    totalSettlement: Double,
    totalSavings: Double,
    onAddTransaction: (String, String, Double, String) -> Unit,
    onDeleteTransaction: (TreasuryTransaction) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var actionTypeIndex by remember { mutableStateOf(0) } // 0: Settlement, 1: Savings
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Settlement sub categories
    var settlementCategoryIndex by remember { mutableStateOf(0) }
    val settlementCategories = listOf(
        "Loan settlement",
        "Money returns",
        "Loan installments",
        "Investment returns",
        "Investment profit dividend"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Deductions & Outflows", style = MaterialTheme.typography.labelSmall, color = Color(0xFFE65100))
                            Text(
                                text = String.format("$%.2f", totalSettlement + totalSavings),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            )
                        }

                        Button(
                            onClick = { showForm = !showForm },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (showForm) Icons.Filled.ExpandLess else Icons.Filled.Add, "add")
                                Text("Add Deduction", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Loan Settlements: $${String.format("%.2f", totalSettlement)}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFFD84315)))
                        Text("Business Savings: $${String.format("%.2f", totalSavings)}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF00796B)))
                    }
                }
            }
        }

        // Dynamic settlement/savings entry form
        if (showForm) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Record Settlements or Business Savings", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                        // Switch: Deductions vs Savings
                        TabRow(
                            selectedTabIndex = actionTypeIndex,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            divider = {}
                        ) {
                            Tab(selected = actionTypeIndex == 0, onClick = { actionTypeIndex = 0 }) {
                                Text("Settlement Log", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            }
                            Tab(selected = actionTypeIndex == 1, onClick = { actionTypeIndex = 1 }) {
                                Text("Savings Allocation", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Log Amount ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // If "Settlement Log", choose particular categories
                        if (actionTypeIndex == 0) {
                            Text("Deduction Reason", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                settlementCategories.forEachIndexed { idx, item ->
                                    FilterChip(
                                        selected = settlementCategoryIndex == idx,
                                        onClick = { settlementCategoryIndex = idx },
                                        label = { Text(item) }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Notes & Records") },
                            placeholder = { Text("e.g. Paid first installment to bank") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val amtVal = amount.toDoubleOrNull() ?: 0.0
                                if (amtVal > 0.0) {
                                    val type = if (actionTypeIndex == 0) "Settlement" else "Savings"
                                    val cat = if (actionTypeIndex == 0) settlementCategories[settlementCategoryIndex] else "Savings"

                                    onAddTransaction(type, cat, amtVal, description)
                                    amount = ""
                                    description = ""
                                    showForm = false
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (actionTypeIndex == 0) Color(0xFFEF6C00) else Color(0xFF00796B)
                            )
                        ) {
                            Text("Commit Cash Outflow")
                        }
                    }
                }
            }
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.LockClock, "empty logo", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No recorded settlements or savings registered for this period.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(transactions) { tx ->
                val isSavings = tx.type == "Savings"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSavings) Color(0xFFE0F2F1) else Color(0xFFFBE9E7)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.category,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSavings) Color(0xFF004D40) else Color(0xFFD84315)
                            )
                        )
                        if (tx.description.isNotBlank()) {
                            Text(tx.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = String.format("-$%.2f", tx.amount),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = if (isSavings) Color(0xFF00796B) else Color(0xFFD84315)
                            )
                        )
                        IconButton(onClick = { onDeleteTransaction(tx) }) {
                            Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// Tab 4: Isolated Selling Chamber (Calculate cost, add profit margins, stock listing, automatic Treasury record)
@Composable
fun SellingChamberTab(
    products: List<ProductItem>,
    chamberTreasury: SellingChamberTreasury,
    onAddProduct: (String, Double, Double, Int) -> Unit,
    onDeleteProduct: (ProductItem) -> Unit,
    onSellPiece: (ProductItem) -> Unit,
    onTransferRevenue: () -> Unit
) {
    var showAddProductForm by remember { mutableStateOf(false) }

    // Finished level goods form fields
    var productName by remember { mutableStateOf("") }
    var rawMaterialCost by remember { mutableStateOf("") }
    var profitMarginPercent by remember { mutableStateOf("25") }
    var stockCountInput by remember { mutableStateOf("") }

    // Live calculation calculations
    val matCostVal = rawMaterialCost.toDoubleOrNull() ?: 0.0
    val marginPercentVal = profitMarginPercent.toDoubleOrNull() ?: 0.0
    val computedSellingPrice = matCostVal * (1.0 + (marginPercentVal / 100.0))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Warning Info about Isolated Nature as specified by user
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Cabin,
                        "chamber icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "ISOLATED SELLING CHAMBER",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        )
                        Text(
                            "Revenue tracked in this chamber does NOT affect main flows until transferred down to the Treasury below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.82f)
                        )
                    }
                }
            }
        }

        // Isolated selling income treasury display panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "SELLING CHAMBER TREASURY",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = String.format("$%.2f", chamberTreasury.accumulatedRevenue),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Button(
                                onClick = onTransferRevenue,
                                enabled = chamberTreasury.accumulatedRevenue > 0.0,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.AccountBalance, "move to main", modifier = Modifier.size(16.dp))
                                    Text("Add to Main Treasury", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Finished product creations toggler bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FINISHED GOOD REGISTER",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                )

                TextButton(onClick = { showAddProductForm = !showAddProductForm }) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(if (showAddProductForm) Icons.Filled.ExpandLess else Icons.Filled.AddCircle, "toggle form")
                        Text(if (showAddProductForm) "Close Creator" else "Calculate & Stock Goods")
                    }
                }
            }
        }

        // Product cost structure computation form
        if (showAddProductForm) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Analyze Product Costings", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = { Text("Product / Good Name") },
                            placeholder = { Text("e.g. Leather Wallet") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = rawMaterialCost,
                                onValueChange = { rawMaterialCost = it },
                                label = { Text("Cost Basis ($)") },
                                placeholder = { Text("e.g. 10.00") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = profitMarginPercent,
                                onValueChange = { profitMarginPercent = it },
                                label = { Text("Profit Margin (%)") },
                                placeholder = { Text("e.g. 35") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = stockCountInput,
                            onValueChange = { stockCountInput = it },
                            label = { Text("Starting Stock Quantity") },
                            placeholder = { Text("e.g. 50") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Real-time calculation readout box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Dynamic Math Preview", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Selling Unit Price:",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = String.format("$%.2f", computedSellingPrice),
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.tertiary)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val sCountVal = stockCountInput.toIntOrNull() ?: 1
                                if (productName.isNotBlank() && matCostVal > 0.0) {
                                    onAddProduct(productName, matCostVal, marginPercentVal, sCountVal)
                                    productName = ""
                                    rawMaterialCost = ""
                                    profitMarginPercent = "25"
                                    stockCountInput = ""
                                    showAddProductForm = false
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Create & List in Stock")
                        }
                    }
                }
            }
        }

        // Products catalogue list
        if (products.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.CardGiftcard, "empty", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No goods registered in the Selling Chamber stock log yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(products) { item ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text(
                                    text = String.format("Cost: $%.2f | Margin: %.1f%%", item.costBasis, item.profitMarginPercent),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            IconButton(onClick = { onDeleteProduct(item) }) {
                                Icon(Icons.Filled.Delete, "delete product", tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("In Stock Quantity", style = MaterialTheme.typography.labelSmall)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Badge(
                                        containerColor = if (item.stockCount > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            text = if (item.stockCount > 0) "${item.stockCount} Left" else "Sold Out",
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }

                                    if (item.soldCount > 0) {
                                        Text("(${item.soldCount} Sold)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format("$%.2f", item.sellingPrice),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                )

                                Button(
                                    onClick = { onSellPiece(item) },
                                    enabled = item.stockCount > 0,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.ShoppingCart, "sell", modifier = Modifier.size(14.dp))
                                        Text("Sell 1", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// Utility: Auto calculation helper for rollover date formats (e.g. "Jun 2026" -> "Jul 2026")
fun incrementMonthId(monthId: String): String {
    return try {
        val parser = SimpleDateFormat("MMM yyyy", Locale.US)
        val date = parser.parse(monthId) ?: return monthId
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, 1)
        parser.format(calendar.time)
    } catch (e: Exception) {
        monthId
    }
}
