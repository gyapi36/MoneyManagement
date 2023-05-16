package com.example.moneymanagement.ui.screens.mainscreen

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneymanagement.R
import com.example.moneymanagement.data.ListItem
import com.example.moneymanagement.ui.theme.MainBackground
import com.example.moneymanagement.ui.theme.MainTopBarBackground
import com.example.moneymanagement.ui.theme.SelectTopBarBackground
import com.example.moneymanagement.ui.theme.WhiteBackGround
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel(),
    context: Context = LocalContext.current,
    onTypeSelected: (String, String, String) -> Unit,
    navController: NavController,
    periodYear: String,
    periodMonth: String
) {

    val listItemState =
        mainViewModel.itemsList(mainViewModel.orderBy, periodYear, periodMonth)
            .collectAsState(initial = MainScreenUIState.Init)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Back arrow",
                                tint = Color.White
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "$periodYear - $periodMonth",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontFamily = mainViewModel.myFont
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MainTopBarBackground
            ),
            actions = {
                IconButton(onClick = {
                    mainViewModel.showDeleteDialog = true
                }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete all",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        mainViewModel.topMenuExpanded = !mainViewModel.topMenuExpanded
                    }
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "More options",
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = mainViewModel.topMenuExpanded,
                    onDismissRequest = { mainViewModel.topMenuExpanded = false }) {
                    DropdownMenuItem(onClick = {
                        mainViewModel.topMenuExpanded =
                            !mainViewModel.topMenuExpanded
                        mainViewModel.orderBy = "category"
                    },
                        text = { Text(text = "Sort by Category") })
                    DropdownMenuItem(onClick = {
                        mainViewModel.topMenuExpanded =
                            !mainViewModel.topMenuExpanded
                        mainViewModel.orderBy = "price"
                    },
                        text = { Text(text = "Sort by Price") })
                    DropdownMenuItem(onClick = {
                        mainViewModel.topMenuExpanded =
                            !mainViewModel.topMenuExpanded
                        mainViewModel.orderBy = "date"
                    },
                        text = { Text(text = "Sort by Add date") })
                }
            })
        Column {
            if (mainViewModel.showDialog) {
                AddNewItemForm(
                    onDialogClose = {
                        mainViewModel.showDialog = false
                        mainViewModel.itemToEdit = null
                        mainViewModel.titleErrorState = false
                        mainViewModel.priceErrorState = false
                        mainViewModel.exceptionErrorState = false
                        mainViewModel.errorText = ""
                    },
                    itemToEdit = mainViewModel.itemToEdit,
                    periodYear = periodYear,
                    periodMonth = periodMonth,
                )
            }

            if (mainViewModel.showDeleteDialog){
                DeleteAllQuestion(
                    onDialogClose = {
                        mainViewModel.showDeleteDialog = false
                    },
                    onDeleteSuccess = {
                        mainViewModel.deleteAll = true
                        mainViewModel.showDeleteDialog = false
                    }
                )
            }

            if (mainViewModel.deleteAll) {
                mainViewModel.deleteAll(
                    (listItemState.value as MainScreenUIState.Success).itemList,
                    periodYear,
                    periodMonth
                )
                mainViewModel.deleteAll = false
            }

            Column(
                modifier = Modifier
                    .padding(1.dp)
                    .weight(5f)
            ) {
                if (listItemState.value is MainScreenUIState.Success) {
                    LazyColumn {
                        items((listItemState.value as MainScreenUIState.Success).itemList) { list ->
                            ListCard(
                                listItem = list.item,
                                onRemoveItem = {
                                    mainViewModel.deleteItem(list.itemId)
                                },
                                onEditItem = {
                                    mainViewModel.showDialog = true
                                    mainViewModel.itemToEdit = it
                                    mainViewModel.idToEdit = list.itemId
                                },
                                onOpenItem = {
                                    onTypeSelected(list.item.periodYear, list.item.periodMonth, list.item.category)
                                }
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(4f)
                ) {
                    Card(
                        colors =
                        CardDefaults.cardColors(
                            containerColor = WhiteBackGround
                        ),
                        shape = RoundedCornerShape(5.dp),
                        elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        ),
                        modifier = Modifier
                            .padding(top = 10.dp, start = 5.dp, end = 5.dp)
                    ) {
                        if (listItemState.value is MainScreenUIState.Success) {
                            val items = (listItemState.value as MainScreenUIState.Success).itemList
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(30.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Income",
                                        color = Color.DarkGray,
                                        fontFamily = mainViewModel.myFont
                                    )
                                    Text(
                                        text = "${mainViewModel.sumIncomes(items)} $",
                                        fontSize = 20.sp,
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = "Expenses",
                                        color = Color.DarkGray,
                                        fontFamily = mainViewModel.myFont
                                    )
                                    Text(
                                        text = "${mainViewModel.sumExpenses(items)} $",
                                        fontSize = 20.sp,
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = "Savings",
                                        color = Color.DarkGray,
                                        fontFamily = mainViewModel.myFont
                                    )
                                    Text(
                                        text = "${mainViewModel.sumSavings(items)} $",
                                        fontSize = 20.sp,
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Remaining",
                                        color = Color.DarkGray,
                                        fontFamily = mainViewModel.myFont
                                    )
                                    Text(
                                        text = "${mainViewModel.getRemaining(items)} $",
                                        fontSize = 20.sp,
                                        color = mainViewModel.categorizeRemaining(items)
                                    )
                                }
                            }
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(2f)
                        .padding(end = 1.dp, bottom = 5.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            mainViewModel.showDialog = true
                        },
                        containerColor = MainTopBarBackground,
                        shape = RoundedCornerShape(35.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add FAB",
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteAllQuestion(
    onDialogClose: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {

                    Text(
                        text = "Are you sure about deleting all items in this list?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onDeleteSuccess()
                            },
                            colors = ButtonDefaults.buttonColors(MainTopBarBackground),
                        ) {
                            Text(
                                text = "Delete all",
                                fontSize = 16.sp
                            )
                        }
                        Button(
                            onClick = {
                                onDialogClose()
                            },
                            colors = ButtonDefaults.buttonColors(SelectTopBarBackground),
                        ) {
                            Text(
                                text = "Cancel",
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
fun AddNewItemForm(
    onDialogClose: () -> Unit = {},
    context: Context = LocalContext.current,
    mainViewModel: MainViewModel = viewModel(),
    itemToEdit: ListItem? = null,
    periodYear: String,
    periodMonth: String,
) {

    var itemTitle by rememberSaveable { mutableStateOf(itemToEdit?.title ?: "") }
    var itemDescription by rememberSaveable { mutableStateOf(itemToEdit?.description ?: "") }
    var itemPrice by rememberSaveable { mutableStateOf(itemToEdit?.price ?: "") }
    var itemCategory by rememberSaveable { mutableStateOf(itemToEdit?.category ?: "") }
    val itemAddDate by rememberSaveable { mutableStateOf(itemToEdit?.date ?: "") }

    fun validate() {
        mainViewModel.titleErrorState =
            TextUtils.isEmpty(itemTitle)
        mainViewModel.priceErrorState =
            TextUtils.isEmpty(itemPrice)
        mainViewModel.exceptionErrorState = false

        if (
            mainViewModel.titleErrorState || mainViewModel.priceErrorState
        ) {
            mainViewModel.errorText = "Please fill out the remaining fields"
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
                    value = itemTitle,
                    onValueChange = {
                        itemTitle = it
                        validate()
                    },
                    isError = mainViewModel.titleErrorState,
                    label = {
                        Text(text = "Title")
                    },
                    trailingIcon = {
                        if (mainViewModel.titleErrorState) {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = "Title error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(5.dp)
                )

                OutlinedTextField(
                    value = itemPrice,
                    onValueChange = {
                        itemPrice = it
                        validate()
                    },
                    isError = mainViewModel.priceErrorState,
                    label = {
                        Text(text = "Price [$]")
                    },
                    singleLine = true,
                    trailingIcon = {
                        if (mainViewModel.priceErrorState) {
                            Icon(
                                Icons.Filled.Warning, contentDescription = "Price error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .padding(2.dp)
                )

                OutlinedTextField(
                    value = itemDescription,
                    onValueChange = {
                        itemDescription = it
                    },
                    label = {
                        Text(text = "Description")
                    },
                    modifier = Modifier
                        .padding(2.dp)
                )

                SpinnerSample(
                    listOf(
                        "Groceries",
                        "Income",
                        "Overhead",
                        "Me",
                        "Extra",
                        "Saving"
                    ),
                    preselected = if (itemCategory != "") itemCategory else {
                        "Groceries"
                    },
                    onSelectionChanged = { itemCategory = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )

                if (
                    mainViewModel.titleErrorState ||
                    mainViewModel.priceErrorState ||
                    mainViewModel.exceptionErrorState
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mainViewModel.errorText,
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
                                if (!mainViewModel.priceErrorState && !mainViewModel.titleErrorState) {
                                    if (itemToEdit == null) {
                                        mainViewModel.uploadItem(
                                            title = itemTitle,
                                            description = itemDescription,
                                            price = itemPrice,
                                            date = SimpleDateFormat("yyyy-MM-dd HH:mm").format(
                                                Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Europe/Berlin"))).time).toString(),
                                            category = itemCategory,
                                            periodYear = periodYear,
                                            periodMonth = periodMonth,
                                        )
                                    } else {
                                        mainViewModel.editItem(
                                            itemKey = mainViewModel.idToEdit,
                                            title = itemTitle,
                                            description = itemDescription,
                                            price = itemPrice,
                                            date = itemAddDate,
                                            category = itemCategory,
                                            periodYear = periodYear,
                                            periodMonth = periodMonth,
                                        )
                                    }
                                    onDialogClose()
                                }
                            } catch (e: Exception) {
                                mainViewModel.exceptionErrorState = true
                                mainViewModel.errorText = e.localizedMessage as String
                            }

                        },
                        colors = ButtonDefaults.buttonColors(MainTopBarBackground),
                    ) {
                        Text(text = if (itemToEdit == null) "Add" else "Modify")
                    }
                }
            }
        }
    }
}

@Composable
fun SpinnerSample(
    list: List<String>,
    preselected: String,
    onSelectionChanged: (myData: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by rememberSaveable { mutableStateOf(preselected) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    onSelectionChanged(selected)

    OutlinedCard(
        modifier = modifier.clickable {
            expanded = !expanded
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = selected,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Icon(
                Icons.Outlined.ArrowDropDown, null, modifier =
                Modifier.padding(8.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.68f)
            ) {
                list.forEach { listEntry ->
                    DropdownMenuItem(
                        onClick = {
                            selected = listEntry
                            expanded = false
                            onSelectionChanged(selected)
                        },
                        text = {
                            Text(
                                text = listEntry,
                                modifier = Modifier
                                    .align(Alignment.Start)
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun ListCard(
    listItem: ListItem,
    mainViewModel: MainViewModel = viewModel(),
    onRemoveItem: () -> Unit = {},
    onOpenItem: () -> Unit = {},
    context: Context = LocalContext.current,
    onEditItem: (ListItem) -> Unit = {}
) {
    OutlinedCard(
        colors =
        CardDefaults.cardColors(
            containerColor = WhiteBackGround
        ),
        shape = RoundedCornerShape(1.dp),
        elevation =
        CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        modifier = Modifier
            .padding(2.dp)
            .clickable {
                onOpenItem()
            }

    ) {

        var expanded by rememberSaveable { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .padding(5.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.padding(1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = mainViewModel.getIcon(listItem.category)),
                    contentDescription = "List icon",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 10.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = listItem.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${listItem.price} $",
                        fontSize = 16.sp,
                        color = when (listItem.category) {
                            "Income" -> Color.Green
                            "Saving" -> Color.Blue
                            else -> Color.Red
                        },
                    )
                    if (listItem.description.isNotEmpty())
                        Text(
                            text = listItem.description,
                            fontSize = 14.sp
                        )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        onEditItem(listItem)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit list item",
                            tint = Color.Black
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.x),
                        contentDescription = "Delete item",
                        modifier = Modifier
                            .size(20.dp)
                            .background(WhiteBackGround)
                            .clickable {
                                onRemoveItem()
                            }
                    )
                    IconButton(onClick = {
                        expanded = !expanded
                    }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) {
                                "Less"
                            } else {
                                "More"
                            }
                        )
                    }
                }
            }

            if (expanded) {
                Text(
                    text = "Time of creation: ${listItem.date}",
                    style = TextStyle(fontSize = 14.sp)
                )
            }
        }
    }
}