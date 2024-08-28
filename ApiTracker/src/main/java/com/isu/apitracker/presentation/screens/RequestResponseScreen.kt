package com.isu.apitracker.presentation.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.isu.apitracker.R
import com.isu.apitracker.presentation.screens.TabConstants.tabs
import com.isu.apitracker.presentation.viewmodel.ApiTrackerViewModel
import com.isu.apitracker.util.toEm
import com.isu.apitracker.util.beautifyJson

object TabConstants {

    private const val REQUEST_TAB = "Request"
    private const val OVERVIEW_TAB = "Overview"
    private const val RESPONSE_TAB = "Response"
    val tabs = listOf(OVERVIEW_TAB,REQUEST_TAB, RESPONSE_TAB)
}

/**
 * Composable function to display the Request/Response screen.
 *
 * @param navController Navigation controller to manage navigation.
 * @param viewModel ViewModel to provide data and handle logic.
 */
fun shareText(context: Context, text: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(intent, "Sharing API Info")
    context.startActivity(shareIntent)
}

@Composable
fun RequestResponseScreen(navController: NavHostController, viewModel: ApiTrackerViewModel) {
    val selectedTabIndex = remember { mutableStateOf(0) }
    val context = LocalContext.current



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
                                    interactionSource = remember {
                                        MutableInteractionSource ()
                                    },

                                    indication = null
                                ) {
                                    selectedTabIndex.value = index
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lineHeight = 12.sp.toEm(), text = tab)
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
                            ) {

                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val text = createText(viewModel.selectedApi.value)
                shareText(context, text = text)
            }) {
                Icon(Icons.Default.Share, contentDescription = "")
            }
        }
    ) { innerPadding ->
        val copyManager = LocalClipboardManager.current
        val apiData = viewModel.selectedApi.value

        AnimatedVisibility(
            visible = selectedTabIndex.value == 1,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }
        ) {
            RequestContent(innerPadding, apiData, copyManager)
        }

        AnimatedVisibility(
            visible = selectedTabIndex.value == 2,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            ResponseContent(innerPadding, apiData, copyManager)
        }
        AnimatedVisibility(
            visible = selectedTabIndex.value == 0,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            OverviewContent(innerPadding, apiData)
        }
    }
}

@Composable
fun OverviewContent(innerPadding: PaddingValues, apiData: ApiListDataClass?) {
    Column(
        modifier = Modifier
            .padding(
                start = 16.dp,
                top = innerPadding.calculateTopPadding(),
                end = 16.dp
            )
            .verticalScroll(rememberScrollState())
    ) {
        val url=apiData?.requestBaseURL
        val method=apiData?.requestMethod
        val statusCode=apiData?.statusCode
        val callTime=apiData?.callTime
        val responseTime=apiData?.startTime

        Column(Modifier.padding(top = 16.dp)) {


                SelectionContainer {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.weight(1f)) {
                            Text(
                                lineHeight = 12.sp.toEm(),
                                text = "URL" + " :",
                                fontWeight = FontWeight.Bold, fontSize = 13.sp.toEm()
                            )
                        }
                        Row(modifier = Modifier.weight(2f)) {
                            Text(
                                lineHeight = 22.sp.toEm(),
                                text = apiData?.requestBaseURL?:"",
                                fontSize = 13.sp.toEm()
                            )
                        }
                    }


            }
            SelectionContainer {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.weight(1f)) {
                            Text(
                                lineHeight = 12.sp.toEm(),
                                text = "Method" + " :",
                                fontWeight = FontWeight.Bold, fontSize = 13.sp.toEm()
                            )
                        }
                        Row(modifier = Modifier.weight(2f)) {
                            Text(
                                lineHeight = 22.sp.toEm(),
                                text = method?:"",
                                fontSize = 13.sp.toEm()
                            )
                        }
                    }


            }
            SelectionContainer {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.weight(1f)) {
                            Text(
                                lineHeight = 12.sp.toEm(),
                                text = "Status Code" + " :",
                                fontWeight = FontWeight.Bold, fontSize = 13.sp.toEm()
                            )
                        }
                        Row(modifier = Modifier.weight(2f)) {
                            Text(
                                lineHeight = 22.sp.toEm(),
                                text = statusCode?:"",
                                fontSize = 13.sp.toEm()
                            )
                        }
                    }


            }
            SelectionContainer {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.weight(1f)) {
                            Text(
                                lineHeight = 12.sp.toEm(),
                                text = "Date" + " :",
                                fontWeight = FontWeight.Bold, fontSize = 13.sp.toEm()
                            )
                        }
                        Row(modifier = Modifier.weight(2f)) {
                            Text(
                                lineHeight = 22.sp.toEm(),
                                text = responseTime?:"",
                                fontSize = 13.sp.toEm()
                            )
                        }
                    }


            }
            SelectionContainer {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.weight(1f)) {
                            Text(
                                lineHeight = 12.sp.toEm(),
                                text = "Time" + " :",
                                fontWeight = FontWeight.Bold, fontSize = 13.sp.toEm()
                            )
                        }
                        Row(modifier = Modifier.weight(2f)) {
                            Text(
                                lineHeight = 22.sp.toEm(),
                                text = callTime?:"",
                                fontSize = 13.sp.toEm()
                            )
                        }
                    }


            }
        }

        Spacer(modifier = Modifier.height(20.dp))

    }
}

