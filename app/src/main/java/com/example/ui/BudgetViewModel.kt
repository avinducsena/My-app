package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    // Login & Profile State
    val profileState: StateFlow<BusinessProfile?> = repository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Screen State / Month
    private val _currentMonth = MutableStateFlow("")
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()

    init {
        // Default to current date Month-Year representation
        val defaultMonth = SimpleDateFormat("MMM yyyy", Locale.US).format(Date())
        _currentMonth.value = defaultMonth
        
        // Let's seed current month database entry automatically if empty later on
        viewModelScope.launch {
            repository.getAllMonths().firstOrNull()?.let { list ->
                if (list.isEmpty()) {
                    repository.insertMonth(BudgetMonth(defaultMonth, 0.0))
                } else {
                    _currentMonth.value = list.first().monthId
                }
            }
        }
    }

    // List of months registered
    val months: StateFlow<List<BudgetMonth>> = repository.getAllMonths()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic data flow based on currently selected month
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMonthDetails: StateFlow<BudgetMonth?> = _currentMonth
        .flatMapLatest { mId ->
            repository.getAllMonths().map { list ->
                list.find { it.monthId == mId } ?: BudgetMonth(mId, 0.0)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val capitalIncomes: StateFlow<List<CapitalIncome>> = _currentMonth
        .flatMapLatest { mId -> repository.getCapitalIncomes(mId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val capitalExpenses: StateFlow<List<CapitalExpense>> = _currentMonth
        .flatMapLatest { mId -> repository.getCapitalExpenses(mId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val treasuryTransactions: StateFlow<List<TreasuryTransaction>> = _currentMonth
        .flatMapLatest { mId -> repository.getTreasuryTransactions(mId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Products & Chamber Treasury (Isolated)
    val productsSource: StateFlow<List<ProductItem>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sellingChamberTreasury: StateFlow<SellingChamberTreasury> = repository.getSellingChamberTreasury()
        .map { it ?: SellingChamberTreasury(id = 1, accumulatedRevenue = 0.0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SellingChamberTreasury(id = 1, accumulatedRevenue = 0.0))

    // Profile Actions
    fun registerBusiness(business: String, user: String, pass: String) {
        viewModelScope.launch {
            val prof = BusinessProfile(id = 1, businessName = business, userName = user, passwordPlain = pass)
            repository.saveProfile(prof)
            // Ensure month is seeded on registration
            val defaultMonth = SimpleDateFormat("MMM yyyy", Locale.US).format(Date())
            repository.insertMonth(BudgetMonth(defaultMonth, 1000.0)) // Seed starting amount
            _currentMonth.value = defaultMonth
            _isLoggedIn.value = true
            _loginError.value = null
        }
    }

    fun loginUser(business: String, user: String, pass: String) {
        viewModelScope.launch {
            val profile = profileState.value
            if (profile != null) {
                if (profile.businessName.equals(business, ignoreCase = true) &&
                    profile.userName.equals(user, ignoreCase = true) &&
                    profile.passwordPlain == pass) {
                    _isLoggedIn.value = true
                    _loginError.value = null
                } else {
                    _loginError.value = "Invalid Business name, User name, or Password"
                }
            } else {
                _loginError.value = "No business registered yet. Please Register first."
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    // Month Selector Actions
    fun selectMonth(monthId: String) {
        _currentMonth.value = monthId
    }

    fun addNewMonth(monthId: String, startingCapital: Double) {
        viewModelScope.launch {
            repository.insertMonth(BudgetMonth(monthId, startingCapital))
            _currentMonth.value = monthId
        }
    }

    // Capital Incomes
    fun addCapitalIncome(amount: Double, type: String, note: String) {
        viewModelScope.launch {
            repository.insertCapitalIncome(
                CapitalIncome(
                    monthId = _currentMonth.value,
                    amount = amount,
                    type = type,
                    note = note
                )
            )
        }
    }

    fun deleteCapitalIncome(income: CapitalIncome) {
        viewModelScope.launch {
            repository.deleteCapitalIncome(income)
        }
    }

    // Capital Expenses
    fun addCapitalExpense(amount: Double, category: String, note: String) {
        viewModelScope.launch {
            repository.insertCapitalExpense(
                CapitalExpense(
                    monthId = _currentMonth.value,
                    amount = amount,
                    category = category,
                    note = note
                )
            )
        }
    }

    fun deleteCapitalExpense(expense: CapitalExpense) {
        viewModelScope.launch {
            repository.deleteCapitalExpense(expense)
        }
    }

    // Selling Chamber (Products)
    fun addOrUpdateProduct(name: String, costBasis: Double, marginPercent: Double, stock: Int) {
        viewModelScope.launch {
            val sPrice = costBasis * (1.0 + (marginPercent / 100.0))
            repository.insertProduct(
                ProductItem(
                    name = name,
                    costBasis = costBasis,
                    profitMarginPercent = marginPercent,
                    sellingPrice = sPrice,
                    stockCount = stock
                )
            )
        }
    }

    fun deleteProduct(product: ProductItem) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun sellProductPiece(product: ProductItem) {
        if (product.stockCount <= 0) return
        viewModelScope.launch {
            // Decrease stock count, increase sold units
            val updated = product.copy(
                stockCount = product.stockCount - 1,
                soldCount = product.soldCount + 1
            )
            repository.insertProduct(updated)

            // Add piece revenue to isolated Selling Chamber Treasury
            val curRev = sellingChamberTreasury.value.accumulatedRevenue
            repository.saveSellingChamberTreasury(
                SellingChamberTreasury(id = 1, accumulatedRevenue = curRev + product.sellingPrice)
            )
        }
    }

    // Transfer selling isolated revenue to Main Treasury as "Product Selling income"
    fun transferSellingRevenueToMainTreasury() {
        val revToTransfer = sellingChamberTreasury.value.accumulatedRevenue
        if (revToTransfer <= 0.0) return

        viewModelScope.launch {
            // Reset isolated treasury
            repository.saveSellingChamberTreasury(SellingChamberTreasury(id = 1, accumulatedRevenue = 0.0))

            // Add transaction to Main Treasury
            repository.insertTreasuryTransaction(
                TreasuryTransaction(
                    monthId = _currentMonth.value,
                    type = "Income",
                    category = "Product Selling income",
                    amount = revToTransfer,
                    description = "Transferred isolated Selling Chamber revenue"
                )
            )
        }
    }

    // Main Treasury Actions
    fun addTreasuryTransaction(type: String, category: String, amount: Double, desc: String) {
        viewModelScope.launch {
            repository.insertTreasuryTransaction(
                TreasuryTransaction(
                    monthId = _currentMonth.value,
                    type = type,
                    category = category,
                    amount = amount,
                    description = desc
                )
            )
        }
    }

    fun deleteTreasuryTransaction(transaction: TreasuryTransaction) {
        viewModelScope.launch {
            repository.deleteTreasuryTransaction(transaction)
        }
    }

    // Link month to month: Advance to next month while copying Main Treasury balance
    fun linkToNextMonth(nextMonthId: String, currentTreasuryBalance: Double) {
        viewModelScope.launch {
            // In case the user input nextMonthId matches, we save next month with currentTreasuryBalance as startingCapital
            repository.insertMonth(
                BudgetMonth(
                    monthId = nextMonthId,
                    startingCapital = currentTreasuryBalance
                )
            )
            _currentMonth.value = nextMonthId
        }
    }
}

class BudgetViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
