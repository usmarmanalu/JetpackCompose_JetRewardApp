package com.dicoding.jetreward.ui.screen.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.*
import com.dicoding.jetreward.R
import com.dicoding.jetreward.di.*
import com.dicoding.jetreward.model.*
import com.dicoding.jetreward.ui.*
import com.dicoding.jetreward.ui.common.*
import com.dicoding.jetreward.ui.components.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory(Injection.provideRepository())
    ),
    navigateToDetail: (Long) -> Unit,
) {
    viewModel.uiState.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when (uiState) {
            is UiState.Loading -> {
                viewModel.getAllRewards()
            }

            is UiState.Success -> {
                HomeContent(
                    orderReward = uiState.data,
                    modifier = modifier,
                    navigateToDetail = navigateToDetail
                )
            }

            is UiState.Error -> {

            }
        }
    }
}

@Composable
fun HomeContent(
    orderReward: List<OrderReward>,
    modifier: Modifier = Modifier,
    navigateToDetail: (Long) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(orderReward) { data ->
            RewardItem(
                image = data.reward.image,
                title = data.reward.title,
                requiredPoint = data.reward.requiredPoint,
                modifier = Modifier.clickable {
                    navigateToDetail(data.reward.id)
                }
            )
        }
    }
}