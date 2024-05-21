package com.example.geminichat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BakingScreen(
    bakingViewModel: BakingViewModel = viewModel()
) {
    val messages by bakingViewModel.messages.collectAsState()
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val placeholderResult = stringResource(R.string.results_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by bakingViewModel.uiState.collectAsState()
//    val listState = rememberLazyListState()
    
    
    Scaffold(
        topBar = {
            TopAppBar(

                modifier = Modifier.background(Color.Green).background(MaterialTheme.colorScheme.secondary),
                title = { Text(text = "Chat with Gemini") },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(5.dp)
        ) {

            Column(modifier = Modifier.fillMaxSize().weight(0.85f)) {

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(messages) { message ->
                        if(message.isFromUser){
//                            Text(text = "User", modifier = Modifier.padding(8.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .align(Alignment.End),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(
                                    MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(text = message.content, modifier = Modifier.padding(8.dp))
                            }
                        } else {
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(8.dp)
//                                    .align(Alignment.Start),
//                                shape = RoundedCornerShape(8.dp),
//                                elevation = CardDefaults.cardElevation(4.dp),
//                                colors = CardDefaults.cardColors(
//                                    MaterialTheme.colorScheme.secondary
//                                )
//                            ) {
                                Text(text = message.content, modifier = Modifier.padding(8.dp))
//                            }
                        }
                    }
                }

                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if(uiState is UiState.Initial){
                    Text(text = result, modifier = Modifier.padding(8.dp))
                }

            }

            Row(modifier = Modifier.padding(16.dp).fillMaxWidth().weight(0.15f)) {
                var newMessage by remember { mutableStateOf("") }
                var newImage by remember { mutableStateOf<Bitmap?>(null) }

                IconButton (
                    onClick = {
                      bakingViewModel.reset()
                        newMessage = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat"
                    )
                }
                TextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Button(
                    onClick = {
                        bakingViewModel.sendPrompt(newMessage)
                        newMessage = ""
                    },
                    enabled = newMessage.isNotBlank() || newImage != null
                ) {
                    Text(text = "Send")
                }
            }


        }
    }


}