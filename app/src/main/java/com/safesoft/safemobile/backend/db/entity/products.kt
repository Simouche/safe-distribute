package com.safesoft.safemobile.backend.db.entity

import androidx.room.*

@Entity(
    tableName = "barcodes",
    indices = [Index(value = ["CODE"], unique = true), Index(value = ["PRODUCT"], unique = false)],
    foreignKeys = [ForeignKey(
        entity = Products::class,
        parentColumns = arrayOf("PRODUCT_ID"),
        childColumns = arrayOf("PRODUCT"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Barcodes(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "CODE") val code: String,
    @ColumnInfo(name = "PRODUCT") val product: Long
)


@Entity(
    tableName = "products",
    foreignKeys = [ForeignKey(
        entity = Brands::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("BRAND"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["BRAND"], unique = false)]
)
data class Products(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "PRODUCT_ID") val id: Long,
    @ColumnInfo(name = "REFERENCE") val reference: String,
    @ColumnInfo(name = "DESIGNATION") val designation: String,
    @ColumnInfo(name = "QUANTITY", defaultValue = "0") val quantity: Double? = null,
    @ColumnInfo(name = "PURCHASE_PRICE_HT", defaultValue = "0") val purchasePriceHT: Double? = null,
    @ColumnInfo(name = "TVA", defaultValue = "19") val tva: Double? = null,
    @ColumnInfo(
        name = "PURCHASE_PRICE_TTC",
        defaultValue = "0"
    ) val purchasePriceTTC: Double? = null,
    @ColumnInfo(
        name = "STEADY_PURCHASE_PRICE_HT",
        defaultValue = "0"
    ) val steadyPurchasePriceHT: Double? = null,
    @ColumnInfo(
        name = "STEADY_PURCHASE_PRICE_TTC",
        defaultValue = "0"
    ) val steadyPurchasePriceTTC: Double? = null,
    @ColumnInfo(name = "MARGE") val marge: Double? = null,
    @ColumnInfo(
        name = "SELL_PRICE_DETAIL_HT",
        defaultValue = "0"
    ) val sellPriceDetailHT: Double? = null,
    @ColumnInfo(
        name = "SELL_PRICE_WHOLE_HT",
        defaultValue = "0"
    ) val sellPriceWholeHT: Double? = null,
    @ColumnInfo(
        name = "SELL_PRICE_HAF_WHOLE_HT",
        defaultValue = "0"
    ) val sellPriceHalfWholeHT: Double? = null,
    @ColumnInfo(
        name = "SELL_PRICE_DETAIL_TTC",
        defaultValue = "0"
    ) val sellPriceDetailTTC: Double? = null,
    @ColumnInfo(
        name = "SELL_PRICE_WHOLE_TTC",
        defaultValue = "0"
    ) val sellPriceWholeTTC: Double? = null,
    @ColumnInfo(
        name = "SELL_PRICE_HAF_WHOLE_TTC",
        defaultValue = "0"
    ) val sellPriceHalfWholeTTC: Double? = null,
    @ColumnInfo(name = "PHOTO") val photo: String? = null,
    @ColumnInfo(name = "QUANTITY_PER_PACKAGE") val quantityPerPackage: Long? = null,
    @ColumnInfo(name = "PROMO", defaultValue = "0") val promotion: Double? = null,
    @ColumnInfo(name = "BRAND") val brand: Long? = null
) {

    companion object {
        fun generateBarCode(): String {
            val charPool: List<Char> = ('0'..'5') + ('6'..'9')
            val randomString = (1..11)
                .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");
            return randomString
        }
    }
}

@Entity(
    tableName = "expiration_dates", foreignKeys = [ForeignKey(
        entity = Products::class,
        parentColumns = arrayOf("PRODUCT_ID"),
        childColumns = arrayOf("PRODUCT"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )], indices = [Index(value = ["PRODUCT"], unique = false)]
)
data class ExpirationDates(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "DATE") val date: String,
    @ColumnInfo(name = "DISABLED", defaultValue = "0") val disabled: Boolean,
    @ColumnInfo(name = "PRODUCT") val product: Long
)

@Entity(tableName = "brands", indices = [Index(value = ["NAME"], unique = true)])
data class Brands(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "NAME") val name: String
) {
    override fun toString(): String = name
}

@Entity(
    primaryKeys = ["PRODUCT_ID", "PROVIDER_ID"],
    foreignKeys = [ForeignKey(
        entity = Products::class,
        parentColumns = arrayOf("PRODUCT_ID"),
        childColumns = arrayOf("PRODUCT_ID"),
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Providers::class,
        parentColumns = arrayOf("PROVIDER_ID"),
        childColumns = arrayOf("PROVIDER_ID")
    )],
    indices = [
        Index(value = ["PRODUCT_ID"], unique = false),
        Index(value = ["PROVIDER_ID"], unique = false)
    ]
)
data class ProductProviderCrossRef(
    @ColumnInfo(name = "PRODUCT_ID") val productId: Long,
    @ColumnInfo(name = "PROVIDER_ID") val providerId: Long
)

data class ProductWithProviders(
    @Embedded val product: Products,
    @Relation(
        parentColumn = "PRODUCT_ID",
        entityColumn = "PROVIDER_ID",
        associateBy = Junction(ProductProviderCrossRef::class)
    ) val provider: List<Providers>
)

data class ProductWithBarcodes(
    @Embedded val product: Products,
    @Relation(
        parentColumn = "PRODUCT_ID",
        entityColumn = "PRODUCT"
    ) val bardcodes: List<Barcodes>
)

data class BrandWithProducts(
    @Embedded val brand: Brands,
    @Relation(
        parentColumn = "id",
        entityColumn = "BRAND"
    ) val products: List<Products>
)

data class ProductWithExpirationDates(
    @Embedded val product: Products,
    @Relation(
        parentColumn = "PRODUCT_ID",
        entityColumn = "PRODUCT"
    ) val expirationDates: List<ExpirationDates>
)

data class AllAboutAProduct(
    @Embedded val product: Products,
    @Relation(
        parentColumn = "PRODUCT_ID",
        entityColumn = "PRODUCT"
    ) val bardcodes: List<Barcodes>,
    @Relation(
        parentColumn = "PRODUCT_ID",
        entityColumn = "PRODUCT"
    ) val expirationDates: List<ExpirationDates>?,
    @Relation(
        parentColumn = "BRAND",
        entityColumn = "id"
    ) val brand: Brands?
) {
    override fun toString(): String = product.designation
}

