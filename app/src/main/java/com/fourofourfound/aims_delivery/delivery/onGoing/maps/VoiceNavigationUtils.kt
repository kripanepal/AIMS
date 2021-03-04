package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.guidance.VoiceCatalog
import com.here.android.mpa.guidance.VoiceGuidanceOptions
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
        id = vPackage.id
        voiceCatalog.downloadVoice(id) { error ->
            if (error == VoiceCatalog.Error.NONE) {

                // set the voice skin for use by navigation manager
                val voiceGuidanceOptions: VoiceGuidanceOptions =
                    navigationManager.voiceGuidanceOptions
                voiceGuidanceOptions.setVoiceSkin(voiceCatalog.getLocalVoiceSkin(id)!!)
                navigationManager.naturalGuidanceMode =
                    EnumSet.allOf(NavigationManager.NaturalGuidanceMode::class.java)

            }
        }
        return true
    }
    return false
}