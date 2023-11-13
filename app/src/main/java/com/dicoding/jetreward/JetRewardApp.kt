package com.dicoding.jetreward

import android.content.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.dicoding.jetreward.ui.navigation.*
import com.dicoding.jetreward.ui.screen.cart.*
import com.dicoding.jetreward.ui.screen.detail.*
import com.dicoding.jetreward.ui.screen.home.*
import com.dicoding.jetreward.ui.screen.profile.*
import com.dicoding.jetreward.ui.theme.*

@Composable
fun JetRewardApp(
    modifier: Modifier = Modifier, navHostController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.DetailReward.route) {
                BottomBar(navHostController)
            }
        }, modifier = modifier

    ) { innerPadding ->
        NavHost(
            navController = navHostController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navigateToDetail = { rewardId ->
                    navHostController.navigate(Screen.DetailReward.createRoute(rewardId))
                })
            }
            composable(Screen.Cart.route) {
                val context = LocalContext.current
                CartScreen(
                    onOrderButtonClicked = { message ->
                        shareOrder(context, message)
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }

            composable(
                route = Screen.DetailReward.route,
                arguments = listOf(navArgument("rewardId") {
                    type = NavType.LongType
                }),

            ) {
                val id = it.arguments?.getLong("rewardId") ?: -1L
                DetailScreen(rewardId = id, navigateBack = {
                    navHostController.navigateUp()
                }, navigateToCart = {
                    navHostController.popBackStack()
                    navHostController.navigate(Screen.Cart.route) {
                        popUpTo(navHostController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
            }
        }
    }
}

@Composable
fun BottomBar(
    navHostController: NavHostController, modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.menu_home),
                icon = Icons.Default.Home,
                screen = Screen.Home
            ),
            NavigationItem(
                title = stringResource(R.string.menu_cart),
                icon = Icons.Default.ShoppingCart,
                screen = Screen.Cart
            ),
            NavigationItem(
                title = stringResource(R.string.menu_profile),
                icon = Icons.Default.AccountCircle,
                screen = Screen.Profile
            ),
        )

        navigationItems.map { item ->
            NavigationBarItem(selected = currentRoute == item.screen.route, onClick = {
                navHostController.navigate(item.screen.route) {
                    popUpTo(navHostController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    restoreState = true
                    launchSingleTop = true
                }
            }, icon = {
                Icon(
                    contentDescription = item.title, imageVector = item.icon
                )
            })
        }
    }
}

private fun shareOrder(context: Context, summary: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.dicoding_reward))
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.dicoding_reward)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun JetHeroesAppPreview() {
    JetRewardTheme {
        JetRewardApp()
    }
}
