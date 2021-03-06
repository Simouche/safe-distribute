package com.safesoft.safemobile.backend.api.response

import com.safesoft.safemobile.backend.db.local.entity.Clients

class ClientResponse(status: Boolean, message: String, client: Clients) :
    BaseResponse(status, message)

class ClientsResponse(status: Boolean, message: String, val clients: List<Clients>) :
    BaseResponse(status, message)