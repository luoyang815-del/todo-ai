
package com.example.pocketassistant.ui
import android.app.Application
import androidx.lifecycle.*
import com.example.pocketassistant.App
import com.example.pocketassistant.data.Entry
import com.example.pocketassistant.data.Event
import com.example.pocketassistant.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class ChatViewModel(private val app: Application): AndroidViewModel(app){
  private val db=(app as App).db
  private val _messages = MutableStateFlow(listOf<Message>()); val messages = _messages.asStateFlow()
  fun send(text:String){ viewModelScope.launch{
    _messages.value = _messages.value + Message("user", text)
    val entry = Entry(rawText=text, hasAiParsed=true, source="chat"); db.entryDao().insert(entry)
    val event = com.example.pocketassistant.data.Event(entryId=entry.id, title=text.take(30), description="（演示）已整理为待办", priority=1)
    db.eventDao().insert(event)
    _messages.value = _messages.value + Message("assistant","已整理为待办。",true, description=event.description)
  } }
  companion object{ fun provideFactory(app: Application)=object: ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass: Class<T>): T { @Suppress("UNCHECKED_CAST") return ChatViewModel(app) as T }
  }}
}
