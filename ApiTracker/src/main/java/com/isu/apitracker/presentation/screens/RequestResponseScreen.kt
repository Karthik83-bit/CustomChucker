package com.isu.apitracker.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.isu.apitracker.presentation.ApiTrackerViewModel
import com.isu.apitracker.R
import com.isu.apitracker.presentation.screens.TabConstants.tabs
import com.isu.apitracker.toEm

object TabConstants {
    private const val REQUEST_TAB = "Request"
    private const val RESPONSE_TAB = "Response"
    val tabs = listOf(REQUEST_TAB, RESPONSE_TAB)
}

/**
 * Composable function to display the Request/Response screen.
 *
 * @param navController Navigation controller to manage navigation.
 * @param viewModel ViewModel to provide data and handle logic.
 */
@Composable
fun RequestResponseScreen(navController: NavHostController, viewModel: ApiTrackerViewModel) {
    val selectedTabIndex = remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color.LightGray.copy(0.5f),
        topBar = {
            Row(
                Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                tabs.forEachIndexed { index, tab ->
                    Column(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null
                                ) {
                                    selectedTabIndex.value = index
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lineHeight=12.sp.toEm(),text = tab)
                        }
                        AnimatedVisibility(
                            visible = selectedTabIndex.value == index,
                            enter = scaleIn(),
                            exit = scaleOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .height(5.dp)
                                    .fillMaxWidth()
                                    .background(Color.Gray)
                            ){

                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        val copyManager = LocalClipboardManager.current
        val apiData = viewModel.selectedApi.value

        AnimatedVisibility(
            visible = selectedTabIndex.value == 0,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }
        ) {
            RequestContent(innerPadding, apiData, copyManager)
        }

        AnimatedVisibility(
            visible = selectedTabIndex.value == 1,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            ResponseContent(innerPadding, apiData, copyManager)
        }
    }
}

/**
 * Composable function to display the Request content.
 *
 * @param innerPadding Padding values from the Scaffold.
 * @param apiData Data to display.
 * @param copyManager Clipboard manager to handle copying text.
 */
@Composable
fun RequestContent(innerPadding: PaddingValues, apiData: ApiListDataClass?, copyManager: ClipboardManager) {
    Column(
        modifier = Modifier
            .padding(
                start = 16.dp,
                top = innerPadding.calculateTopPadding(),
                end = 16.dp
            )
            .verticalScroll(rememberScrollState())
    ) {
        Column(Modifier.padding(top = 16.dp)) {
            Text(lineHeight=12.sp.toEm(),text = "Headers", fontWeight = FontWeight.Bold, fontSize = 34.sp.toEm(), textDecoration = TextDecoration.Underline)
            apiData?.requestHeaders?.forEach {
                SelectionContainer {
                    Row(modifier = Modifier.fillMaxWidth()){
                        Row(modifier = Modifier.weight(1f)){
                            Text(lineHeight=12.sp.toEm(),
                                text = it.key+":",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(modifier = Modifier.weight(1f)){
                            Text(lineHeight=12.sp.toEm(),
                                text = processedRequest(it.value).replace("[","").replace("]","")
                            )
                        }
                    }

                }
            }

        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(lineHeight=12.sp.toEm(),text = "Request:", fontWeight = FontWeight.Bold, fontSize = 34.sp.toEm(), textDecoration = TextDecoration.Underline)
        RequestSection(apiData?.request, copyManager)

        Spacer(modifier = Modifier.height(10.dp))
        apiData?.decodedRequest?.forEachIndexed { index, decodedRequest ->
            Text(lineHeight=12.sp.toEm(),text = "DecodedRequest:${index + 1}", fontWeight = FontWeight.Bold)
            RequestSection(decodedRequest, copyManager)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Composable function to display a section of the request content.
 *
 * @param requestText Text to display.
 * @param copyManager Clipboard manager to handle copying text.
 */
@Composable
fun RequestSection(requestText: String?, copyManager: ClipboardManager) {
    if (!requestText.isNullOrEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color(0xF0141414))
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        if (requestText.isNotEmpty()) {
                            copyManager.setText(AnnotatedString(requestText))
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_content_copy_24),
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            }
            Text(lineHeight=12.sp.toEm(),text = processedRequest(requestText) ?: "No response found", color = Color.White)
        }
    }
}

/**
 * Composable function to display the Response content.
 *
 * @param innerPadding Padding values from the Scaffold.
 * @param apiData Data to display.
 * @param copyManager Clipboard manager to handle copying text.
 */
@Composable
fun ResponseContent(innerPadding: PaddingValues, apiData: ApiListDataClass?, copyManager: ClipboardManager) {
    Column(
        modifier = Modifier
            .padding(
                start = 16.dp,
                top = innerPadding.calculateTopPadding(),
                end = 16.dp
            )
            .verticalScroll(rememberScrollState())
    ) {
        Column(Modifier.padding(top = 16.dp)) {
            Text(lineHeight=12.sp.toEm(),text = "Headers", fontWeight = FontWeight.Bold, fontSize = 34.sp.toEm(), textDecoration = TextDecoration.Underline)
            apiData?.responseHeaders?.forEach {
                SelectionContainer {
                    Row(modifier = Modifier.fillMaxWidth()){
                        Row(modifier = Modifier.weight(1f)){
                            Text(lineHeight=12.sp.toEm(),
                                text = it.key+":",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(modifier = Modifier.weight(1f)){
                            Text(lineHeight=12.sp.toEm(),
                                text = processedRequest(it.value).replace("[","").replace("]","")
                            )
                        }
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(lineHeight=12.sp.toEm(),text = "Response:", fontWeight = FontWeight.Bold, fontSize = 34.sp.toEm(), textDecoration = TextDecoration.Underline)
        ResponseSection(apiData?.response, copyManager)

        Spacer(modifier = Modifier.height(10.dp))
        apiData?.decodedOutput?.forEachIndexed { index, decodedOutput ->
            Text(lineHeight=12.sp.toEm(),text = "DecodedResponse: ${index + 1}", fontWeight = FontWeight.Bold)
            ResponseSection(decodedOutput, copyManager)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Composable function to display a section of the response content.
 *
 * @param responseText Text to display.
 * @param copyManager Clipboard manager to handle copying text.
 */
@Composable
fun ResponseSection(responseText: String?, copyManager: ClipboardManager) {
    if (!responseText.isNullOrEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color(0xF0141414))
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        if (responseText.isNotEmpty()) {
                            copyManager.setText(AnnotatedString(responseText))
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_content_copy_24),
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            }
            Text(lineHeight=12.sp.toEm(),text = processedRequest(responseText) ?: "No response found", color = Color.White)
        }
    }
}

/**
 * Processes a given request string to remove the first and last quotes if present.
 *
 * @param request The request string to process.
 * @return The processed request string.
 */
fun processedRequest(request: String): String{
    return request.replaceFirst("\"", "").reversed().replaceFirst("\"", "").reversed()
}
