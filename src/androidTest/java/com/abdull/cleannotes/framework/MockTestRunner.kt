package com.abdull.cleannotes.framework

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.abdull.cleannotes.framework.presentation.TestBaseApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

/**
 * Created by Abdullah Alqahtani on 11/20/2020.
 * This class is needed to tell Junit runner to use the TestBaseApplication instead
 * of the real BaseApplication for instrumentation testing
 */

@FlowPreview
@ExperimentalCoroutinesApi
class MockTestRunner : AndroidJUnitRunner(){

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(
            cl,
            TestBaseApplication::class.java.name,
            context)
    }
}