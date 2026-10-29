package com.nutrix.app.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.nutrix.app.core.designsystem.NutrixColors
import com.nutrix.app.core.engine.HealthRatingEngine
import com.nutrix.app.core.engine.NutritionCalculator
import com.nutrix.app.core.model.*
import com.nutrix.app.data.repository.NutrixRepository
import com.nutrix.app.feature.assistant.AiAssistantScreen
import com.nutrix.app.feature.dietplan.DietPlanScreen
import com.nutrix.app.feature.fooddetails.FoodDetailsScreen
import com.nutrix.app.feature.foodhistory.FoodHistoryScreen
import com.nutrix.app.feature.foodsearch.FoodSearchScreen
import com.nutrix.app.feature.grocery.GroceryScreen
import com.nutrix.app.feature.home.HomeScreen
import com.nutrix.app.feature.onboarding.OnboardingScreen
import com.nutrix.app.feature.profile.ProfileScreen
import com.nutrix.app.feature.progress.ProgressScreen
import com.nutrix.app.feature.reminders.RemindersScreen
import com.nutrix.app.feature.scanner.ScannerScreen
import com.nutrix.app.feature.scanresult.ScanResultScreen
import com.nutrix.app.feature.welcome.WelcomeScreen
import kotlinx.coroutines.launch

object NutrixDestinations {
    const val WELCOME = "welcome"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val SCANNER = "scanner"
    const val SCAN_RESULT = "scan_result/{barcode}"
    const val FOOD_SEARCH = "food_search"
    const val FOOD_DETAILS = "food_details/{foodId}"
    const val FOOD_HISTORY = "food_history"
    const val DIET_PLAN = "diet_plan"
    const val PROGRESS = "progress"
    const val PROFILE = "profile"
    const val ASSISTANT = "assistant"
    const val GROCERY = "grocery"
    const val REMINDERS = "reminders"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NutrixNavGraph(
    repository: NutrixRepository,
    navController: NavHostController = rememberNavController()
) {
    val coroutineScope = rememberCoroutineScope()

    val userProfile by repository.getUserProfile().collectAsState(initial = UserProfile())
    val allFoods by repository.getAllFoods().collectAsState(initial = emptyList())
    val allMeals by repository.getAllMeals().collectAsState(initial = emptyList())
    val thisWeekPlans by repository.getDietPlansForWeek(0).collectAsState(initial = emptyList())
    val nextWeekPlans by repository.getDietPlansForWeek(1).collectAsState(initial = emptyList())
    val groceries by repository.getGroceries().collectAsState(initial = emptyList())
    val reminders by repository.getAllReminders().collectAsState(initial = emptyList())

    var currentDateIso by remember { mutableStateOf(NutritionCalculator.getTodayIsoDate()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentDateIso = NutritionCalculator.getTodayIsoDate()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val todayMeals = allMeals.filter { it.date == currentDateIso }
    val todaySummary = remember(todayMeals, currentDateIso) {
        NutritionCalculator.calculateDailySummary(
            date = currentDateIso,
            dayLabel = "Today",
            meals = todayMeals
        )
    }

    val waterGlasses by repository.getWaterLog(currentDateIso).collectAsState(initial = 4)
    val nextPlannedMeal = thisWeekPlans.firstOrNull { !it.isCompleted }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 5 primary tabs show the bottom navigation bar when soft keyboard is not active
    val bottomBarRoutes = listOf(
        NutrixDestinations.HOME,
        NutrixDestinations.FOOD_SEARCH,
        NutrixDestinations.ASSISTANT,
        NutrixDestinations.DIET_PLAN,
        NutrixDestinations.PROFILE
    )
    val isImeVisible = WindowInsets.isImeVisible
    val showBottomBar = currentRoute in bottomBarRoutes && !isImeVisible

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NutrixBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(NutrixDestinations.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = NutrixColors.BackgroundDark
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NutrixDestinations.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NutrixDestinations.WELCOME) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(NutrixDestinations.ONBOARDING)
                    }
                )
            }

