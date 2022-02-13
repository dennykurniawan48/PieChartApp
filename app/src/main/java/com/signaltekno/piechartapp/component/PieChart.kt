package com.signaltekno.piechartapp.component

import android.widget.Toast
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun PieChart(scaffoldState: ScaffoldState) {
    val coroutineScope = rememberCoroutineScope()
    val points = listOf(10f, 40f, 25f, 100f, 65f)
    val colors = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow,
        Color.Magenta
    )

    val sum = points.sum()
    var startAngle = 0f
    val radius = 300f // the size of the pie chart
    val rect = Rect(Offset(-radius, -radius), Size(2*radius, 2*radius))
    val path = Path()
    val angles = mutableListOf<Float>()
    var start by remember{ mutableStateOf(false) }
    val sweepPre by animateFloatAsState(
        targetValue = if (start) 1f else 0f,
        animationSpec = FloatTweenSpec(duration = 1000)
    )

    Canvas(modifier = Modifier
        .fillMaxWidth(0.8f)
        .height(350.dp)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    val x = it.x - radius
                    val y = it.y - radius
                    var touchAngle = Math.toDegrees(Math.atan2(y.toDouble(), x.toDouble()))
                    if (x<0&&y < 0 || x>0 && y <0) {
                        touchAngle += 360
                    }
                    val position = getPositionFromAngle(touchAngle, angles)
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("OnTap: $position")
                    }

                }
            )
        }){
            translate(radius, radius){
                start = true
                for((i, p) in points.withIndex()) {
                    val sweepAngle = p / sum * 360f
                    path.moveTo(0f, 0f)
                    path.arcTo(rect = rect, startAngle, sweepPre* sweepAngle, false)
                    angles.add(sweepAngle)
                    drawPath(path, color = colors[i])
                    path.reset()
                    startAngle+=sweepAngle
                }
            }
    }

}

fun getPositionFromAngle(touchAngle: Double, angles: List<Float>): Int{
    var totalAngle = 0f
    for((i, angle) in angles.withIndex()){
        totalAngle += angle
        if(touchAngle <= totalAngle){
            return i
        }
    }
    return -1
}

