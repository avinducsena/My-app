package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.BudgetRepository
import com.example.ui.BudgetViewModel
import com.example.ui.BudgetViewModelFactory
import com.example.ui.screens.BudgetApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Build SQLite persistent database with fallback migration rules
    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "business_budget.db"
    ).fallbackToDestructiveMigration().build()

    // Constructor injection for architecture compliance
    val repository = BudgetRepository(db.budgetDao())
    val factory = BudgetViewModelFactory(repository)
    val viewModel = ViewModelProvider(this, factory)[BudgetViewModel::class.java]

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          BudgetApp(
              viewModel = viewModel,
              modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}
