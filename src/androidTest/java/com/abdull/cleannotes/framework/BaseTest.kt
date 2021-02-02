package com.abdull.cleannotes.framework

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.abdull.cleannotes.framework.presentation.TestBaseApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

/**
 * Created by Abdullah Alqahtani on 11/23/2020.
 */

@ExperimentalCoroutinesApi
@FlowPreview
abstract class BaseTest {

    val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext<Context>() as TestBaseApplication

    abstract fun injectTest()



}