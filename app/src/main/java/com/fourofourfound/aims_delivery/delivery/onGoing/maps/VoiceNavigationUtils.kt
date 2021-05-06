package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.guidance.VoiceCatalog
import com.here.android.mpa.guidance.VoicePackage
import java.util.*

/**
 * Set up voice navigation
 * This method downloads the voice catalog used for navigation.
 */
fun NavigationFragment.setUpVoiceNavigation() {
    val voiceCatalog = VoiceCatalog.getInstance()
    voiceCatalog.downloadCatalog { error ->
        if (error == VoiceCatalog.Error.NONE) {
            // Get the list of voice packages from the voice catalog list
            checkVoiceCatalog(voiceCatalog)
        }
    }
}

/**
 * Checking
 * This methods checks if the english voice pack is present on catalog or not
 * @param voiceCatalog
 */
private fun NavigationFragment.checkVoiceCatalog(
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

/**
 * Downloading
 * This methods checks if the voice pack is downloaded or not
 * @param vPackage represents an entry within the voice catalog
 * @param voiceCatalog  used to access voice skin files from the local device
 * @return true if voice id can be found, false otherwise
 */
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