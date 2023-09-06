package com.example.bmicalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

            NavHost(navController = navController, startDestination = "home") {
                composable(route = "home") {
                    HomeScreen() { height, weight ->
                        mainViewModel.calculateBmi(height, weight)
                        navController.navigate("result")
                    }
                }

                composable(route = "result") {
                    ResultScreen(mainViewModel.bmi.value) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onSubmit: (Double, Double) -> Unit) {
    val (height, setHeight) = rememberSaveable {
        mutableStateOf("")
    }

    val (weight, setWeight) = rememberSaveable {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "비만도 계산기") })
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = height,
                onValueChange = { value ->
                    setHeight(value)
                },
                label = {
                    Text(text = "키")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = weight,
                onValueChange = { value ->
                    setWeight(value)
                },
                label = {
                    Text(text = "몸무게")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (height.isNotEmpty() && weight.isNotEmpty()) {
                    onSubmit(height.toDouble(), weight.toDouble())
                }

            }, modifier = Modifier.align(Alignment.End)) {
                Text(text = "결과")
            }
        }
    }
}

data class BmiData(
    val text: String,
    val iconResourceId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(bmi: Double, onArrowBackClick: () -> Unit) {

    val bmiData: BmiData = when {
        bmi >= 35 -> BmiData("고도 비만", R.drawable.baseline_sentiment_very_dissatisfied_24)
        bmi >= 30 -> BmiData("2단계 비만", R.drawable.baseline_sentiment_very_dissatisfied_24)
        bmi >= 25 -> BmiData("1단계 비만", R.drawable.baseline_sentiment_very_dissatisfied_24)
        bmi >= 23 -> BmiData("과체중", R.drawable.baseline_sentiment_very_dissatisfied_24)
        bmi >= 18.5 -> BmiData("정상", R.drawable.baseline_sentiment_very_satisfied_24)
        else -> BmiData("과체중", R.drawable.baseline_sentiment_very_dissatisfied_24)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { "비만도 계산기" },
            navigationIcon = {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.clickable {
                    onArrowBackClick()
                })
            },
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = bmiData.text, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(id = bmiData.iconResourceId),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(
                    color = Color.Black
                )
            )
        }
    }
}


class MainViewModel : ViewModel() {

    private val _bmi = mutableStateOf(0.0)
    val bmi: State<Double> = _bmi

    fun calculateBmi(height: Double, weight: Double) {
        _bmi.value = weight / (height / 100.0).pow(2.0)
    }

}