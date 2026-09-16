package com.nocomerciales.app

import android.telecom.Call
import android.telecom.CallScreeningService

/**
 * Se invoca en cada llamada entrante (el sistema requiere que la app tenga
 * el rol ROLE_CALL_SCREENING). Si el callerID coincide con la numeración
 * comercial 400XXXXXX, se rechaza automáticamente.
 */
class CallBlockerService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val number = callDetails.handle?.schemeSpecificPart

        if (PhoneNumberMatcher.isCommercialNumber(number)) {
            val response = CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipNotification(true)
                .setSkipCallLog(false)
                .build()
            respondToCall(callDetails, response)
            BlockedCallsStore.addBlockedCall(
                applicationContext,
                number ?: "",
                System.currentTimeMillis()
            )
        } else {
            respondToCall(callDetails, CallResponse.Builder().build())
        }
    }
}
