package com.safesoft.safemobile.backend.db.remote.dao

import android.util.Log
import com.safesoft.safemobile.backend.db.local.entity.AllAboutAPurchase
import com.safesoft.safemobile.backend.db.remote.RemoteDBRepository
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import io.reactivex.Completable
import io.reactivex.internal.operators.completable.CompletableFromCallable
import java.sql.Connection
import java.sql.ResultSet
import javax.inject.Inject

class RemotePurchaseDao @Inject constructor(
    private val connector: RemoteDBRepository,
    override var preferencesRepository: PreferencesRepository
) : BaseDao<AllAboutAPurchase> {
    override var tableName: String? = "BON_A1"
    val linesTableName: String = "BON_A2"

    override var selectQueryColumns: List<String> = listOf(
        "NUM_BON",
        "DATE_BON",
        "HEURE",
        "CODE_FRS",
        "CODE_FRS2",
        "NBR_P",
        "TOT_REMISE",
        "HT",
        "TVA",
        "TIMBRE",
        "REMISE",
        "VERSER",
        "MODE_RG",
        "CODE_CAISSE",
        "BLOCAGE",
        "MODE_TARIF",
        "TOT_QTE",
        "CODE_DEPOT",
        "AUTRE_INFO",
    )


    override var retrievalColumns: List<String> = selectQueryColumns

    override var insertColumns: List<String> = listOf(
        "NUM_BON",
        "DATE_BON",
        "HEURE",
        "CODE_FRS",
        "HT",
        "TVA",
        "TIMBRE",
        "REMISE",
        "VERSER",
        "MODE_RG",
        "BLOCAGE",
        "MODE_TARIF",
        "TOT_QTE",
        "CODE_DEPOT",
        "AUTRE_INFO",
    )

    val insertLineColumns = listOf(
        "NUM_BON",
        "CODE_BARRE",
        "PRODUIT",
        "QTE",
        "PA_HT",
        "TVA",
        "CODE_DEPOT",
        "REMISE",
    )


    override var connection: Connection? = connector.connection


    override val TAG: String
        get() = this::class.simpleName!!

    override fun checkConnection() {
        connection = connector.connection
    }

    override fun retrieve(resultSet: ResultSet): List<AllAboutAPurchase> {
        TODO("Not yet implemented")
    }

    override fun insert(vararg items: AllAboutAPurchase): Completable {
        return CompletableFromCallable {
            items.forEach {
                val code = generateCode("GEN_BON_A1_ID")
                val insertPurchaseQuery: String = createInsertQuery()
                val preparedStatement = prepareStatement(insertPurchaseQuery)
                val values = mutableMapOf<Int, Any>(1 to code)
                values.putAll(it.toMap())
                val boundStatement = bindParams(preparedStatement, values)
                val rows: Int = executeInsert(boundStatement)
                Log.d(TAG, "insert: inserted a purchase")
                if (rows != 0 && rows != -1)
                    it.purchaseLines.forEach { line ->
                        val insertLineQuery = createInsertQuery(insertLineColumns)
                        val preparedLineStatement = prepareStatement(insertLineQuery)
                        val lineValues = mutableMapOf<Int, Any>(1 to code)
                        lineValues.putAll(line.toMap()) // TODO: 20/12/2020  add the code depot here properly
                        val boundLineStatement = bindParams(preparedLineStatement, values)
                        val linesRows: Int = executeInsert(boundLineStatement)

                        if (linesRows != 0 && linesRows != -1)
                            Log.d(TAG, "insert: inserted purchase line")
                        else
                            Log.d(TAG, "insert: failed to insert purchase line")
                    }
                Log.d(TAG, "insert: finished inserting a purchase with its lines")
            }
            true
        }
    }

    override fun update(vararg items: AllAboutAPurchase): Completable {
        TODO("Not yet implemented")
    }


}