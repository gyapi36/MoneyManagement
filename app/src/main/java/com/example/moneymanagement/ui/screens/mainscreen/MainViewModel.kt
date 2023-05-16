package com.example.moneymanagement.ui.screens.mainscreen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import com.example.moneymanagement.R
import com.example.moneymanagement.data.ListItem
import com.example.moneymanagement.data.ListWithId
import com.example.moneymanagement.ui.theme.FewLeft
import com.example.moneymanagement.ui.theme.ManyLeft
import com.example.moneymanagement.ui.theme.MediumLeft
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.ArrayList
import java.util.Arrays
import java.util.UUID

sealed interface AddItemUIState {
    object Init : AddItemUIState
    object LoadingItemUpload : AddItemUIState
    object ItemUploadSuccess : AddItemUIState
    data class ErrorDuringItemUpload(val error: String?) : AddItemUIState
}

sealed interface MainScreenUIState {
    object Init : MainScreenUIState
    data class Success(val itemList: List<ListWithId>) : MainScreenUIState
    data class Error(val error: String?) : MainScreenUIState
}

class MainViewModel : ViewModel() {

    val myFont = FontFamily(Font(R.font.king_font))

    var addItemUIState: AddItemUIState by mutableStateOf(AddItemUIState.Init)
    var topMenuExpanded by mutableStateOf(false)
    var orderBy by mutableStateOf("date")

    var showDialog by mutableStateOf(false)

    var showDeleteDialog by mutableStateOf(false)
    var deleteAll by mutableStateOf(false)

    companion object {
        const val COLLECTION_ITEMS = "items"
    }

    var itemToEdit: ListItem? by mutableStateOf(null)

    var idToEdit: String by mutableStateOf("")

    var titleErrorState by mutableStateOf(false)
    var priceErrorState by mutableStateOf(false)
    var exceptionErrorState by mutableStateOf(false)
    var errorText by mutableStateOf("")

    fun uploadItem(
        title: String,
        description: String,
        price: String,
        date: String,
        category: String,
        periodYear: String,
        periodMonth: String,
    ) {
        addItemUIState = AddItemUIState.LoadingItemUpload

        val myItem = ListItem(
            uid = UUID.randomUUID().toString(),
            title = title,
            description = description,
            price = price,
            date = date,
            category = category,
            periodYear = periodYear,
            periodMonth = periodMonth,

            )

        val itemsCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_ITEMS
        )
        itemsCollection.add(myItem)
            .addOnSuccessListener {
                addItemUIState = AddItemUIState.ItemUploadSuccess
            }
            .addOnFailureListener {
                addItemUIState = AddItemUIState.ErrorDuringItemUpload(it.message)
            }
    }

    fun editItem(
        itemKey: String,
        title: String,
        description: String,
        price: String,
        date: String,
        category: String,
        periodYear: String,
        periodMonth: String,
    ) {
        addItemUIState = AddItemUIState.LoadingItemUpload

        val myItem = ListItem(
            uid = UUID.randomUUID().toString(),
            title = title,
            description = description,
            price = price,
            date = date,
            category = category,
            periodYear = periodYear,
            periodMonth = periodMonth,

            )

        val itemsCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_ITEMS
        ).document(itemKey)
        itemsCollection.set(myItem)
            .addOnSuccessListener {
                addItemUIState = AddItemUIState.ItemUploadSuccess
            }
            .addOnFailureListener {
                addItemUIState = AddItemUIState.ErrorDuringItemUpload(it.message)
            }
    }

    fun sumIncomes(items: List<ListWithId>): String {
        var sum = 0
        items.forEach {
            if (it.item.category == "Income") {
                sum += it.item.price.toInt()
            }
        }
        return sum.toString()
    }

    fun sumSavings(items: List<ListWithId>): String {
        var sum = 0
        items.forEach {
            if (it.item.category == "Saving") {
                sum += it.item.price.toInt()
            }
        }
        return sum.toString()
    }

    fun sumExpenses(items: List<ListWithId>): String {
        var sum = 0
        items.forEach {
            if (!(it.item.category == "Income" || it.item.category == "Saving")) {
                sum += it.item.price.toInt()
            }
        }
        return sum.toString()
    }

    fun getRemaining(items: List<ListWithId>): String {
        return (sumIncomes(items).toInt() - sumExpenses(items).toInt() - sumSavings(items).toInt()).toString()
    }

    fun categorizeRemaining(items: List<ListWithId>): Color {
        return if (getRemaining(items).toInt() > sumIncomes(items).toInt() * 0.5) {
            ManyLeft
        } else if (getRemaining(items).toInt() <= sumIncomes(items).toInt() * 0.5
            && getRemaining(items).toInt() >= sumIncomes(items).toInt() * 0.2
        ) {
            MediumLeft
        } else if (getRemaining(items).toInt() == sumIncomes(items).toInt()) {
            Color.Black
        } else {
            FewLeft
        }
    }

    fun itemsList(order: String, year: String, month: String) = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                .orderBy(order, Query.Direction.ASCENDING)
                .whereEqualTo("periodYear", year)
                .whereEqualTo("periodMonth", month)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val itemList = snapshot.toObjects(ListItem::class.java)
                        val itemWithIdList = mutableListOf<ListWithId>()
                        itemList.forEachIndexed { index, item ->
                            itemWithIdList.add(ListWithId(snapshot.documents[index].id, item))
                        }
                        MainScreenUIState.Success(
                            itemWithIdList
                        )
                    } else {
                        MainScreenUIState.Error(e?.message.toString())
                    }
                    trySend(response) // This line emits a UI state through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun deleteItem(itemKey: String) {
        FirebaseFirestore.getInstance().collection(
            COLLECTION_ITEMS
        ).document(itemKey).delete()
    }

    fun deleteAll(itemList: List<ListWithId>, year: String, month: String) {
        itemList.forEach {
            if (it.item.periodYear == year && it.item.periodMonth == month) {
                FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS).document(it.itemId)
                    .delete()
            }
        }
    }

    fun isEmptyList(itemList: List<ListWithId>): Boolean {
        return itemList.isEmpty()
    }

    fun getIcon(itemCategory: String): Int {
        return when (itemCategory) {
            "Income" -> R.drawable.income
            "Overhead" -> R.drawable.overhead
            "Groceries" -> R.drawable.groceries
            "Me" -> R.drawable.me
            "Extra" -> R.drawable.extra
            "Saving" -> R.drawable.deposit
            else -> {
                R.drawable.groceries
            }
        }
    }
}