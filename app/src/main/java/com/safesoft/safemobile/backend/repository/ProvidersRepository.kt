package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.api.service.ClientService
import com.safesoft.safemobile.backend.api.service.ProviderService
import com.safesoft.safemobile.backend.db.dao.ProvidersDao
import com.safesoft.safemobile.backend.db.entity.Providers
import javax.inject.Inject

class ProvidersRepository @Inject constructor(
    private val providersDao: ProvidersDao,
    private val providerService: ProviderService
) {

    fun getAllProviders() = providersDao.getAllProviders()

    fun getProviderById(id: Long) = providersDao.getProviderById(id)

    fun searchProvider(query: String) = providersDao.searchProvider(query)

    fun searchProviderFlow(query: String) = providersDao.searchProviderFlow(query)

    fun getAllProvidersWithProducts() = providersDao.getAllProvidersWithProducts()

    fun getAllProvidersWithPurchases() = providersDao.getAllProvidersWithPurchases()

    fun addProviders(vararg providers: Providers) = providersDao.addProviders(*providers)

    fun updateProviders(vararg providers: Providers) = providersDao.updateProviders(*providers)

    fun deleteProviders(vararg providers: Providers) = providersDao.deleteProviders(*providers)

    fun deleteAllProviders() = providersDao.deleteAllProviders()

    fun loadProvidersFromServer() = providerService.getAllProviders()

}