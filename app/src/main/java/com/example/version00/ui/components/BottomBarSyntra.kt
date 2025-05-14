package com.example.version00.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.version00.R
import com.example.version00.ui.theme.Indices

@Composable
fun BottomBarSyntra(selectedIndex: MutableState<Int>) {
    BottomNavigation(
        backgroundColor = Indices,
        contentColor = Color.Black
    ) {
        val iconColor = Color.Black
        val labelColor = Color.Black

        val items = listOf(
            Pair(R.drawable.ic_entrenamiento, "Entrenamiento"),
            Pair(R.drawable.ic_actividades, "Actividades"),
            Pair(R.drawable.ic_explorar, "Explorar"),
            Pair(R.drawable.ic_ejercicios, "Ejercicios"),
            Pair(R.drawable.ic_cuerpo, "Cuerpo")
        )

        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.first),
                        contentDescription = item.second,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.second,
                        fontSize = 8.sp,
                        color = labelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = selectedIndex.value == index,
                onClick = { selectedIndex.value = index },
                selectedContentColor = iconColor,
                unselectedContentColor = iconColor,
                alwaysShowLabel = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomBar() {
    BottomBarSyntra(selectedIndex = remember { mutableStateOf(0) })
}
