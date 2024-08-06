package com.isu.apitracker.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.isu.apitracker.ApiTrackerViewModel
import com.isu.apitracker.R
import com.isu.apitracker.presentation.screens.TabConstants.tabs
import org.json.JSONObject

object TabConstants {
    const val REQUEST_TAB = "Request"
    const val RESPONSE_TAB = "Response"
    val tabs = listOf(REQUEST_TAB, RESPONSE_TAB)

}

@Composable
fun RequestResponseScreen(navController: NavHostController, viewModel: ApiTrackerViewModel) {
    val selectedTabIndex = remember {
        mutableStateOf(0)
    }

    Scaffold(
        containerColor = Color.LightGray.copy(0.5f),
        topBar = {
            Row (
                Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(Color.White)){

                    Column(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .height(50.dp)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null
                                ) {
                                    selectedTabIndex.value = 0
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(text = tabs[0])
                        }
                        AnimatedVisibility(visible = selectedTabIndex.value==0, enter = scaleIn(), exit = scaleOut()) {
                            Row (modifier = Modifier
                                .height(5.dp)
                                .fillMaxWidth()
                                .background(Color.Gray)){

                            }
                        }
                    }
                Column(Modifier
                        .fillMaxWidth(1f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .height(50.dp)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null
                                ) {
                                    selectedTabIndex.value = 1
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(text = tabs[1])
                        }
                        AnimatedVisibility(visible = selectedTabIndex.value==1, enter = scaleIn(), exit = scaleOut()) {
                            Row (modifier = Modifier
                                .height(5.dp)
                                .fillMaxWidth()
                                .background(Color.Gray)){

                            }
                        }
                    }


            }
        }
    ) { innerPadding ->
        val copyManager=LocalClipboardManager.current
        AnimatedVisibility(visible = selectedTabIndex.value==0,enter= slideInHorizontally { -it }, exit = slideOutHorizontally { -it }) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = innerPadding.calculateTopPadding(),
                        16.dp
                    )

                    .verticalScroll(rememberScrollState())
            ) {
                Column (Modifier.padding(top = 16.dp)){
                    Text(text = "Headers:", fontWeight = FontWeight.Bold)
                    Text(text = viewModel.selectedApi.value?.requestHeaders?.replace("""\""","")?.split(",")?.joinToString (",\n")?:"No headers")
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Request:", fontWeight = FontWeight.Bold)
                if(viewModel.selectedApi.value?.request!=null||viewModel.selectedApi.value?.request?.isNotEmpty()==true){
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(
                            Color(
                                0xF0141414
                            )
                        )
                        .padding(16.dp),){
                        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                            IconButton(onClick = { /*TODO*/ }, modifier = Modifier) {
                                Icon(painter = painterResource(id =R.drawable.baseline_content_copy_24 ) , contentDescription = "", tint = Color.LightGray)
                            }
                        }
                        Text(text = viewModel.selectedApi.value?.request?:"No response found", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(
                            Color(
                                0xF0141414
                            )
                        )
                        .padding(16.dp),)
                    {
                        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                            IconButton(onClick = {
                                if(viewModel.selectedApi.value?.response!=null && viewModel.selectedApi.value?.response!!.isNotEmpty()==true){
                                    copyManager.setText(AnnotatedString("${viewModel.selectedApi.value?.decodedRequest}"))
                                }

                            }, modifier = Modifier) {
                                Icon(painter = painterResource(id =R.drawable.baseline_content_copy_24 ) , contentDescription = "", tint = Color.LightGray)
                            }
                        }


                        Text(text = viewModel.selectedApi.value?.decodedRequest?:"No response found", color = Color.White)
                    }
                }



            }
        }
        AnimatedVisibility(visible = selectedTabIndex.value==1,enter= slideInHorizontally { it }, exit = slideOutHorizontally { it }) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = innerPadding.calculateTopPadding(),
                        16.dp
                    )

                    .verticalScroll(rememberScrollState())
            ) {
                Column (Modifier.padding(top = 16.dp)){
                    Text(text = "Headers:", fontWeight = FontWeight.Bold)
                    Text(text = viewModel.selectedApi.value?.responseHeaders?.replace("""\""","")?.split(",")?.joinToString (",\n")?:"No headers")
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Response:", fontWeight = FontWeight.Bold)
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        Color(
                            0xF0141414
                        )
                    )
                    .padding(16.dp),)
                {
                    Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                        IconButton(onClick = {
                            if(viewModel.selectedApi.value?.response!=null && viewModel.selectedApi.value?.response!!.isNotEmpty()==true){
                                copyManager.setText(AnnotatedString("${viewModel.selectedApi.value?.response}"))
                            }

                        }, modifier = Modifier) {
                            Icon(painter = painterResource(id =R.drawable.baseline_content_copy_24 ) , contentDescription = "", tint = Color.LightGray)
                        }
                    }


                    Text(text = viewModel.selectedApi.value?.response?:"No response found", color = Color.White)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "DecodedResponse:", fontWeight = FontWeight.Bold)
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        Color(
                            0xF0141414
                        )
                    )
                    .padding(16.dp),)
                {
                    Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                        IconButton(onClick = {
                            if(viewModel.selectedApi.value?.response!=null && viewModel.selectedApi.value?.response!!.isNotEmpty()==true){
                                copyManager.setText(AnnotatedString("${viewModel.selectedApi.value?.response}"))
                            }

                        }, modifier = Modifier) {
                            Icon(painter = painterResource(id =R.drawable.baseline_content_copy_24 ) , contentDescription = "", tint = Color.LightGray)
                        }
                    }


                    Text(text = viewModel.selectedApi.value?.decodedOutput?:"No response found", color = Color.White)
                }


            }
        }

    }
}


