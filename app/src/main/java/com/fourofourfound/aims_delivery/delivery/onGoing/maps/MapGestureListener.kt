package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import android.graphics.PointF
import com.here.android.mpa.common.ViewObject
import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.mapping.MapGesture


/**
 * My on gesture listener
 * This class is responsible for listening map gestures.
 * @constructor Create empty My on gesture listener
 */
class MyOnGestureListener() : MapGesture.OnGestureListener {

    /**
     * On pan start
     * Called when the drag starts on map
     */
    override fun onPanStart() {
        NavigationManager.getInstance().mapUpdateMode = NavigationManager.MapUpdateMode.NONE
    }

    /**
     * On pan end
     * Called when the drag end on map
     */
    override fun onPanEnd() {
        NavigationManager.getInstance().mapUpdateMode = NavigationManager.MapUpdateMode.NONE
    }

    override fun onMultiFingerManipulationStart() {
    }

    override fun onMultiFingerManipulationEnd() {
    }

    override fun onMapObjectsSelected(p0: MutableList<ViewObject>) = false

    override fun onTapEvent(p0: PointF) = false

    override fun onDoubleTapEvent(p0: PointF) = false

    override fun onPinchLocked() {
    }

    /**
     * On pinch zoom event
     * Called when pich zoom is detected.
     * @param p0
     * @param p1
     * @return
     */
    override fun onPinchZoomEvent(p0: Float, p1: PointF): Boolean {
        NavigationManager.getInstance().mapUpdateMode = NavigationManager.MapUpdateMode.NONE
        return false
    }

    override fun onRotateLocked() {
    }

    /**
     * On rotate event
     * Called when map is rotated.
     * @param p0
     * @return
     */
    override fun onRotateEvent(p0: Float): Boolean {
        NavigationManager.getInstance().mapUpdateMode = NavigationManager.MapUpdateMode.NONE
        return false
    }

    override fun onTiltEvent(p0: Float) = false

    override fun onLongPressEvent(p0: PointF) = false

    override fun onLongPressRelease() {}

    override fun onTwoFingerTapEvent(p0: PointF) = false

}