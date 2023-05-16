package com.example.moneymanagement.ui.screens.typescreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import com.example.moneymanagement.R
import com.example.moneymanagement.data.ListItem
import com.example.moneymanagement.data.ListWithId
import com.example.moneymanagement.ui.screens.mainscreen.MainViewModel

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

sealed interface AddItemUIState {
    object Init : AddItemUIState
    object LoadingItemUpload : AddItemUIState
    object ItemUploadSuccess : AddItemUIState
    data class ErrorDuringItemUpload(val error: String?) : AddItemUIState
}

sealed interface TypeScreenUIState {
    object Init : TypeScreenUIState
    data class Success(val itemList: List<ListWithId>) : TypeScreenUIState
    data class Error(val error: String?) : TypeScreenUIState
}

class TypeViewModel : ViewModel() {
    val myFont = FontFamily(Font(R.font.king_font))

    var orderBy by mutableStateOf("date")

    var showDialog by mutableStateOf(false)

    companion object {
        const val COLLECTION_ITEMS = "items"
    }

    var addItemUIState: AddItemUIState by mutableStateOf(AddItemUIState.Init)

    var topMenuExpanded by mutableStateOf(false)
    var showDeleteDialog by mutableStateOf(false)
    var deleteAll by mutableStateOf(false)

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
            MainViewModel.COLLECTION_ITEMS
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

    fun itemsList(order: String, year: String, month: String, category: String) = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                .orderBy(order, Query.Direction.ASCENDING)
                .whereEqualTo("periodYear", year)
                .whereEqualTo("periodMonth", month)
                .whereEqualTo("category", category)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val itemList = snapshot.toObjects(ListItem::class.java)
                        val itemWithIdList = mutableListOf<ListWithId>()
                        itemList.forEachIndexed { index, item ->
                            itemWithIdList.add(ListWithId(snapshot.documents[index].id, item))
                        }
                        TypeScreenUIState.Success(
                            itemWithIdList
                        )
                    } else {
                        TypeScreenUIState.Error(e?.message.toString())
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

    fun deleteAll(itemList: List<ListWithId>, year: String, month: String, category: String) {
        itemList.forEach {
            if (it.item.periodYear == year && it.item.periodMonth == month && it.item.category == category) {
                FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS).document(it.itemId)
                    .delete()
            }
        }
    }
}