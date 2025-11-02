
package com.example.pocketassistant.ui
import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.example.pocketassistant.App
import com.example.pocketassistant.data.Entry
import com.example.pocketassistant.data.Event
import com.example.pocketassistant.net.OpenAiService
import com.example.pocketassistant.notify.ReminderWorker
import com.example.pocketassistant.util.CalendarUtil
import com.example.pocketassistant.widget.TodoWidgetProvider
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch
class MainViewModel(private val app: Application): AndroidViewModel(app){
  private val db = (app as App).db
  val entries = db.entryDao().latest()
  val events = db.eventDao().upcoming()
  fun addQuick(text:String){ if(text.isBlank()) return; viewModelScope.launch{ db.entryDao().insert(Entry(rawText=text)) } }
  fun addQuickAndParse(text:String){
    if(text.isBlank()) return
    viewModelScope.launch{
      val entry = Entry(rawText=text, hasAiParsed=true, source="chat"); db.entryDao().insert(entry)
      val parsed = OpenAiService(app).parseTodo(text); val now = System.currentTimeMillis()
      val startAt = now + parsed.minutesFromNow*60*1000L
      val event = Event(entryId=entry.id, title=parsed.title, description= if(parsed.note.isBlank()) "已通过 GPT 智能整理" else parsed.note,
        startTime= startAt, remindAt= startAt, priority=parsed.priority)
      db.eventDao().insert(event)
      CalendarUtil.tryInsert(app, event.title, event.description, event.startTime)
      val delay = (event.remindAt ?: startAt) - System.currentTimeMillis()
      if (delay > 0) {
        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
          .setInitialDelay(delay, TimeUnit.MILLISECONDS)
          .setInputData(workDataOf("eventId" to event.eventId))
          .build()
        WorkManager.getInstance(app).enqueue(work)
      }
      TodoWidgetProvider.requestUpdate(app)
    }
  }
  companion object{ fun provideFactory(app: Application)=object: ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass: Class<T>): T { @Suppress("UNCHECKED_CAST") return MainViewModel(app) as T }
  }}
}
