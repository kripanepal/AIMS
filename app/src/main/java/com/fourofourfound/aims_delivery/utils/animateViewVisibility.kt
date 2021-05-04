package com.fourofourfound.aims_delivery.utils

import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup

/**
 * Animate view visibility
 * This method animated the give view relative to the given parent
 * @param root the parent where the animation begins or ends
 * @param view the view to be animated
 * @param makeVisible the flag which tells to hide view or not
 * @param gravity the direction where the animation starts (default = right)
 */
fun animateViewVisibility(
    root: View,
    view: View,
    makeVisible: Boolean,
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
        view.visibility = if (makeVisible) View.VISIBLE else View.GONE

    }

}