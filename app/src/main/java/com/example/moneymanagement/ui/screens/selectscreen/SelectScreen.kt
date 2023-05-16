package com.example.moneymanagement.ui.screens.selectscreen

import android.content.Context
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymanagement.R
import com.example.moneymanagement.data.PeriodItem
import com.example.moneymanagement.ui.screens.mainscreen.MainScreenUIState
import com.example.moneymanagement.ui.screens.mainscreen.MainViewModel
import com.example.moneymanagement.ui.theme.MainBackground
import com.example.moneymanagement.ui.theme.SelectBackground
import com.example.moneymanagement.ui.theme.SelectTopBarBackground
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectScreen(
    selectViewModel: SelectViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel(),
    context: Context = LocalContext.current,
    onPeriodSelected: (String, String) -> Unit
) {

    val listPeriodState =
        selectViewModel.periodsList()
            .collectAsState(initial = SelectScreenUIState.Init)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        if (listPeriodState.value is SelectScreenUIState.Success) {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Money SMART",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontFamily = selectViewModel.myFont
                        )

                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SelectTopBarBackground
                ),
                actions = {
                    IconButton(onClick = {
                        selectViewModel.showDialog = true
                    }) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add period",
                            tint = Color.White
                        )
                    }
                })
            Column {
                if (selectViewModel.showDialog) {
                    AddNewPeriodForm(
                        onDialogClose = {
                            selectViewModel.showDialog = false
                            selectViewModel.periodToEdit = null
                            selectViewModel.yearErrorState = false
                            selectViewModel.monthErrorState = false
                            selectViewModel.exceptionErrorState = false
                            selectViewModel.errorText = ""
                        },
                        periodToEdit = selectViewModel.periodToEdit
                    )
                }

                if (selectViewModel.showDeleteDialog) {
                    DeleteQuestion(
                        onDialogClose = {
                            selectViewModel.showDeleteDialog = false
                        },
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(1.dp)
                ) {
                    LazyColumn {
                        items((listPeriodState.value as SelectScreenUIState.Success).periodList) { list ->
                            val listItemState =
                                mainViewModel.itemsList(
                                    mainViewModel.orderBy,
                                    list.period.year,
                                    list.period.month
                                )
                                    .collectAsState(initial = MainScreenUIState.Init)
                            PeriodCard(
                                periodItem = list.period,
                                onRemoveItem = {
                                    if (mainViewModel.isEmptyList((listItemState.value as MainScreenUIState.Success).itemList)) {
                                        selectViewModel.deleteItem(list.periodId)
                                    }
                                    else {
                                        selectViewModel.showDeleteDialog = true
                                    }
                                },
                                onEditItem = {
                                    selectViewModel.showDialog = true
                                    selectViewModel.periodToEdit = it
                                    selectViewModel.idToEdit = list.periodId
                                },
                                onOpenItem = { item ->
                                    onPeriodSelected(item.year, item.month)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteQuestion(
    onDialogClose: () -> Unit = {},
    context: Context = LocalContext.current,
) {
    Dialog(
        onDismissRequest = {
            onDialogClose()
        }
    ) {
        Surface(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(size = 5.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You need to delete all items in the list first",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    ) {
                        Button(
                            onClick = {
                                onDialogClose()
                            },
                            colors = ButtonDefaults.buttonColors(SelectBackground),
                        ) {
                            Text(
                                text = "I understand",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewPeriodForm(
    onDialogClose: () -> Unit = {},
    context: Context = LocalContext.current,
    selectViewModel: SelectViewModel = viewModel(),
    periodToEdit: PeriodItem? = null
) {
    var periodMonth by rememberSaveable { mutableStateOf(periodToEdit?.month ?: "") }
    var periodDescription by rememberSaveable { mutableStateOf(periodToEdit?.description ?: "") }
    var periodYear by rememberSaveable { mutableStateOf(periodToEdit?.year ?: "") }
    val periodCreationDate by rememberSaveable { mutableStateOf(periodToEdit?.date ?: "") }

    fun validate() {
        selectViewModel.yearErrorState =
            TextUtils.isEmpty(periodYear)
        selectViewModel.monthErrorState =
            TextUtils.isEmpty(periodMonth)
        selectViewModel.exceptionErrorState = false

        if (
            selectViewModel.yearErrorState || selectViewModel.monthErrorState
        ) {
            selectViewModel.errorText = "Please fill out the remaining fields"
        }
    }

    Dialog(
        onDismissRequest = {
            onDialogClose()
        }
    ) {
        Surface(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(size = 5.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = periodYear,
                    onValueChange = {
                        periodYear = it
                        validate()
                    },
                    isError = selectViewModel.yearErrorState,
                    label = {
                        Text(text = "Year")
                    },
                    trailingIcon = {
                        if (selectViewModel.yearErrorState) {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = "Year error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(5.dp)
                )

                OutlinedTextField(
                    value = periodMonth,
                    onValueChange = {
                        periodMonth = it
                        validate()
                    },
                    isError = selectViewModel.monthErrorState,
                    label = {
                        Text(text = "Month")
                    },
                    singleLine = true,
                    trailingIcon = {
                        if (selectViewModel.monthErrorState) {
                            Icon(
                                Icons.Filled.Warning, contentDescription = "Month error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .padding(2.dp)
                )

                OutlinedTextField(
                    value = periodDescription,
                    onValueChange = {
                        periodDescription = it
                    },
                    label = {
                        Text(text = "Description")
                    },
                    modifier = Modifier
                        .padding(2.dp)
                )

                if (
                    selectViewModel.yearErrorState ||
                    selectViewModel.monthErrorState ||
                    selectViewModel.exceptionErrorState
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectViewModel.errorText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            validate()
                            try {
                                if (!selectViewModel.yearErrorState && !selectViewModel.monthErrorState) {
                                    if (periodToEdit == null) {
                                        selectViewModel.uploadPeriod(
                                            year = periodYear,
                                            description = periodDescription,
                                            month = periodMonth,
                                            date = SimpleDateFormat("yyyy-MM-dd HH:mm").format(
                                                Calendar.getInstance(
                                                    TimeZone.getTimeZone(
                                                        ZoneId.of(
                                                            "Europe/Berlin"
                                                        )
                                                    )
                                                ).time
                                            ).toString(),
                                        )
                                    } else {
                                        selectViewModel.editPeriod(
                                            itemKey = selectViewModel.idToEdit,
                                            year = periodYear,
                                            description = periodDescription,
                                            month = periodMonth,
                                            date = periodCreationDate,
                                        )
                                    }
                                    onDialogClose()
                                }
                            } catch (e: Exception) {
                                selectViewModel.exceptionErrorState = true
                                selectViewModel.errorText = e.localizedMessage as String
                            }

                        },
                        colors = ButtonDefaults.buttonColors(SelectBackground),
                    ) {
                        Text(text = if (periodToEdit == null) "Add" else "Modify")
                    }
                }
            }
        }
    }
}

@Composable
fun PeriodCard(
    periodItem: PeriodItem,
    onRemoveItem: () -> Unit = {},
    onOpenItem: (PeriodItem) -> Unit = {},
    context: Context = LocalContext.current,
    onEditItem: (PeriodItem) -> Unit = {}
) {
    OutlinedCard(
        colors =
        CardDefaults.cardColors(
            containerColor = SelectBackground
        ),
        shape = RoundedCornerShape(1.dp),
        elevation =
        CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        modifier = Modifier
            .padding(2.dp)
            .clickable {
                onOpenItem(periodItem)
            }

    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.padding(1.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${periodItem.year} - ${periodItem.month}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (periodItem.description.isNotEmpty())
                        Text(
                            text = periodItem.description,
                            fontSize = 14.sp
                        )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        onEditItem(periodItem)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit period",
                            tint = Color.Black
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.x),
                        contentDescription = "Delete period",
                        modifier = Modifier
                            .size(20.dp)
                            .background(SelectBackground)
                            .clickable {
                                onRemoveItem()
                            }
                    )
                }
            }
        }
    }
}