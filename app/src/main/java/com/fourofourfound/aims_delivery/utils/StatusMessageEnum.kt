package com.fourofourfound.aims_delivery.utils


/**
 * Status message enum
 * This class represents status of the trip or a destination
 * @property status status of the trip or a destination
 * @constructor
 */
enum class StatusMessageEnum (val status: Int,val  code:String,val  message:String) {
    ARRIVESRC(2,"ArriveSrc","Arrive At Source"),
    ARRIVESITE(3,"ArriveSite","Arrive at Site"),
    LEAVESRC(4,"LeaveSrc","Leaving Source"),
    LEAVESITE(5,"LeaveSite","Leaving Site"),
    ONBREAK(6,"OnBreak","Driver on Break"),
    ONDUTY(7,"OnDuty","Clocking in for work Day"),
    OFFDUTY(8,"OffDuty","Clocking out for work day"),
    DRIVING(9,"Driving","Driving to next location"),
    SELTRIP(10,"SelTrip","Select Trip"),
    SRCSITE(13,"SrcSite","Heading to Site From Source"),
    SITESITE(14,"SiteSite","Heading to Site from Site"),
    TRIPDONE(16,"TripDone","Trip Complete"),
}

