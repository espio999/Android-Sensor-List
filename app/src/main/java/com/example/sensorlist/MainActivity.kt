package com.example.sensorlist

import android.content.ContentValues
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sensorlist.ui.theme.SensorListTheme
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SensorListTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        description = resources.getString(R.string.description),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        val myCSV = makeCSVdata(sensors)
        saveCSVtoDownload(myCSV)
        Log.v("Android Sensor List", myCSV)
    }

    private fun saveCSVtoDownload(csvData: String){
        val fileName = "sensor list - ${Build.MODEL}.csv"
        val mimeType = "text/csv"

        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)

        val fileUri = this.contentResolver.insert(MediaStore.Files.getContentUri("external"), values) ?: return
        val outputStream: OutputStream? = this.contentResolver.openOutputStream(fileUri)

        /*
                val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues()
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, FILE_NAME)
                    values.put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE)
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)

                    val fileUri = this.contentResolver.insert(MediaStore.Files.getContentUri("external"), values) ?: return
                    this.contentResolver.openOutputStream(fileUri)
                } else {
                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), FILE_NAME)
                    FileOutputStream(file)
                }
         */

        outputStream?.write(csvData.toByteArray())
        outputStream?.close()
    }

    private fun makeCSVdata(sensors: List<Sensor>): String{
        val myCSV = StringBuilder()

        //CSV header
        val csvHeader = resources.getStringArray(R.array.header_item)
        myCSV.append(makeCSVfromList(csvHeader.asList()))

        //CSV body
        sensors.forEach { sensor ->
            val csvBody : List<String> = listOf(
                sensor.name,
                sensor.stringType,
                sensor.vendor,
                sensor.version.toString(),
                sensor.id.toString(),
                sensor.power.toString(),
                sensor.resolution.toString(),
                sensor.highestDirectReportRateLevel.toString(),
                sensor.reportingMode.toString(),
                sensor.maximumRange.toString(),
                sensor.minDelay.toString(),
                sensor.maxDelay.toString(),
                sensor.fifoReservedEventCount.toString(),
                sensor.fifoMaxEventCount.toString(),
                sensor.isAdditionalInfoSupported.toString(),
                sensor.isDirectChannelTypeSupported(0).toString(),
                sensor.isDynamicSensor.toString(),
                sensor.isWakeUpSensor.toString()
            )

            myCSV.append(makeCSVfromList(csvBody))
        }

        return myCSV.toString()
    }

    private fun makeCSVfromList(items: List<String>): String {
        val splitter = '\t'
        val crlf = '\n'
        val ret = StringBuilder()

        for (item in items) {
            ret.append("${item}${splitter}")
        }

        return ret.substring(0, ret.length - 1) + crlf
    }
}

@Composable
fun Greeting(description: String, modifier: Modifier = Modifier) {
    Text(
        text = description,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SensorListTheme {
        Greeting("Android")
    }
}
