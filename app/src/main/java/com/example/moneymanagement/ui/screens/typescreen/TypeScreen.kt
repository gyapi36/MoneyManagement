package com.example.moneymanagement.ui.screens.typescreen

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
import com.example.moneymanagement.ui.screens.mainscreen.AddNewItemForm
import com.example.moneymanagement.ui.screens.mainscreen.DeleteAllQuestion
import com.example.moneymanagement.ui.screens.mainscreen.ListCard
import com.example.moneymanagement.ui.screens.mainscreen.MainScreenUIState
import com.example.moneymanagement.ui.screens.mainscreen.MainViewModel
import com.example.moneymanagement.ui.screens.mainscreen.SpinnerSample
import com.example.moneymanagement.ui.theme.MainBackground
import com.example.moneymanagement.ui.theme.MainTopBarBackground
import com.example.moneymanagement.ui.theme.SelectTopBarBackground
import com.example.moneymanagement.ui.theme.TypeTopBarBackground
import com.example.moneymanagement.ui.theme.WhiteBackGround
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeScreen(
    typeViewModel: TypeViewModel = viewModel(),
    context: Context = LocalContext.current,
    navController: NavController,
    periodYear: String,
    periodMonth: String,
    categoryChosen: String,
) {
    val listItemState =
        typeViewModel.itemsList(typeViewModel.orderBy, periodYear, periodMonth, categoryChosen)
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
                            text = "$periodYear - $periodMonth: $categoryChosen",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontFamily = typeViewModel.myFont
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = TypeTopBarBackground
            ),
            actions = {
                IconButton(onClick = {
                    typeViewModel.showDeleteDialog = true
                }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete all",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        typeViewModel.topMenuExpanded = !typeViewModel.topMenuExpanded
                    }
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "More options",
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = typeViewModel.topMenuExpanded,
                    onDismissRequest = { typeViewModel.topMenuExpanded = false }) {
                    DropdownMenuItem(onClick = {
                        typeViewModel.topMenuExpanded =
                            !typeViewModel.topMenuExpanded
                        typeViewModel.orderBy = "price"
                    },
                        text = { Text(text = "Sort by Price") })
                    DropdownMenuItem(onClick = {
                        typeViewModel.topMenuExpanded =
                            !typeViewModel.topMenuExpanded
                        typeViewModel.orderBy = "date"
                    },
                        text = { Text(text = "Sort by Add date") })
                }
            })
        Column {
            if (typeViewModel.showDialog) {
                AddNewTypeForm(
                    onDialogClose = {
                        typeViewModel.showDialog = false
                        typeViewModel.itemToEdit = null
                        typeViewModel.titleErrorState = false
                        typeViewModel.priceErrorState = false
                        typeViewModel.exceptionErrorState = false
                        typeViewModel.errorText = ""
                    },
                    itemToEdit = typeViewModel.itemToEdit,
                    periodYear = periodYear,
                    periodMonth = periodMonth,
                    categoryChosen = categoryChosen,
                )
            }

            if (typeViewModel.showDeleteDialog) {
                DeleteAllTypeQuestion(
                    onDialogClose = {
                        typeViewModel.showDeleteDialog = false
                    },
                    onDeleteSuccess = {
                        typeViewModel.deleteAll = true
                        typeViewModel.showDeleteDialog = false
                    }
                )
            }

            if (typeViewModel.deleteAll) {
                typeViewModel.deleteAll(
                    (listItemState.value as TypeScreenUIState.Success).itemList,
                    periodYear,
                    periodMonth,
                    categoryChosen
                )
                typeViewModel.deleteAll = false
            }

            Column(
                modifier = Modifier
                    .padding(1.dp)
                    .weight(12f)
            ) {
                if (listItemState.value is TypeScreenUIState.Success) {
                    LazyColumn {
                        items((listItemState.value as TypeScreenUIState.Success).itemList) { list ->
                            ListCard(
                                listItem = list.item,
                                onRemoveItem = {
                                    typeViewModel.deleteItem(list.itemId)
                                },
                                onEditItem = {
                                    typeViewModel.showDialog = true
                                    typeViewModel.itemToEdit = it
                                    typeViewModel.idToEdit = list.itemId
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(2f)
                        .padding(end = 1.dp, bottom = 1.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            typeViewModel.showDialog = true
                        },
                        containerColor = TypeTopBarBackground,
                        shape = RoundedCornerShape(30.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add Type FAB",
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteAllTypeQuestion(
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
fun AddNewTypeForm(
    onDialogClose: () -> Unit = {},
    context: Context = LocalContext.current,
    typeViewModel: TypeViewModel = viewModel(),
    itemToEdit: ListItem? = null,
    periodYear: String,
    periodMonth: String,
    categoryChosen: String,
) {
    var itemTitle by rememberSaveable { mutableStateOf(itemToEdit?.title ?: "") }
    var itemDescription by rememberSaveable { mutableStateOf(itemToEdit?.description ?: "") }
    var itemPrice by rememberSaveable { mutableStateOf(itemToEdit?.price ?: "") }
    var itemCategory by rememberSaveable { mutableStateOf(itemToEdit?.category ?: "") }
    val itemAddDate by rememberSaveable { mutableStateOf(itemToEdit?.date ?: "") }

    fun validate() {
        typeViewModel.titleErrorState =
            TextUtils.isEmpty(itemTitle)
        typeViewModel.priceErrorState =
            TextUtils.isEmpty(itemPrice)
        typeViewModel.exceptionErrorState = false

        if (
            typeViewModel.titleErrorState || typeViewModel.priceErrorState
        ) {
            typeViewModel.errorText = "Please fill out the remaining fields"
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
                    isError = typeViewModel.titleErrorState,
                    label = {
                        Text(text = "Title")
                    },
                    trailingIcon = {
                        if (typeViewModel.titleErrorState) {
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
                    isError = typeViewModel.priceErrorState,
                    label = {
                        Text(text = "Price [$]")
                    },
                    singleLine = true,
                    trailingIcon = {
                        if (typeViewModel.priceErrorState) {
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
                        categoryChosen
                    ),
                    preselected = categoryChosen,
                    onSelectionChanged = { itemCategory = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )

                if (
                    typeViewModel.titleErrorState ||
                    typeViewModel.priceErrorState ||
                    typeViewModel.exceptionErrorState
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = typeViewModel.errorText,
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
                                if (!typeViewModel.priceErrorState && !typeViewModel.titleErrorState) {
                                    if (itemToEdit == null) {
                                        typeViewModel.uploadItem(
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
                                        typeViewModel.editItem(
                                            itemKey = typeViewModel.idToEdit,
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
                                typeViewModel.exceptionErrorState = true
                                typeViewModel.errorText = e.localizedMessage as String
                            }

                        },
                        colors = ButtonDefaults.buttonColors(TypeTopBarBackground),
                    ) {
                        Text(text = if (itemToEdit == null) "Add" else "Modify")
                    }
                }
            }
        }
    }
}

@Composable
fun TypeCard(
    listTypeItem: ListItem,
    mainViewModel: MainViewModel = viewModel(),
    onRemoveItem: () -> Unit = {},
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
                    painter = painterResource(id = mainViewModel.getIcon(listTypeItem.category)),
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
                        text = listTypeItem.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${listTypeItem.price} $",
                        fontSize = 16.sp,
                        color = when (listTypeItem.category) {
                            "Income" -> Color.Green
                            "Saving" -> Color.Blue
                            else -> Color.Red
                        },
                    )
                    if (listTypeItem.description.isNotEmpty())
                        Text(
                            text = listTypeItem.description,
                            fontSize = 14.sp
                        )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        onEditItem(listTypeItem)
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
                    text = "Time of creation: ${listTypeItem.date}",
                    style = TextStyle(fontSize = 14.sp)
                )
            }
        }
    }
}