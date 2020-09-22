package com.safesoft.safemobile.backend.di.module

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.safesoft.safemobile.backend.PREFERENCES_ENCRYPT_KEY
import com.safesoft.safemobile.backend.PREFERENCES_NAME
import com.safesoft.safemobile.backend.db.SafeDatabase
import com.safesoft.safemobile.backend.db.dao.*
import com.safesoft.safemobile.backend.db.entity.Products
import com.safesoft.safemobile.backend.utils.asSHA256
import com.securepreferences.SecurePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): SafeDatabase {
        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val password = "123456789".asSHA256()
                db.execSQL("INSERT INTO users (USERNAME,PASSWORD,LOGGED) VALUES ('ADMIN','$password',0);")
                for (i in 0..200)
                    db.execSQL("INSERT INTO providers (CODE,NAME) VALUES ('123$i','TEST $i PROVIDER $i');")

                for (i in 0..200)
                    db.execSQL("INSERT INTO clients (CODE,NAME) VALUES ('$i','TEST $i CLIENT $i');")

                for (i in 0..8000) {
                    db.execSQL("INSERT INTO products (REFERENCE,DESIGNATION,QUANTITY,PURCHASE_PRICE_HT,PURCHASE_PRICE_TTC,MARGE,SELL_PRICE_DETAIL_HT) VALUES ('$i','DESIGNATION $i',$i,$i,$i,$i,$i);")
                    db.execSQL("INSERT INTO barcodes (CODE,PRODUCT) VALUES (${Products.generateBarCode()},$i);")
                    db.execSQL("INSERT INTO barcodes (CODE,PRODUCT) VALUES (${Products.generateBarCode()},$i);")
                }

            }
        }

        return Room.databaseBuilder(
            application,
            SafeDatabase::class.java,
            "safedatabase"
        ).addCallback(callback)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: SafeDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideProvidersDao(db: SafeDatabase): ProvidersDao = db.providersDao()

    @Provides
    @Singleton
    fun provideProductsDao(db: SafeDatabase): ProductsDao = db.productsDao()

    @Provides
    @Singleton
    fun providePurchasesDao(db: SafeDatabase): PurchasesDao = db.purchaseDao()
    @Provides
    @Singleton
    fun provideSalesDao(db: SafeDatabase): SalesDao = db.salesDao()

    @Provides
    @Singleton
    fun provideClientsDao(db: SafeDatabase): ClientsDao = db.clientsDao()

    @Provides
    @Singleton
    fun provideHomeDao(db: SafeDatabase): HomeDao = db.homeDao()

    @Provides
    @Singleton
    fun providesSharedPreferences(application: Application): SharedPreferences {
        return SecurePreferences(application, PREFERENCES_ENCRYPT_KEY, PREFERENCES_NAME)
    }


}