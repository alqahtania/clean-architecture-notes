package com.abdull.cleannotes.framework.presentation.notedetail.state

/**
 * Created by Abdullah Alqahtani on 12/1/2020.
 */
sealed class CollapsingToolbarState{

    class Collapsed: CollapsingToolbarState(){

        override fun toString(): String {
            return "Collapsed"
        }
    }

    class Expanded: CollapsingToolbarState(){

        override fun toString(): String {
            return "Expanded"
        }
    }
}