package com.fourofourfound.aims_delivery.utils

import com.fourofourfound.aims_delivery.database.entities.StatusTable

/**
 * Status message enum
 * This class represents status of the trip or a destination
 * @property status status of the trip or a destination
 * @constructor
 */
enum class StatusMessageEnum (val status: Int) {
    ARRIVESRC(2),
    ARRIVESITE(3),
    LEAVESRC(4),
    LEAVESITE(5),
    ONBREAK(6),
    ONDUTY(7),
    OFFDUTY(8),
    DRIVING(9),
    SELTRIP(10),
    SRCSITE(13),
    SITESITE(14),
    TRIPDONE(16),
}

/**
 * Get status type
 * This method gets the list of status code with status messages
 * @param statusList the list from where the status code and messages are to be found
 * @param statusType the status code to be filtered
 * @return the status table object with the status code and message
 */
fun getStatusType(statusList: List<StatusTable>, statusType: StatusMessageEnum): StatusTable? {
    return statusList.find {
        it.id == statusType.status
    }
}