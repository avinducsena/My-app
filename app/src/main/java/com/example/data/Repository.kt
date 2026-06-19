package com.example.data

import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val dao: BudgetDao) {

    fun getProfile(): Flow<BusinessProfile?> = dao.getProfile()
    suspend fun saveProfile(profile: BusinessProfile) = dao.saveProfile(profile)

    fun getAllMonths(): Flow<List<BudgetMonth>> = dao.getAllMonths()
    suspend fun insertMonth(month: BudgetMonth) = dao.insertMonth(month)

    fun getCapitalIncomes(monthId: String): Flow<List<CapitalIncome>> = dao.getCapitalIncomes(monthId)
    suspend fun insertCapitalIncome(income: CapitalIncome) = dao.insertCapitalIncome(income)
    suspend fun deleteCapitalIncome(income: CapitalIncome) = dao.deleteCapitalIncome(income)

    fun getCapitalExpenses(monthId: String): Flow<List<CapitalExpense>> = dao.getCapitalExpenses(monthId)
    suspend fun insertCapitalExpense(expense: CapitalExpense) = dao.insertCapitalExpense(expense)
    suspend fun deleteCapitalExpense(expense: CapitalExpense) = dao.deleteCapitalExpense(expense)

    fun getAllProducts(): Flow<List<ProductItem>> = dao.getAllProducts()
    suspend fun insertProduct(product: ProductItem) = dao.insertProduct(product)
    suspend fun deleteProduct(product: ProductItem) = dao.deleteProduct(product)

    fun getSellingChamberTreasury(): Flow<SellingChamberTreasury?> = dao.getSellingChamberTreasury()
    suspend fun saveSellingChamberTreasury(treasury: SellingChamberTreasury) = dao.saveSellingChamberTreasury(treasury)

    fun getTreasuryTransactions(monthId: String): Flow<List<TreasuryTransaction>> = dao.getTreasuryTransactions(monthId)
    suspend fun insertTreasuryTransaction(transaction: TreasuryTransaction) = dao.insertTreasuryTransaction(transaction)
    suspend fun deleteTreasuryTransaction(transaction: TreasuryTransaction) = dao.deleteTreasuryTransaction(transaction)
}
