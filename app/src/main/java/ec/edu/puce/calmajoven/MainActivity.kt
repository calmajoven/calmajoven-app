package ec.edu.puce.calmajoven

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ec.edu.puce.calmajoven.ui.navigation.CalmaJovenNavGraph
import ec.edu.puce.calmajoven.ui.theme.CalmaJovenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalmaJovenTheme {
                CalmaJovenNavGraph()
            }
        }
    }
}
