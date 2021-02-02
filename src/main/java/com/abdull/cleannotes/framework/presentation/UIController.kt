package com.abdull.cleannotes.framework.presentation

import com.abdull.cleannotes.business.domain.state.DialogInputCaptureCallback
import com.abdull.cleannotes.business.domain.state.Response
import com.abdull.cleannotes.business.domain.state.StateMessageCallback

/**
 * Created by Abdullah Alqahtani on 11/28/2020.
 */
interface UIController {

    fun displayProgressBar(isDisplayed: Boolean)

    fun hideSoftKeyboard()

    fun displayInputCaptureDialog(title: String, callback: DialogInputCaptureCallback)

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

}