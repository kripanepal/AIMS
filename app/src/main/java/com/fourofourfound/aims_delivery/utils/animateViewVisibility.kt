package com.fourofourfound.aims_delivery.utils

import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup

 fun animateViewVisibility(
     root: View,
     view: View,
     isVisible: Boolean,
     gravity: Int = Gravity.RIGHT
 ) {
     root.post {
         TransitionManager.beginDelayedTransition(root as ViewGroup?)
         val transition: Transition = Slide(gravity);
         transition.duration = 800
         transition.addTarget(view)
         TransitionManager.beginDelayedTransition(
             view.parent as ViewGroup?,
             transition
         )
         view.visibility = if (isVisible) View.VISIBLE else View.GONE

    }

}