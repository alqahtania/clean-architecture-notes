package com.abdull.cleannotes.framework.presentation.common

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.abdull.cleannotes.business.domain.util.DateUtil
import com.abdull.cleannotes.framework.presentation.notedetail.NoteDetailFragment
import com.abdull.cleannotes.framework.presentation.notelist.NoteListFragment
import com.abdull.cleannotes.framework.presentation.splash.SplashFragment
import javax.inject.Inject

/**
 * Created by Abdullah Alqahtani on 11/26/2020.
 */


class NoteFragmentFactory
@Inject
constructor(
    private val viewModelFactory : ViewModelProvider.Factory,
    private val dateUtil: DateUtil
): FragmentFactory(){

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when(className){

            NoteListFragment::class.java.name -> {
                val fragment = NoteListFragment(viewModelFactory, dateUtil)
                fragment
            }

            NoteDetailFragment::class.java.name -> {
                val fragment = NoteDetailFragment(viewModelFactory)
                fragment
            }

            SplashFragment::class.java.name -> {
                val fragment = SplashFragment(viewModelFactory)
                fragment
            }

            else -> {
                super.instantiate(classLoader, className)
            }
        }
}