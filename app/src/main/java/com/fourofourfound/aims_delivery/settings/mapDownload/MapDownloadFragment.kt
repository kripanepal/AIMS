package com.fourofourfound.aims_delivery.settings.mapDownload

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentMapDownloadBinding
import com.here.android.mpa.common.ApplicationContext
import com.here.android.mpa.common.MapEngine
import com.here.android.mpa.common.MapSettings
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.odml.MapLoader
import com.here.android.mpa.odml.MapLoader.ResultCode
import com.here.android.mpa.odml.MapPackage
import java.io.File
import java.util.*

class MapDownloadFragment : Fragment() {


    lateinit var mapLoader: MapLoader
    lateinit var listAdapter: MapDownloaderAdapter
    lateinit var currentMapPackageList: List<MapPackage>
    lateinit var binding: FragmentMapDownloadBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_map_download, container, false
        )
        listAdapter = MapDownloaderAdapter(StateClickHandler { state ->
            //TODO handle item click
        })
        binding.stateRecyclerView.adapter = listAdapter

        initMapEngine()

        return binding.root

    }

    private fun initMapEngine() {

        val path: String = File(requireActivity().getExternalFilesDir(null), ".here-map-data")
            .absolutePath
        MapSettings.setDiskCacheRootPath(path)
        MapEngine.getInstance().init(
            ApplicationContext(requireContext())
        ) { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                getMapPackages()
            } else {
                AlertDialog.Builder(requireContext())
                    .setMessage("""Error : ${error.name}${error.details}""".trimIndent())
                    .setTitle("Okay")
                    .setNegativeButton(android.R.string.cancel) { dialog, which -> dialog.cancel() }
                    .create().show()
            }
        }
    }

    private fun getMapPackages() {
        mapLoader = MapLoader.getInstance()
        mapLoader.addListener(listener)
        mapLoader.mapPackages
    }


    private val listener: MapLoader.Listener = object : MapLoader.Listener {
        override fun onProgress(p0: Int) {
        }

        override fun onInstallationSize(l: Long, l1: Long) {}
        override fun onGetMapPackagesComplete(rootMapPackage: MapPackage?, resultCode: ResultCode) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                val children = rootMapPackage!!.children


                refreshListView(ArrayList(children))
            } else if (resultCode == ResultCode.OPERATION_BUSY) {
                mapLoader.mapPackages
            }
        }

        override fun onCheckForUpdateComplete(
            updateAvailable: Boolean, current: String?, update: String?,
            resultCode: ResultCode
        ) {

            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                if (updateAvailable) {
                    // Update the map if there is a new version available
                    val success: Boolean = mapLoader.performMapDataUpdate()
                    if (!success) {
                        Toast.makeText(
                            requireContext(),
                            "MapLoader is being busy with other operations",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Starting map update from current version:$current to $update",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Current map version: $current is the latest",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (resultCode == ResultCode.OPERATION_BUSY) {
                mapLoader.checkForMapDataUpdate()
            }
        }

        override fun onPerformMapDataUpdateComplete(
            rootMapPackage: MapPackage?,
            resultCode: ResultCode
        ) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                Toast.makeText(requireContext(), "Map update is completed", Toast.LENGTH_SHORT)
                    .show()
                refreshListView(ArrayList(rootMapPackage!!.children))
            }
        }

        override fun onInstallMapPackagesComplete(
            rootMapPackage: MapPackage?,
            resultCode: ResultCode
        ) {

            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                Toast.makeText(requireContext(), "Installation is completed", Toast.LENGTH_SHORT)
                    .show()
                val children = rootMapPackage!!.children
                refreshListView(ArrayList(children))
            } else if (resultCode == ResultCode.OPERATION_CANCELLED) {
                Toast.makeText(requireContext(), "Installation is cancelled...", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onUninstallMapPackagesComplete(
            rootMapPackage: MapPackage?,
            resultCode: ResultCode
        ) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                Toast.makeText(requireContext(), "Uninstallation is completed", Toast.LENGTH_SHORT)
                    .show()
                val children = rootMapPackage!!.children
                refreshListView(ArrayList(children))
            } else if (resultCode == ResultCode.OPERATION_CANCELLED) {
                Toast.makeText(
                    requireContext(),
                    "Uninstallation is cancelled...",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun refreshListView(list: ArrayList<MapPackage>) {

        var toSubmit: List<String> =
            list.map {
                it.englishTitle.toString()
            }
        Log.i("AAAAAAAAAAAAAAA", toSubmit.toString())
        listAdapter.submitList(toSubmit)
        currentMapPackageList = list
    }

    // Handles the click action on map list item.
    fun onListItemClicked(l: ListView?, v: View?, position: Int, id: Long) {
        val clickedMapPackage: MapPackage = currentMapPackageList[position]
        val children = clickedMapPackage.children
        if (children.size > 0) {
            // Children map packages exist.Show them on the screen.
            refreshListView(ArrayList(children))
        } else {
            /*
             * No children map packages are available, we should perform downloading or
             * un-installation action.
             */
            val idList: MutableList<Int> = ArrayList()
            idList.add(clickedMapPackage.id)
            if (clickedMapPackage
                    .installationState == MapPackage.InstallationState.INSTALLED
            ) {
                val success: Boolean = mapLoader.uninstallMapPackages(idList)
                if (!success) {
                    Toast.makeText(
                        requireContext(), "MapLoader is being busy with other operations",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "Uninstalling...", Toast.LENGTH_SHORT).show()
                }
            } else {
                val success: Boolean = mapLoader.installMapPackages(idList)
                if (!success) {
                    Toast.makeText(
                        requireContext(), "MapLoader is being busy with other operations",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(), "Downloading " + clickedMapPackage.title,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}