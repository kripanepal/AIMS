package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import com.fourofourfound.aimsdelivery.R
import com.here.android.mpa.routing.Maneuver


fun routeNameToImageMapper(directionName: Maneuver.Icon?): Int {

    return when (directionName) {
        Maneuver.Icon.START -> R.drawable.start
        Maneuver.Icon.UNDEFINED -> R.drawable.start

        Maneuver.Icon.UTURN_RIGHT -> R.drawable.start
        Maneuver.Icon.UTURN_LEFT -> R.drawable.start
        Maneuver.Icon.HIGHWAY_KEEP_LEFT -> R.drawable.start
        Maneuver.Icon.HIGHWAY_KEEP_RIGHT -> R.drawable.start
        Maneuver.Icon.UTURN_RIGHT -> R.drawable.start


        Maneuver.Icon.QUITE_LEFT -> R.drawable.outer_left
        Maneuver.Icon.QUITE_RIGHT -> R.drawable.outer_right
        Maneuver.Icon.GO_STRAIGHT -> R.drawable.go_straight
        Maneuver.Icon.KEEP_RIGHT -> R.drawable.keep_right
        Maneuver.Icon.LIGHT_RIGHT -> R.drawable.light_right
        Maneuver.Icon.HEAVY_RIGHT -> R.drawable.heavy_right
        Maneuver.Icon.KEEP_MIDDLE -> R.drawable.keep_middle
        Maneuver.Icon.KEEP_LEFT -> R.drawable.keep_left
        Maneuver.Icon.LIGHT_LEFT -> R.drawable.light_left
        Maneuver.Icon.HEAVY_LEFT -> R.drawable.heavy_left
        Maneuver.Icon.ENTER_HIGHWAY_RIGHT_LANE -> R.drawable.enter_highway_right_lane
        Maneuver.Icon.ENTER_HIGHWAY_LEFT_LANE -> R.drawable.enter_highway_left_lane
        Maneuver.Icon.LEAVE_HIGHWAY_LEFT_LANE -> R.drawable.leave_highway_left_lane
        Maneuver.Icon.HIGHWAY_KEEP_RIGHT -> R.drawable.leave_highway_right_lane

        Maneuver.Icon.END -> R.drawable.end


        else -> R.drawable.start
    }

}