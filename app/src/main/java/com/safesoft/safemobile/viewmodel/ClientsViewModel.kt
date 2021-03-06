package com.safesoft.safemobile.viewmodel

import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.safesoft.safemobile.backend.api.response.ClientsResponse
import com.safesoft.safemobile.backend.db.local.entity.Clients
import com.safesoft.safemobile.backend.db.local.entity.FiscalData
import com.safesoft.safemobile.backend.db.local.entity.Location
import com.safesoft.safemobile.backend.db.local.entity.Providers
import com.safesoft.safemobile.backend.repository.ClientsRepository
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.forms.ClientForm

class ClientsViewModel @ViewModelInject constructor(
    private val clientsRepository: ClientsRepository,
    private val config: PagedList.Config,
    val clientForm: ClientForm
) : BaseViewModel(), BaseFormOwner {
    private val tag = ClientsViewModel::class.simpleName

    val searchQuery = MutableLiveData<String>()

    val clientsList: LiveData<PagedList<Clients>> =
        clientsRepository.getAllClients().toLiveData(config = config)

    fun search(query: String): LiveData<PagedList<Clients>> =
        clientsRepository.searchClient(query).toLiveData(config = config)


    fun codeDoubleTap(): Boolean {
        Log.d(tag, "Code double tapped!")
        clientForm.fields.code.value = Providers.generateProviderCode()
        return true
    }


    fun searchFlow(query: String): LiveData<Resource<List<Clients>>> {
        val data = MutableLiveData<Resource<List<Clients>>>()
        enqueue(clientsRepository.searchClientFlow(query), data)
        return data
    }

    val onFocusCode: View.OnFocusChangeListener = View.OnFocusChangeListener { view, focused ->
        run {
            val et = view as EditText
            if (et.text.isNotEmpty() && !focused)
                clientForm.isCodeValid(true)
        }
    }

    private fun saveProvider(vararg clients: Clients): LiveData<Resource<Int>> {
        val data = MutableLiveData<Resource<Int>>()
        enqueue(clientsRepository.addClients(*clients), data)
        return data
    }

    fun saveClient(): LiveData<Resource<Int>> {
        val fields = clientForm.fields
        val client = Clients(
            0,
            code = fields.code.value!!,
            name = fields.name.value,
            activity = fields.activity.value,
            address = fields.address.value,
            commune = fields.commune.value,
            contact = fields.contact.value,
            phones = fields.phones.value,
            faxes = fields.faxes.value,
            rib = fields.rib.value,
            webSite = fields.webSite.value,
            sold = fields.initialSold.value,
            note = fields.note.value,
            provider = fields.provider.value,
            fiscalData = FiscalData(
                fields.registreCommerce.value,
                fields.articleFiscale.value,
                fields.identifiantFiscale.value,
                fields.identifiantStatistic.value
            ),
            location = Location(
                fields.longitude.value ?: 0.0,
                fields.latitude.value ?: 0.0
            ),
            synched = false,
            inApp = true
        )
        return saveProvider(client)
    }

    override fun reInitFields() {
        val fields = clientForm.fields
        fields.code.value = null
        fields.name.value = null
        fields.activity.value = null
        fields.address.value = null
        fields.commune.value = null
        fields.contact.value = null
        fields.phones.value = null
        fields.faxes.value = null
        fields.rib.value = null
        fields.webSite.value = null
        fields.initialSold.value = null
        fields.note.value = null
        fields.provider.value = null
        fields.registreCommerce.value = null
        fields.articleFiscale.value = null
        fields.identifiantFiscale.value = null
        fields.identifiantStatistic.value = null
        fields.longitude.value = 0.0
        fields.latitude.value = 0.0
    }

    fun loadClientsFromServer() = clientsRepository.loadClientsFromServer()

    fun loadClientsFromServerUI(): LiveData<Resource<ClientsResponse>> {
        val data = MutableLiveData<Resource<ClientsResponse>>()
        enqueue(clientsRepository.loadClientsFromServer(), data)
        return data
    }
}