            composable(NutrixDestinations.ONBOARDING) {
                OnboardingScreen(
                    currentProfile = userProfile,
                    onComplete = { updated ->
                        coroutineScope.launch {
                            repository.updateUserProfile(updated)
                            navController.navigate(NutrixDestinations.HOME) {
                                popUpTo(NutrixDestinations.WELCOME) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(NutrixDestinations.HOME) {
                HomeScreen(
                    userProfile = userProfile,
                    todaySummary = todaySummary,
                    waterGlasses = waterGlasses,
                    nextPlannedMeal = nextPlannedMeal,
                    onWaterChange = { count ->
                        coroutineScope.launch { repository.setWaterGlasses(currentDateIso, count) }
                    },
                    onNavigateToSearch = { navController.navigate(NutrixDestinations.FOOD_SEARCH) },
                    onNavigateToScanner = { navController.navigate(NutrixDestinations.SCANNER) },
                    onNavigateToDietPlan = { navController.navigate(NutrixDestinations.DIET_PLAN) },
                    onNavigateToHistory = { navController.navigate(NutrixDestinations.FOOD_HISTORY) },
                    onNavigateToProfile = { navController.navigate(NutrixDestinations.PROFILE) },
                    onNavigateToMealDetails = { foodId ->
                        navController.navigate("food_details/$foodId")
                    }
                )
            }

            composable(NutrixDestinations.SCANNER) {
                ScannerScreen(
                    onBarcodeDetected = { barcode ->
                        navController.navigate("scan_result/$barcode")
                    },
                    onClose = { navController.popBackStack() }
                )
            }

            composable(
                route = NutrixDestinations.SCAN_RESULT,
                arguments = listOf(navArgument("barcode") { type = NavType.StringType })
            ) { backStackEntry ->
                val barcode = backStackEntry.arguments?.getString("barcode") ?: "028400017008"
                var targetFood by remember { mutableStateOf<FoodItem?>(null) }

                LaunchedEffect(barcode) {
                    targetFood = repository.getFoodByBarcode(barcode) ?: allFoods.firstOrNull { it.barcode == barcode } ?: allFoods.firstOrNull()
                }

                if (targetFood != null) {
                    ScanResultScreen(
                        foodItem = targetFood!!,
                        onAddToToday = { food, mealType ->
                            coroutineScope.launch {
                                repository.logMeal(
                                    LoggedMeal(
                                        foodId = food.id,
                                        foodName = food.name,
                                        category = food.category,
                                        mealType = mealType,
                                        date = currentDateIso,
                                        servingSizeGrams = food.servingSizeGrams,
                                        servingDesc = food.servingUnit,
                                        calories = food.calories,
                                        protein = food.protein,
                                        carbs = food.carbs,
                                        fats = food.fats,
                                        fiber = food.fiber,
                                        sugar = food.sugar,
                                        sodium = food.sodium,
                                        rating = food.healthRating
                                    )
                                )
                                navController.navigate(NutrixDestinations.HOME) {
                                    popUpTo(NutrixDestinations.HOME) { inclusive = false }
                                }
                            }
                        },
                        onSaveProduct = { food ->
                            coroutineScope.launch { repository.saveCustomFood(food) }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(NutrixDestinations.FOOD_SEARCH) {
                FoodSearchScreen(
                    foods = allFoods,
                    onFoodClick = { food ->
                        navController.navigate("food_details/${food.id}")
                    },
                    onQuickAdd = { food, grams, portionDesc, mealType ->
                        coroutineScope.launch {
                            val scaled = NutritionCalculator.scaleNutrients(
                                baseCalories = food.calories,
                                baseProtein = food.protein,
                                baseCarbs = food.carbs,
                                baseFats = food.fats,
                                baseFiber = food.fiber,
                                baseSugar = food.sugar,
                                baseSodium = food.sodium,
                                baseGrams = food.servingSizeGrams,
                                targetGrams = grams
                            )
                            repository.logMeal(
                                LoggedMeal(
                                    foodId = food.id,
                                    foodName = food.name,
                                    category = food.category,
                                    mealType = mealType,
                                    date = currentDateIso,
                                    servingSizeGrams = grams,
                                    servingDesc = portionDesc,
                                    calories = scaled.calories,
                                    protein = scaled.protein,
                                    carbs = scaled.carbs,
                                    fats = scaled.fats,
                                    fiber = scaled.fiber,
                                    sugar = scaled.sugar,
                                    sodium = scaled.sodium,
                                    rating = food.healthRating
                                )
                            )
                        }
                    },
                    onAddCustomFood = { customFood ->
                        coroutineScope.launch { repository.saveCustomFood(customFood) }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = NutrixDestinations.FOOD_DETAILS,
                arguments = listOf(navArgument("foodId") { type = NavType.LongType })
            ) { backStackEntry ->
                val foodId = backStackEntry.arguments?.getLong("foodId") ?: 1L
                val foodItem = allFoods.firstOrNull { it.id == foodId } ?: allFoods.firstOrNull()

                if (foodItem != null) {
                    FoodDetailsScreen(
                        foodItem = foodItem,
                        onAddToToday = { loggedFood, grams, portionDesc, mealType ->
                            coroutineScope.launch {
                                val scaled = NutritionCalculator.scaleNutrients(
                                    baseCalories = loggedFood.calories,
                                    baseProtein = loggedFood.protein,
                                    baseCarbs = loggedFood.carbs,
                                    baseFats = loggedFood.fats,
                                    baseFiber = loggedFood.fiber,
                                    baseSugar = loggedFood.sugar,
                                    baseSodium = loggedFood.sodium,
                                    baseGrams = loggedFood.servingSizeGrams,
                                    targetGrams = grams
                                )
                                repository.logMeal(
                                    LoggedMeal(
                                        foodId = loggedFood.id,
                                        foodName = loggedFood.name,
                                        category = loggedFood.category,
                                        mealType = mealType,
                                        date = currentDateIso,
                                        servingSizeGrams = grams,
                                        servingDesc = portionDesc,
                                        calories = scaled.calories,
                                        protein = scaled.protein,
                                        carbs = scaled.carbs,
                                        fats = scaled.fats,
                                        fiber = scaled.fiber,
                                        sugar = scaled.sugar,
                                        sodium = scaled.sodium,
                                        rating = loggedFood.healthRating
                                    )
                                )
                                navController.navigate(NutrixDestinations.HOME) {
                                    popUpTo(NutrixDestinations.HOME) { inclusive = false }
                                }
                            }
                        },
                        onSave = { food ->
                            coroutineScope.launch { repository.saveCustomFood(food) }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(NutrixDestinations.FOOD_HISTORY) {
                FoodHistoryScreen(
                    allMeals = allMeals,
                    onDeleteMeal = { mealId ->
                        coroutineScope.launch { repository.deleteMeal(mealId) }
                    },
                    onUpdateMeal = { meal ->
                        coroutineScope.launch { repository.updateMeal(meal) }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NutrixDestinations.DIET_PLAN) {
                DietPlanScreen(
                    userProfile = userProfile,
                    allDietPlans = thisWeekPlans + nextWeekPlans,
                    onToggleCompleted = { planId, isCompleted ->
                        coroutineScope.launch { repository.setPlanCompleted(planId, isCompleted) }
                    },
                    onLogMealFromPlan = { plan ->
                        coroutineScope.launch {
                            val matchingFood = allFoods.firstOrNull { it.name.equals(plan.foodName, ignoreCase = true) }
                            val category = matchingFood?.category ?: FoodCategory.CURRIES
                            val rating = matchingFood?.healthRating ?: HealthRatingEngine.calculateRating(
                                category = category,
                                calories = plan.calories,
                                proteinGrams = plan.protein,
                                carbsGrams = plan.carbs,
                                fatsGrams = plan.fats,
                                fiberGrams = 3f,
                                sugarGrams = 2f,
                                sodiumMg = 150f
                            ).score

                            repository.logMeal(
                                LoggedMeal(
                                    foodId = matchingFood?.id ?: 0L,
                                    foodName = plan.foodName,
                                    category = category,
                                    mealType = plan.mealType,
                                    date = currentDateIso,
                                    servingSizeGrams = 200f,
                                    servingDesc = plan.portionDesc,
                                    calories = plan.calories,
                                    protein = plan.protein,
                                    carbs = plan.carbs,
                                    fats = plan.fats,
                                    rating = rating
                                )
                            )
                        }
                    },
                    onRegenerate = { weekOffset ->
                        coroutineScope.launch {
                            repository.regenerateWeekPlan(
                                weekOffset = weekOffset,
                                goal = userProfile.goalType,
                                preference = userProfile.dietaryPreference,
                                allergies = userProfile.allergies
                            )
                        }
                    },
                    onGoalChange = { newGoal ->
                        coroutineScope.launch {
                            val newCalories = NutritionCalculator.calculateTargetCalories(
                                weightKg = userProfile.weightKg,
                                heightCm = userProfile.heightCm,
                                age = userProfile.age,
                                goal = newGoal
                            )
                            repository.updateUserProfile(userProfile.copy(goalType = newGoal, dailyCalorieTarget = newCalories))
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NutrixDestinations.PROGRESS) {
                ProgressScreen(
                    allMeals = allMeals,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NutrixDestinations.PROFILE) {
                ProfileScreen(
                    userProfile = userProfile,
                    onUpdateProfile = { updated ->
                        coroutineScope.launch { repository.updateUserProfile(updated) }
                    },
                    onNavigateToReminders = { navController.navigate(NutrixDestinations.REMINDERS) },
                    onNavigateToGrocery = { navController.navigate(NutrixDestinations.GROCERY) },
                    onNavigateToAssistant = { navController.navigate(NutrixDestinations.ASSISTANT) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NutrixDestinations.ASSISTANT) {
                AiAssistantScreen(
                    userProfile = userProfile,
                    allMeals = allMeals,
                    allDietPlans = thisWeekPlans + nextWeekPlans,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NutrixDestinations.GROCERY) {
                GroceryScreen(
                    groceries = groceries,
                    onTogglePurchased = { item ->
                        coroutineScope.launch {
                            repository.updateGrocery(item.copy(isPurchased = !item.isPurchased))
                        }
                    },
                    onAddItem = { newItem ->
                        coroutineScope.launch { repository.addGrocery(newItem) }
                    },
                    onClearPurchased = {
                        coroutineScope.launch { repository.clearPurchasedGroceries() }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NutrixDestinations.REMINDERS) {
                RemindersScreen(
                    reminders = reminders,
                    onToggleReminder = { id, enabled ->
                        coroutineScope.launch { repository.setReminderEnabled(id, enabled) }
                    },
                    onAddReminder = { newReminder ->
                        coroutineScope.launch { repository.saveReminder(newReminder) }
                    },
                    onDeleteReminder = { id ->
                        coroutineScope.launch { repository.deleteReminder(id) }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun NutrixBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ai_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ai_glow"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NutrixColors.BackgroundDark.copy(alpha = 0.96f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(1.dp, NutrixColors.CardBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(68.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                BottomNavItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = currentRoute == NutrixDestinations.HOME,
                    onClick = { onNavigate(NutrixDestinations.HOME) }
                )

                // 2. Food
                BottomNavItem(
                    icon = Icons.Default.Restaurant,
                    label = "Food",
                    isSelected = currentRoute == NutrixDestinations.FOOD_SEARCH,
                    onClick = { onNavigate(NutrixDestinations.FOOD_SEARCH) }
                )

                // 3. Center Glowing AI Assistant
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NutrixColors.ElectricBlue, NutrixColors.VividViolet, NutrixColors.Magenta)
                            )
                        )
                        .border(
                            BorderStroke(
                                2.dp,
                                Brush.linearGradient(
                                    listOf(
                                        NutrixColors.ElectricBlue.copy(alpha = glowAlpha),
                                        NutrixColors.Magenta.copy(alpha = glowAlpha)
                                    )
                                )
                            ),
                            CircleShape
                        )
                        .clickable { onNavigate(NutrixDestinations.ASSISTANT) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 4. Diet Plan
                BottomNavItem(
                    icon = Icons.Default.CalendarToday,
                    label = "Diet Plan",
                    isSelected = currentRoute == NutrixDestinations.DIET_PLAN,
                    onClick = { onNavigate(NutrixDestinations.DIET_PLAN) }
                )

                // 5. Profile
                BottomNavItem(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    isSelected = currentRoute == NutrixDestinations.PROFILE,
                    onClick = { onNavigate(NutrixDestinations.PROFILE) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .defaultMinSize(minWidth = 56.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) NutrixColors.Magenta else NutrixColors.TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) NutrixColors.Magenta else NutrixColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
