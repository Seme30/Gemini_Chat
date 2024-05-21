package com.example.geminichat

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BakingViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()


    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages


    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.apiKey
    )

    private var chat = generativeModel.startChat()


    fun reset(){
        _uiState.value = UiState.Initial
       _messages.value = listOf()
    }

    fun sendPrompt(
        prompt: String
    ) {
        _uiState.value = UiState.Loading
        val newMessage = Message(prompt)
        _messages.value += newMessage

        viewModelScope.launch(Dispatchers.IO) {
            try {

                chat.sendMessageStream(content {
                    text(prompt)
                }).collect { chunk ->
                    _messages.value += Message(chunk.text?:"", isFromUser = false)
                    _uiState.value = UiState.Success(chunk.text?:"")
                }
//                val response = generativeModel.generateContent(
//                    content {
//                        text(prompt)
//                    }
//                )
//                response.text?.let { outputContent ->
//                    _uiState.value = UiState.Success(outputContent)
//                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}