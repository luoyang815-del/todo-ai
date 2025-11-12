package com.aihelper.app.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider          // ✅ 只用 Glance 的 ColorProvider
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle              // ✅ 只用 Glance 的 TextStyle
import androidx.glance.unit.dp                    // ✅ dp 来自 Glance unit
import com.aihelper.app.Repo
import kotlinx.coroutines.runBlocking

class TodosWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: android.content.Context, id: GlanceId) {
        val list = runBlocking { Repo(context).top3() }
        provideContent {
            WidgetUI(list.map { "• " + (it.title.ifBlank { it.content }.take(24)) })
        }
    }
}

@Composable
fun WidgetUI(lines: List<String>) {
    Column(
        modifier = GlanceModifier
            .appWidgetBackground()
            .background(ColorProvider(Color(0x6E000000)))
            .padding(12.dp)
    ) {
        Text("AI 助手 · 代办", style = TextStyle(color = ColorProvider(Color.White)))
        Spacer(GlanceModifier.height(8.dp))
        if (lines.isEmpty()) {
            Text("暂无代办", style = TextStyle(color = ColorProvider(Color.White)))
        } else {
            lines.forEach { s ->
                Text(s, maxLines = 1, style = TextStyle(color = ColorProvider(Color.White)))
            }
        }
    }
}

class TodosWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodosWidget()
}
