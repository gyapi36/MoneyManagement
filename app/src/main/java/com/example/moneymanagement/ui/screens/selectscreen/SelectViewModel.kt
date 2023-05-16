package com.example.moneymanagement.ui.screens.selectscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import com.example.moneymanagement.R
import com.example.moneymanagement.data.PeriodItem
import com.example.moneymanagement.data.PeriodWithId
import com.example.moneymanagement.ui.screens.mainscreen.MainViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

sealed interface AddPeriodUIState {
    object Init : AddPeriodUIState
    object LoadingPeriodUpload : AddPeriodUIState
    object PeriodUploadSuccess : AddPeriodUIState
    data class ErrorDuringPeriodUpload(val error: String?) : AddPeriodUIState
}

sealed interface SelectScreenUIState {
    object Init : SelectScreenUIState
    data class Success(val periodList: List<PeriodWithId>) : SelectScreenUIState
    data class Error(val error: String?) : SelectScreenUIState
}

class SelectViewModel : ViewModel() {

    val myFont = FontFamily(Font(R.font.king_font))

    var addPeriodUIState: AddPeriodUIState by mutableStateOf(AddPeriodUIState.Init)

    var showDialog by mutableStateOf(false)

    var showDeleteDialog by mutableStateOf(false)
    var delete by mutableStateOf(false)

    companion object {
        const val COLLECTION_PERIODS = "periods"
    }

    var periodToEdit: PeriodItem? by mutableStateOf(null)

    var idToEdit: String by mutableStateOf("")

    var yearErrorState by mutableStateOf(false)
    var monthErrorState by mutableStateOf(false)
    var exceptionErrorState by mutableStateOf(false)
    var errorText by mutableStateOf("")

    fun uploadPeriod(
        year: String,
        month: String,
        description: String,
        date: String,
    ) {
        addPeriodUIState = AddPeriodUIState.LoadingPeriodUpload

        val myItem = PeriodItem(
            uid = UUID.randomUUID().toString(),
            year = year,
            month = month,
            description = description,
            date = date,
        )

        val periodsCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_PERIODS
        )
        periodsCollection.add(myItem)
            .addOnSuccessListener {
                addPeriodUIState = AddPeriodUIState.PeriodUploadSuccess
            }
            .addOnFailureListener {
                addPeriodUIState = AddPeriodUIState.ErrorDuringPeriodUpload(it.message)
            }
    }

    fun editPeriod(
        itemKey: String,
        year: String,
        month: String,
        description: String,
        date: String,
    ) {
        addPeriodUIState = AddPeriodUIState.LoadingPeriodUpload

        val myPeriod = PeriodItem(
            uid = UUID.randomUUID().toString(),
            year = year,
            month = month,
            description = description,
            date = date,
        )

        val periodsCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_PERIODS
        ).document(itemKey)
        periodsCollection.set(myPeriod)
            .addOnSuccessListener {
                addPeriodUIState = AddPeriodUIState.PeriodUploadSuccess
            }
            .addOnFailureListener {
                addPeriodUIState = AddPeriodUIState.ErrorDuringPeriodUpload(it.message)
            }
    }

    fun periodsList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(COLLECTION_PERIODS).orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val periodList = snapshot.toObjects(PeriodItem::class.java)
                        val periodWithIdList = mutableListOf<PeriodWithId>()
                        periodList.forEachIndexed { index, period ->
                            periodWithIdList.add(PeriodWithId(snapshot.documents[index].id, period))
                        }
                        SelectScreenUIState.Success(
                            periodWithIdList
                        )
                    } else {
                        SelectScreenUIState.Error(e?.message.toString())
                    }
                    trySend(response) // This line emits a UI state through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun deleteItem(itemKey: String) {
        FirebaseFirestore.getInstance().collection(
            COLLECTION_PERIODS
        ).document(itemKey).delete()
    }

}