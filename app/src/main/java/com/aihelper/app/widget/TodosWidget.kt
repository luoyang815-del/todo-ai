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
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle               // ✅ 只用 Glance 的 TextStyle
import androidx.glance.color.ColorProvider        // ✅ ColorProvider（日/夜两套）
import androidx.glance.unit.dp                    // ✅ dp 正确来源
import com.aihelper.app.Repo
import kotlinx.coroutines.runBlocking

class TodosWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: android.content.Context,
        id: GlanceId
    ) {
        val list = runBlocking { Repo(context).top3() }
        provideContent {
            WidgetUI(
                lines = list.map { "• " + (it.title.ifBlank { it.content }.take(24)) }
            )
        }
    }
}

@Composable
fun WidgetUI(lines: List<String>) {
    val bg = ColorProvider(
        day = Color(0x6E000000),   // 半透明黑，日间
        night = Color(0x6E000000)  // 夜间同色（你也可换成更深/更浅）
    )
    val fg = ColorProvider(
        day = Color.White,
        night = Color.White
    )

    Column(
        modifier = GlanceModifier
            .appWidgetBackground()
            .background(bg)        // ✅ 传 ColorProvider(day, night)
            .padding(12.dp)
    ) {
        Text(
            text = "AI 助手 · 代办",
            style = TextStyle(color = fg)   // ✅ TextStyle.color 用 ColorProvider
        )
        Spacer(GlanceModifier.height(8.dp))

        if (lines.isEmpty()) {
            Text(
                text = "暂无代办",
                style = TextStyle(color = fg)
            )
        } else {
            lines.forEach { s ->
                Text(
                    text = s,
                    maxLines = 1,
                    style = TextStyle(color = fg)
                )
            }
        }
    }
}

class TodosWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodosWidget()
}
