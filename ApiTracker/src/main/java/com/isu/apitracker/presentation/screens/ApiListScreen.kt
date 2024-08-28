package com.isu.apitracker.presentation.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.isu.apitracker.presentation.viewmodel.ApiTrackerViewModel
import com.isu.apitracker.presentation.ApiTrackingActivity
import com.isu.apitracker.util.toEm


data class ApiListDataClass(
    val id: Int = 0,
    val statusCode: String = "",
    val requestMethod: String = "GET",
    val requestEndPoint: String = "/jdsjlkds/lksdjklj",
    val requestBaseURL: String = "com.google",
    val callTime: String = System.currentTimeMillis().toString(),
    val responseTime: String = "",
    val memoryConsumption: Long = 26,
    val request: String = "",
    val requestHeaders: Map<String, String> = mapOf(),
    val response: String = "",
    val responseHeaders: Map<String, String> = mapOf(),
    val delete: Boolean = false,
    val decodedRequest: List<String?> = emptyList(),
    val decodedOutput: List<String?> = emptyList(),
    val startTime: String,
)



@Preview
@Composable
fun ApiListScreen(navController: NavHostController, viewModel: ApiTrackerViewModel) {
    val addItemsToDelete = remember {
        mutableStateOf(false)
    }
    val selectAll = remember {
        mutableStateOf(false)
    }
    val context= LocalContext.current
    val activity= context as ApiTrackingActivity
    LaunchedEffect(key1 = Unit) {
        viewModel.getAllApi()
    }

    Scaffold(containerColor = Color.White, topBar = {
        if (addItemsToDelete.value) {
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .shadow(1.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {

                    IconButton(onClick = {
                        addItemsToDelete.value = false
                        selectAll.value=false
                        viewModel.selectedApiToDelete.clear()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                    }
                }
                Row(
                    Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(lineHeight=12.sp.toEm(),text = "SELECT ALL", modifier = Modifier.clickable {
                        selectAll.value = true
                        viewModel.selectedApiToDelete.addAll(
                            viewModel.apiList.toList().map { it.id })
                    })
                    IconButton(onClick = {

                        viewModel.deleteSelectedApi()


                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                    }
                }

            }
        } else {
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .shadow(1.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {

                    IconButton(onClick = {
                        activity.finish()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                    }
                }
                Row {

                    IconButton(onClick = {
                        viewModel.deleteAllApi()
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                    }
                }

            }
        }
    }) { padding ->
        LazyColumn(modifier = Modifier.padding(top = padding.calculateTopPadding())) {


            items(viewModel.apiList.reversed()) {
                ApiListItem(
                    data = it,
                    selectedItemsToDelete = viewModel.selectedApiToDelete,
                    addItemsToDelete = addItemsToDelete,
                    selectAll = selectAll,
                    onChecked = { check ->
                        if (check) {
                            viewModel.selectedApiToDelete.add(it.id)
                        } else {
                            viewModel.selectedApiToDelete.remove(it.id)
                        }
                    },
                    onClick = {
                        Log.d("selectedAPI", "ApiListScreen:${it} ")
                        viewModel.selectedApi.value = it
                        navController.navigate("RequestResponseScreen")
                    },
                )
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun ApiListItem(
    selectAll: MutableState<Boolean>,
    addItemsToDelete: MutableState<Boolean> = mutableStateOf(false),
    selectedItemsToDelete: SnapshotStateList<Int>,
    onLongClick: () -> Unit = {},
    onChecked: (Boolean) -> Unit = {},
    onClick: () -> Unit,
    data: ApiListDataClass ,
) {

    val selectToDelete = remember(selectedItemsToDelete, selectAll.value, data.id,addItemsToDelete.value) {
        mutableStateOf(selectedItemsToDelete.contains(data.id))
    }

    Card(
        modifier = Modifier
            .combinedClickable(
                enabled = true,
                onClickLabel = null,
                role = null,
                onLongClickLabel = null,
                onLongClick = {
                    addItemsToDelete.value = !addItemsToDelete.value
                    onLongClick()
                },
                onDoubleClick = {},
                onClick = {
                    if (addItemsToDelete.value) {
                        addItemsToDelete.value = true
                    } else {
                        onClick()
                    }
                }), elevation = CardDefaults.cardElevation(10.dp), colors = CardDefaults.cardColors(
            if (data.statusCode == "200") Color(0xFFB2F1A4) else
                Color(0xFFF1A4A4)
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(100.dp)
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = addItemsToDelete.value) {
                Checkbox(
                    checked = selectToDelete.value,
                    onCheckedChange = {
                        onChecked(it)
                        selectToDelete.value = it
                    }
                )
            }
            Column {
                Row(){
                    Column {

                        Text(lineHeight=12.sp.toEm(),
                            text = "${data.requestMethod}",
                            fontWeight = FontWeight(500),
                            fontSize = 16.sp.toEm(),
                            fontStyle = FontStyle.Italic
                        )
                        Text(lineHeight=12.sp.toEm(),
                            text = data.statusCode.toString(),
                            fontWeight = FontWeight(700),
                            fontSize = 16.sp.toEm(),
                            fontStyle = FontStyle.Normal,
                            color = if(data.statusCode.matches("200".toRegex())) Green  else{ Red}
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Text(lineHeight=18.sp.toEm(),
                            text = "${data.requestEndPoint}",
                            fontWeight = FontWeight(500),
                            fontSize = 14.sp.toEm(),
                            fontStyle = FontStyle.Italic
                        )


                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(lineHeight=12.sp.toEm(),text = "${data.callTime}", fontSize = 12.sp.toEm())
                    Text(lineHeight=12.sp.toEm(),text = "${data.startTime}",fontSize = 12.sp.toEm())
                    Text(lineHeight=12.sp.toEm(),text = "${data.responseTime}",fontSize = 12.sp.toEm())
                    Text(lineHeight=12.sp.toEm(),text = "${data.memoryConsumption} B",fontSize = 12.sp.toEm())
                }
            }

        }
    }


}