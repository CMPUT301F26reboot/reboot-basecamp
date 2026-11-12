package com.example.campsitereservationapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Reusable number input with minus and plus buttons, and a numpad when the number is tapped
 * @param modifier modifier from parent compose
 * @param title optional title string
 * @param subHeader optional subheader string
 * @param num the number to display
 * @param onNumChange called with the new number when a button is pressed or a number is typed
 */
@Composable
fun IntInputScreenComponent(
    modifier: Modifier = Modifier,
    title: String? = null,
    subHeader: String? = null,
    num: Int = 0,
    onNumChange: (Int) -> Unit = {},
) {
    // Shared so the title, subheader and number are always the same size.
    val textStyle = MaterialTheme.typography.titleMedium
    val buttonColors = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
    val focusManager = LocalFocusManager.current

    // Kept apart from num so the field can be empty while typing.
    var typedText by rememberSaveable { mutableStateOf(num.toString()) }
    val fieldText = if ((typedText.toIntOrNull() ?: 0) == num) typedText else num.toString()

    val changeNum = { newNum: Int ->
        typedText = newNum.toString()
        onNumChange(newNum)
    }

    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (title != null) {
                    Text(
                        text = title,
                        style = textStyle,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (subHeader != null) {
                    Text(
                        text = subHeader,
                        style = textStyle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = { changeNum(num - 1) },
                        enabled = num > 0,
                        colors = buttonColors,
                        modifier = Modifier
                            .size(36.dp)
                    ) {
                        Text("−", fontWeight = FontWeight.Bold)
                    }

                    BasicTextField(
                        value = fieldText,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }.take(9)
                            typedText = if (digits.isEmpty()) "" else digits.toInt().toString()
                            onNumChange(typedText.toIntOrNull() ?: 0)
                        },
                        textStyle = textStyle.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .width(48.dp)
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused && typedText.isEmpty()) {
                                    typedText = "0"
                                }
                            }
                    )

                    FilledTonalIconButton(
                        onClick = { changeNum(num + 1) },
                        colors = buttonColors,
                        modifier = Modifier
                            .size(36.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntInputComponentExample() {
    var num by remember { mutableIntStateOf(2) }

    IntInputScreenComponent(
        title = "title",
        subHeader = "subHeader",
        num = num,
        onNumChange = { num = it }
    )
}