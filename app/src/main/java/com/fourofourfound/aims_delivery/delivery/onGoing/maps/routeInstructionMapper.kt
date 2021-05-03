package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import com.fourofourfound.aimsdelivery.R
import com.here.android.mpa.routing.Maneuver

/**
 * Route name to image mapper
 * This method provides the upcoming direction sign
 * @param directionName upcoming direction image
 * @return upcoming direction
 */
fun routeNameToImageMapper(directionName: Maneuver.Icon?): Int? {

    return when (directionName) {
        Maneuver.Icon.START -> R.drawable.start
        Maneuver.Icon.UNDEFINED -> null

        Maneuver.Icon.UTURN_RIGHT -> R.drawable.u_turn_right
        Maneuver.Icon.UTURN_LEFT -> R.drawable.u_turn_left
        Maneuver.Icon.HIGHWAY_KEEP_LEFT -> R.drawable.keep_left
        Maneuver.Icon.HIGHWAY_KEEP_RIGHT -> R.drawable.keep_right
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
        Maneuver.Icon.LEAVE_HIGHWAY_RIGHT_LANE -> R.drawable.leave_highway_right_lane

        Maneuver.Icon.END -> R.drawable.end


        else -> R.drawable.start
    }

}

/**
 * Route name to direction text mapper
 * This method provides the name of the next direction
 * @param directionName upcoming direction text
 * @return upcoming direction text
 */
fun routeNameToDirectionTextMapper(directionName: Maneuver.Icon?): String {

    return when (directionName) {
        Maneuver.Icon.START -> "Start"
        Maneuver.Icon.UNDEFINED -> ""
        Maneuver.Icon.UTURN_RIGHT -> "U-Turn Right"
        Maneuver.Icon.UTURN_LEFT -> "U-Turn Left"
        Maneuver.Icon.HIGHWAY_KEEP_LEFT -> "Highway Keep Left"
        Maneuver.Icon.HIGHWAY_KEEP_RIGHT -> "Highway Keep Right"
        Maneuver.Icon.QUITE_LEFT -> "Turn Left"
        Maneuver.Icon.QUITE_RIGHT -> "Turn Right"
        Maneuver.Icon.GO_STRAIGHT -> "Keep Straight"
        Maneuver.Icon.KEEP_RIGHT -> "Keep Right"
        Maneuver.Icon.LIGHT_RIGHT -> "Turn Right"
        Maneuver.Icon.HEAVY_RIGHT -> "Sharp Right"
        Maneuver.Icon.KEEP_MIDDLE -> "Keep Middle"
        Maneuver.Icon.KEEP_LEFT -> "Keep Left"
        Maneuver.Icon.LIGHT_LEFT -> "Turn Left"
        Maneuver.Icon.HEAVY_LEFT -> "Sharp Left"
        Maneuver.Icon.ENTER_HIGHWAY_RIGHT_LANE -> "Enter Highway Right Lane"
        Maneuver.Icon.ENTER_HIGHWAY_LEFT_LANE -> "Enter Highway Left Lane"
        Maneuver.Icon.LEAVE_HIGHWAY_LEFT_LANE -> "Leave Highway Left Lane"
        Maneuver.Icon.LEAVE_HIGHWAY_RIGHT_LANE -> "Leave Highway Right Lane"
        Maneuver.Icon.FERRY -> "Ferry"
        Maneuver.Icon.PASS_STATION -> "Pass Station"
        Maneuver.Icon.HEAD_TO -> "Head To"
        Maneuver.Icon.CHANGE_LINE -> "Change Line"
        Maneuver.Icon.END -> "Arriving"
        else -> "Roundabout"
    }

}