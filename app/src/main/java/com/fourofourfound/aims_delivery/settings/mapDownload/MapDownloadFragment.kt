package com.fourofourfound.aims_delivery.settings.mapDownload

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentMapDownloadBinding
import com.here.android.mpa.common.ApplicationContext
import com.here.android.mpa.common.MapEngine
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.odml.MapLoader
import com.here.android.mpa.odml.MapLoader.ResultCode
import com.here.android.mpa.odml.MapPackage


class MapDownloadFragment : Fragment() {

    lateinit var mapLoader: MapLoader
    lateinit var listAdapter: MapDownloaderAdapter
    lateinit var binding: FragmentMapDownloadBinding
    lateinit var viewModel: MapDownloadViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_map_download, container, false)
        listAdapter =
            MapDownloaderAdapter(CurrentItemClickHandler { state -> onListItemClicked(state) })
        binding.stateRecyclerView.adapter = listAdapter
        viewModel = ViewModelProvider(this).get(MapDownloadViewModel::class.java)
        initMapEngine()

        viewModel.packageList.observe(viewLifecycleOwner) {
            if (it != null) {
                listAdapter.submitList(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.downloadProgressBar.visibility = View.VISIBLE
            } else {
                binding.downloadProgressBar.visibility = View.GONE
            }
        }
        Log.i("AAAAAAAAAAAAAAA", viewModel.packageList.value.toString())

        return binding.root

    }

    private fun initMapEngine() {

        MapEngine.getInstance().init(
            ApplicationContext(requireActivity())
        ) { error ->
            if (error == OnEngineInitListener.Error.NONE)
                getMapPackages()
            else {

                CustomDialogBuilder(
                    requireActivity(),
                    "Error",
                    "Something Went Wrong",
                    "OK",
                    null,
                    null,
                    null,
                    true
                ).builder.show()
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
            binding.downloadProgressBar.progress = p0

        }

        override fun onInstallationSize(l: Long, l1: Long) {}
        override fun onGetMapPackagesComplete(rootMapPackage: MapPackage?, resultCode: ResultCode) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {

                findUsaStates(rootMapPackage)

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
                            requireActivity(),
                            "MapLoader is being busy with other operations",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Starting map update from current version:$current to $update",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireActivity(),
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
                Toast.makeText(requireActivity(), "Map update is completed", Toast.LENGTH_SHORT)
                    .show()
                refreshListView(ArrayList(rootMapPackage!!.children))
            }
        }

        override fun onInstallMapPackagesComplete(
            rootMapPackage: MapPackage?,
            resultCode: ResultCode
        ) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                Toast.makeText(requireActivity(), "Installation is completed", Toast.LENGTH_SHORT)
                    .show()
                findUsaStates(rootMapPackage)
            } else if (resultCode == ResultCode.OPERATION_CANCELLED) Toast.makeText(
                requireActivity(),
                "Installation is cancelled...",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.loading.value = false
        }

        override fun onUninstallMapPackagesComplete(
            rootMapPackage: MapPackage?,
            resultCode: ResultCode
        ) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                Toast.makeText(requireActivity(), "Uninstallation is completed", Toast.LENGTH_SHORT)
                    .show()
                findUsaStates(rootMapPackage)
            } else if (resultCode == ResultCode.OPERATION_CANCELLED) {
                Toast.makeText(
                    requireActivity(),
                    "Uninstallation is cancelled...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            viewModel.loading.value = false
        }
    }

    private fun findUsaStates(rootMapPackage: MapPackage?) {
        for (continent in rootMapPackage!!.children) {
            if (continent.id == 8) {
                for (country in continent.children) {
                    if (country.id === 1000) {
                        refreshListView(ArrayList(country.children))
                    }
                }
            }
        }
    }

    private fun refreshListView(list: ArrayList<MapPackage>) {
        viewModel.setPackageList(list)
    }

    private fun onListItemClicked(clickedMapPackage: MapPackage) {
        val children = clickedMapPackage.children
        if (children.size > 0) {
            refreshListView(ArrayList(children))
        } else {

            val idList: MutableList<Int> = ArrayList()
            idList.add(clickedMapPackage.id)
            if (clickedMapPackage.installationState == MapPackage.InstallationState.INSTALLED) uninstallMapPackage(
                idList
            )
            else installMapPackage(idList, clickedMapPackage)
        }
    }

    private fun installMapPackage(idList: MutableList<Int>, clickedMapPackage: MapPackage) {
        val success: Boolean = mapLoader.installMapPackages(idList)
        if (!success) Toast.makeText(
            requireActivity(),
            "MapLoader is being busy with other operations",
            Toast.LENGTH_SHORT
        ).show()
        else {


            viewModel.loading.value = true
            Toast.makeText(
                requireActivity(),
                "Downloading " + clickedMapPackage.title,
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun uninstallMapPackage(idList: MutableList<Int>) {
        val runUninstalling = {
            viewModel.loading.value = true
            val success: Boolean = mapLoader.uninstallMapPackages(idList)
            if (!success) Toast.makeText(
                requireActivity(),
                "MapLoader is being busy with other operations",
                Toast.LENGTH_SHORT
            ).show()
            else Toast.makeText(requireActivity(), "Uninstalling...", Toast.LENGTH_SHORT).show()
        }

        CustomDialogBuilder(
            requireActivity(),
            "Uninstall Map",
            "Offline routing will not be available for this state",
            "Continue",
            runUninstalling,
            "Cancel",
            null,
            true
        ).builder.show()


    }
}