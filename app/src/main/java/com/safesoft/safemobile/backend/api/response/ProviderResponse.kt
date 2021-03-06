package com.safesoft.safemobile.backend.api.response

import com.safesoft.safemobile.backend.db.local.entity.Providers

class ProvidersResponse(status: Boolean, message: String, val providers: List<Providers>) :
    BaseResponse(status, message)