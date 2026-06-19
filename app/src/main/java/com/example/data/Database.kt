package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val businessName: String,
    val userName: String,
    val passwordPlain: String
)

@Entity(tableName = "budget_months")
data class BudgetMonth(
    @PrimaryKey val monthId: String, // e.g. "Jun 2026", "Jul 2026"
    val startingCapital: Double = 0.0
)

@Entity(tableName = "capital_incomes")
data class CapitalIncome(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthId: String,
    val amount: Double,
    val type: String, // "Investment" | "Loan" | "Business income" | "Support Fund"
    val note: String
)

@Entity(tableName = "capital_expenses")
data class CapitalExpense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthId: String,
    val amount: Double,
    val category: String, // "Raw Material" | "Transport" | "Food" | "Electricity" | "Other Costs" | "Wastage" | "Courier Costs"
    val note: String
)

@Entity(tableName = "product_items")
data class ProductItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val costBasis: Double,
    val profitMarginPercent: Double,
    val sellingPrice: Double,
    val stockCount: Int,
    val soldCount: Int = 0
)

@Entity(tableName = "selling_chamber_treasury")
data class SellingChamberTreasury(
    @PrimaryKey val id: Int = 1,
    val accumulatedRevenue: Double = 0.0
)

@Entity(tableName = "treasury_transactions")
data class TreasuryTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthId: String,
    val type: String, // "Income" | "Settlement" | "Savings"
    val category: String, // e.g. "Product Selling income", "Extra support income", "Gift income", "Other income", etc.
    val amount: Double,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface BudgetDao {
    // Profile
    @Query("SELECT * FROM business_profile WHERE id = 1")
    fun getProfile(): Flow<BusinessProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: BusinessProfile)

    // Month
    @Query("SELECT * FROM budget_months ORDER BY monthId DESC")
    fun getAllMonths(): Flow<List<BudgetMonth>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonth(month: BudgetMonth)

    // Capital Incomes
    @Query("SELECT * FROM capital_incomes WHERE monthId = :monthId")
    fun getCapitalIncomes(monthId: String): Flow<List<CapitalIncome>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapitalIncome(income: CapitalIncome)

    @Delete
    suspend fun deleteCapitalIncome(income: CapitalIncome)

    // Capital Expenses
    @Query("SELECT * FROM capital_expenses WHERE monthId = :monthId")
    fun getCapitalExpenses(monthId: String): Flow<List<CapitalExpense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapitalExpense(expense: CapitalExpense)

    @Delete
    suspend fun deleteCapitalExpense(expense: CapitalExpense)

    // Products
    @Query("SELECT * FROM product_items ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductItem)

    @Delete
    suspend fun deleteProduct(product: ProductItem)

    // Selling Chamber Treasury
    @Query("SELECT * FROM selling_chamber_treasury WHERE id = 1")
    fun getSellingChamberTreasury(): Flow<SellingChamberTreasury?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSellingChamberTreasury(treasury: SellingChamberTreasury)

    // Treasury Transactions
    @Query("SELECT * FROM treasury_transactions WHERE monthId = :monthId ORDER BY timestamp DESC")
    fun getTreasuryTransactions(monthId: String): Flow<List<TreasuryTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreasuryTransaction(transaction: TreasuryTransaction)

    @Delete
    suspend fun deleteTreasuryTransaction(transaction: TreasuryTransaction)
}

@Database(
    entities = [
        BusinessProfile::class,
        BudgetMonth::class,
        CapitalIncome::class,
        CapitalExpense::class,
        ProductItem::class,
        SellingChamberTreasury::class,
        TreasuryTransaction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
}
