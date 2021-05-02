package com.fourofourfound.aims_delivery.utils

import com.fourofourfound.aims_delivery.database.entities.StatusTable

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

public fun getStatusType(statusList:List<StatusTable>,statusType:StatusMessageEnum): StatusTable? {
    return statusList.find {
        it.id == statusType.status
    }
}