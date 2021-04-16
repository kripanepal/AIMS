package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.guidance.VoiceCatalog
import com.here.android.mpa.guidance.VoicePackage
import java.util.*


fun NavigationFragment.setUpVoiceNavigation() {
    val voiceCatalog = VoiceCatalog.getInstance()
    voiceCatalog.downloadCatalog { error ->
        if (error == VoiceCatalog.Error.NONE) {
            // Get the list of voice packages from the voice catalog list
            checking(voiceCatalog)
        }
    }
}

private fun NavigationFragment.checking(
    voiceCatalog: VoiceCatalog
) {
    val voicePackages = voiceCatalog.catalogList
    // select
    for (vPackage in voicePackages) {
        if (vPackage.marcCode.compareTo("eng", ignoreCase = true) == 0) {
            if (downloading(vPackage, voiceCatalog)) break
        }
    }
}

private fun NavigationFragment.downloading(
    vPackage: VoicePackage,
    voiceCatalog: VoiceCatalog
): Boolean {
    if (vPackage.isTts) {
        var voiceId = vPackage.id
        voiceCatalog.downloadVoice(voiceId) { error ->
            if (error == VoiceCatalog.Error.NONE) {
                // set the voice skin for use by navigation manager
                navigationManager.voiceGuidanceOptions.setVoiceSkin(
                    voiceCatalog.getLocalVoiceSkin(
                        voiceId
                    )!!
                )
                navigationManager.naturalGuidanceMode =
                    EnumSet.allOf(NavigationManager.NaturalGuidanceMode::class.java)
            }
        }
        return true
    }
    return false
}