fun createText(value: ApiListDataClass?): String {
    return "API INFO:\n" +
            "___________________________________\n" +
            "url : ${value?.requestBaseURL}\n" +
            "Method : ${value?.requestMethod}\n" +
            "StatusCode : ${value?.statusCode}\n\n" +


            "REQUEST\n" +

            "___________________________\n" +
            "Headers :-\n" +
            "____________\n" +
            getHeaderAsString(value?.requestHeaders ?: emptyMap()) + "\n" +

            "RequestBody :\n" +
            "____________\n" +
            "${processedRequest(value?.request ?: "")}\n\n" +
            if (value?.decodedRequest?.isNotEmpty() == true) {
                "DecodedRequestBody :\n" +
                        "____________\n" +
                        "${value.decodedRequest.reduce { acc, s -> acc + s + "\n" }}\n\n"
            } else {
                ""
            } +


            "---------------------------------------------------------------------------\n" +
            "RESPONSE\n" +
            "___________________________\n" +
            "Headers :-\n" +
            "____________\n" +
            getHeaderAsString(value?.responseHeaders ?: emptyMap()) + "\n" +
            "ResponseBody :\n" +
            "____________\n" +
            "${processedRequest(value?.response ?: "")}\n\n" +
            if (value?.decodedOutput?.isNotEmpty() == true) {
                "DecodedResponseBody :\n" +
                        "____________\n" +
                        "${value.decodedOutput.reduce { acc, s -> acc + s + "\n" }}\n\n"
            } else {
                ""
            }
}

fun getHeaderAsString(requestHeaders: Map<String, String>): String {
    var header = ""
    requestHeaders.forEach {
        header += "${it.key} : ${it.value}\n"
    }
    return header
}

/**
 * Composable function to display the Request content.
 *
 * @param innerPadding Padding values from the Scaffold.
 * @param apiData Data to display.
 * @param copyManager Clipboard manager to handle copying text.
 */
@Composable
fun RequestContent(
    innerPadding: PaddingValues,
    apiData: ApiListDataClass?,
    copyManager: ClipboardManager,
) {
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

            Text(
                lineHeight = 12.sp.toEm(),
                text = "Headers",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp.toEm(),
                textDecoration = TextDecoration.Underline
            )
            Spacer(modifier = Modifier.height(10.dp))
            apiData?.requestHeaders?.forEach {
                SelectionContainer {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.weight(1f)) {
                            Text(
                                lineHeight = 13.sp.toEm(),
                                text = it.key + " :",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(modifier = Modifier.weight(2f)) {
                            Text(
                                lineHeight = 22.sp.toEm(),
                                text = it.value.replace("[", "").replace("]", ""),
                                fontSize = 13.sp.toEm()
                            )
                        }
                    }

                }
            }

        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            lineHeight = 12.sp.toEm(),
            text = "Request:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp.toEm(),
            textDecoration = TextDecoration.Underline
        )
        RequestSection(apiData?.request, copyManager)

        Spacer(modifier = Modifier.height(10.dp))
        apiData?.decodedRequest?.forEachIndexed { index, decodedRequest ->
            Text(
                lineHeight = 22.sp.toEm(),
                text = "DecodedRequest:${index + 1}",
                fontWeight = FontWeight.Bold, fontSize = 13.sp
            )
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
            Text(
                lineHeight = 22.sp.toEm(),
                text = processedRequest(requestText),
                color = Color.White,
                fontSize = 13.sp.toEm()
            )
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
fun ResponseContent(
    innerPadding: PaddingValues,
    apiData: ApiListDataClass?,
    copyManager: ClipboardManager,
) {
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
            Text(
                lineHeight = 22.sp.toEm(),
                text = "Headers",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp.toEm(),
                textDecoration = TextDecoration.Underline
            )
            Spacer(modifier = Modifier.height(10.dp))
            apiData?.responseHeaders?.forEach {
                SelectionContainer {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.weight(1f)) {
                            Text(
                                lineHeight = 12.sp.toEm(),
                                text = it.key + " :",
                                fontWeight = FontWeight.Bold, fontSize = 13.sp.toEm()
                            )
                        }
                        Row(modifier = Modifier.weight(2f)) {
                            Text(
                                lineHeight = 22.sp.toEm(),
                                text = it.value.replace("[", "").replace("]", ""),
                                fontSize = 13.sp.toEm()
                            )
                        }
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            lineHeight = 22.sp.toEm(),
            text = "Response:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp.toEm(),
            textDecoration = TextDecoration.Underline
        )
        ResponseSection(apiData?.response, copyManager)

        Spacer(modifier = Modifier.height(10.dp))
        apiData?.decodedOutput?.forEachIndexed { index, decodedOutput ->
            Text(
                lineHeight = 12.sp.toEm(),
                text = "DecodedResponse: ${index + 1}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp.toEm()
            )
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
            Text(
                lineHeight = 22.sp.toEm(),
                text = processedRequest(responseText),
                color = Color.White,
                fontSize = 13.sp.toEm()
            )
        }
    }
}

/**
 * Processes a given request string to remove the first and last quotes if present.
 *
 * @param request The request string to process.
 * @return The processed request string.
 */
fun processedRequest(request: String): String {

    return beautifyJson(request) ?: ""
//    return request.replaceFirst("\"", "").reversed().replaceFirst("\"", "").reversed()
}
