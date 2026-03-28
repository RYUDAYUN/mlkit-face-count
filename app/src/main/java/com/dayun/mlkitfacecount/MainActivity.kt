package com.dayun.mlkitfacecount

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dayun.mlkitfacecount.ui.theme.MLKitFaceCountTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MLKitFaceCountTheme {
                FaceDetectionScreen()
            }
        }
    }
}
