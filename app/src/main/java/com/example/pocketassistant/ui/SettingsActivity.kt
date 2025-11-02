
package com.example.pocketassistant.ui
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pocketassistant.net.OpenAiService
import kotlinx.coroutines.launch
class SettingsActivity: ComponentActivity(){
  override fun onCreate(savedInstanceState: Bundle?){ super.onCreate(savedInstanceState); setContent{ MaterialTheme{ SettingsScreen() } } }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: SettingsViewModel = viewModel()){
  val ctx = LocalContext.current
  val scope = rememberCoroutineScope()
  val useGateway by vm.useGatewayKey.collectAsState(initial=true)
  val apiKey by vm.apiKey.collectAsState(initial="")
  val baseUrl by vm.baseUrl.collectAsState(initial="https://api.openai.com/")
  val model by vm.model.collectAsState(initial="gpt-4.1-mini")
  val proxyType by vm.proxyType.collectAsState(initial="NONE")
  val proxyHost by vm.proxyHost.collectAsState(initial="")
  val proxyPort by vm.proxyPort.collectAsState(initial=0)
  val proxyUser by vm.proxyUser.collectAsState(initial="")
  val proxyPass by vm.proxyPass.collectAsState(initial="")
  var useGatewayLocal by remember{ mutableStateOf(useGateway) }
  var apiKeyLocal by remember{ mutableStateOf(apiKey) }
  var baseUrlLocal by remember{ mutableStateOf(baseUrl) }
  var modelLocal by remember{ mutableStateOf(model) }
  var proxyTypeLocal by remember{ mutableStateOf(proxyType) }
  var proxyHostLocal by remember{ mutableStateOf(proxyHost) }
  var proxyPortLocal by remember{ mutableStateOf(if(proxyPort==0) "" else proxyPort.toString()) }
  var proxyUserLocal by remember{ mutableStateOf(proxyUser) }
  var proxyPassLocal by remember{ mutableStateOf(proxyPass) }
  var modelExpanded by remember{ mutableStateOf(false) }
  val models = listOf("gpt-4.1-mini","gpt-4o-mini","o3-mini","gpt-4.1")
  Scaffold(topBar={ TopAppBar(title={ Text("设置") }) }){ padding->
    Column(Modifier.padding(padding).padding(16.dp)){
      Text("GPT / 网关", style=MaterialTheme.typography.titleMedium)
      Row{ Checkbox(checked=useGatewayLocal, onCheckedChange={useGatewayLocal=it}); Spacer(Modifier.width(8.dp)); Text("使用网关中的 API Key") }
      OutlinedTextField(apiKeyLocal,{apiKeyLocal=it}, enabled=!useGatewayLocal, label={Text("OpenAI API Key（未走网关时填写）")}, modifier=Modifier.fillMaxWidth(), singleLine=true)
      OutlinedTextField(baseUrlLocal,{baseUrlLocal=it}, label={Text("Base URL（可填网关）")}, modifier=Modifier.fillMaxWidth(), singleLine=true)
      Spacer(Modifier.height(12.dp))
      Text("模型选择", style=MaterialTheme.typography.titleMedium)
      ExposedDropdownMenuBox(expanded=modelExpanded, onExpandedChange={modelExpanded=!modelExpanded}){
        OutlinedTextField(value=modelLocal, onValueChange={}, readOnly=true, label={Text("选择模型")}, modifier=Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(expanded=modelExpanded, onDismissRequest={modelExpanded=false}){
          models.forEach{ m -> DropdownMenuItem(text={Text(m)}, onClick={ modelLocal=m; modelExpanded=false }) }
        }
      }
      Spacer(Modifier.height(12.dp))
      Text("代理/网关", style=MaterialTheme.typography.titleMedium)
      Row(Modifier.fillMaxWidth()){
        Column(Modifier.weight(1f)){
          OutlinedTextField(proxyTypeLocal,{proxyTypeLocal=it}, label={Text("类型：NONE/HTTP/HTTPS/SOCKS5")}, singleLine=true)
          Spacer(Modifier.height(8.dp))
          OutlinedTextField(proxyHostLocal,{proxyHostLocal=it}, label={Text("Host")}, singleLine=true)
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)){
          OutlinedTextField(proxyPortLocal,{proxyPortLocal=it}, label={Text("Port")}, singleLine=true, keyboardOptions=androidx.compose.ui.text.input.KeyboardOptions(keyboardType=KeyboardType.Number))
          Spacer(Modifier.height(8.dp))
          OutlinedTextField(proxyUserLocal,{proxyUserLocal=it}, label={Text("用户名")}, singleLine=true)
          Spacer(Modifier.height(8.dp))
          OutlinedTextField(proxyPassLocal,{proxyPassLocal=it}, label={Text("密码")}, singleLine=true)
        }
      }
      Spacer(Modifier.height(12.dp))
      Row{
        Button(onClick={ vm.save(useGatewayLocal,apiKeyLocal,baseUrlLocal,modelLocal,proxyTypeLocal,proxyHostLocal,proxyPortLocal.toIntOrNull()?:0,proxyUserLocal,proxyPassLocal); Toast.makeText(ctx,"已保存",Toast.LENGTH_SHORT).show() }){ Text("保存") }
        Spacer(Modifier.width(12.dp))
        Button(onClick={ scope.launch{ val (ok,msg)=OpenAiService(ctx.applicationContext as android.app.Application).testConnectivity(); Toast.makeText(ctx,(if(ok)"连通正常：" else "连通失败：")+msg,Toast.LENGTH_LONG).show() } }){ Text("测试连通性") }
      }
    }
  }
}
