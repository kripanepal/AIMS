package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import android.graphics.PointF
import android.os.Handler

import com.here.android.mpa.common.ViewObject
import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.mapping.MapGesture


// Map gesture listener
class MyOnGestureListener() : MapGesture.OnGestureListener {
    override fun onPanStart() {
        NavigationManager.getInstance().mapUpdateMode = NavigationManager.MapUpdateMode.NONE
    }

    override fun onPanEnd() {
        NavigationManager.getInstance().mapUpdateMode = NavigationManager.MapUpdateMode.NONE
    }

    override fun onMultiFingerManipulationStart() {
    }

    override fun onMultiFingerManipulationEnd() {
    }

    override fun onMapObjectsSelected(p0: MutableList<ViewObject>)= false

    override fun onTapEvent(p0: PointF)= false

    override fun onDoubleTapEvent(p0: PointF)= false

    override fun onPinchLocked() {
    }

    override fun onPinchZoomEvent(p0: Float, p1: PointF): Boolean {
        NavigationManager.getInstance().mapUpdateMode = NavigationManager.MapUpdateMode.NONE
        return false
    }
    override fun onRotateLocked() {
    }

    override fun onRotateEvent(p0: Float): Boolean {
        NavigationManager.getInstance().mapUpdateMode = NavigationManager.MapUpdateMode.NONE
        return false
    }

    override fun onTiltEvent(p0: Float) = false

    override fun onLongPressEvent(p0: PointF)= false

    override fun onLongPressRelease() {
    }

    override fun onTwoFingerTapEvent(p0: PointF)= false

}