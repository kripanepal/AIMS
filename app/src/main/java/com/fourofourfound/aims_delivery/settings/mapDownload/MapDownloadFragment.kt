package com.fourofourfound.aims_delivery.settings.mapDownload

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
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
    lateinit var progressBar: ProgressBar
    lateinit var progressText: TextView
    lateinit var dialog: AlertDialog
    lateinit var overlayDialog: AlertDialog

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
        buildDownloadDialog()
        observeActivePackage()
        observeLoadingState()
        observeToastMessages()
        observeMapDownloadingState()

        return binding.root
    }

    private fun observeMapDownloadingState() {
        viewModel.mapDownloading.observe(viewLifecycleOwner) {
            if (it) {
                dialog.setMessage(viewModel.currentlyDownloadingState)
                dialog.show()
                binding.downloadProgressBar.visibility = View.GONE
                progressBar = dialog.requireViewById(R.id.mapDownloadingProgressBar)
                progressText = dialog.findViewById(R.id.mapDownloadingText)
                viewModel.mapDownloadingPercentage.observe(viewLifecycleOwner)
                { progress ->
                    progressBar.progress = progress
                    progressText.text = getString(R.string.text_with_percentage, progress)
                }
            }
        }
    }

    private fun observeToastMessages() {
        viewModel.displayMessages.observe(viewLifecycleOwner)
        {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.displayMessages.value = null
            }
        }
    }

    private fun observeLoadingState() {
        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) binding.downloadProgressBar.visibility = View.VISIBLE
            else binding.downloadProgressBar.visibility = View.GONE

        }
    }

    private fun observeActivePackage() {
        viewModel.packageList.observe(viewLifecycleOwner) {
            if (it != null) listAdapter.submitList(it.toList())

        }
    }

    private fun buildDownloadDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.map_downloading_dialog)
        builder.setPositiveButton("Cancel") { _, _ ->
            MapLoader.getInstance().cancelCurrentOperation()
            viewModel.loading.value = true
        }
        builder.setNegativeButton("Hide") { _, _ -> viewModel.loading.value = true }
        dialog = builder.create()
        dialog.setTitle("Downloading")
    }

    private fun initMapEngine() {
        MapEngine.getInstance().init(
            ApplicationContext(requireActivity())
        ) { error ->
            if (error == OnEngineInitListener.Error.NONE) getMapPackages()
            else viewModel.displayMessages.value = "Something went wrong"
        }
    }

    private fun getMapPackages() {
        mapLoader = MapLoader.getInstance()
        mapLoader.addListener(listener)
        mapLoader.mapPackages
    }


    private val listener: MapLoader.Listener = object : MapLoader.Listener {
        override fun onProgress(p0: Int) {
            viewModel.mapDownloadingPercentage.value = p0
        }

        override fun onInstallationSize(l: Long, l1: Long) {}
        override fun onGetMapPackagesComplete(rootMapPackage: MapPackage?, resultCode: ResultCode) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) findUsaStates(rootMapPackage)
            else if (resultCode == ResultCode.OPERATION_BUSY) mapLoader.mapPackages

        }

        override fun onCheckForUpdateComplete(
            updateAvailable: Boolean, current: String?, update: String?,
            resultCode: ResultCode
        ) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                if (updateAvailable) {
                    // Update the map if there is a new version available
                    val success: Boolean = mapLoader.performMapDataUpdate()
                    if (!success) viewModel.displayMessages.value =
                        "MapLoader is being busy with other operations"
                    else viewModel.displayMessages.value =
                        "Starting map update from current version:$current to $update"

                } else viewModel.displayMessages.value == "Current map version: $current is the latest"

            } else if (resultCode == ResultCode.OPERATION_BUSY) mapLoader.checkForMapDataUpdate()

        }

        override fun onPerformMapDataUpdateComplete(
            rootMapPackage: MapPackage?,
            resultCode: ResultCode
        ) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                viewModel.displayMessages.value = "Map update is completed"
                refreshListView(ArrayList(rootMapPackage!!.children))
            }
        }

        override fun onInstallMapPackagesComplete(
            rootMapPackage: MapPackage?,
            resultCode: ResultCode
        ) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                viewModel.displayMessages.value = "Installation is completed"
                findUsaStates(rootMapPackage)

            } else if (resultCode == ResultCode.OPERATION_CANCELLED)
                viewModel.displayMessages.value = "Installation is cancelled..."
            viewModel.loading.value = false
            viewModel.mapDownloading.value = false
            viewModel.mapDownloadingPercentage.value = 0
            dialog.hide()
        }

        override fun onUninstallMapPackagesComplete(
            rootMapPackage: MapPackage?,
            resultCode: ResultCode
        ) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                viewModel.displayMessages.value = "Uninstallation is completed"
                findUsaStates(rootMapPackage)
            } else if (resultCode == ResultCode.OPERATION_CANCELLED) viewModel.displayMessages.value =
                "Uninstallation is cancelled..."

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
        viewModel.loading.value = false
        viewModel.setPackageList(list)
    }

    private fun onListItemClicked(clickedMapPackage: MapPackage) {
        if (!viewModel.loading.value!!) {
            val children = clickedMapPackage.children
            if (children.size > 0) {
                refreshListView(ArrayList(children))
            } else {
                val idList: MutableList<Int> = ArrayList()
                idList.add(clickedMapPackage.id)
                if (clickedMapPackage.installationState == MapPackage.InstallationState.INSTALLED) {
                    uninstallMapPackage(idList)
                } else {
                    installMapPackage(idList, clickedMapPackage.englishTitle)
                }
            }
        }
    }

    private fun installMapPackage(idList: MutableList<Int>, englishTitle: String?) {
        val success: Boolean = mapLoader.installMapPackages(idList)
        if (!success) viewModel.displayMessages.value =
            "MapLoader is being busy with other operations"
        else {
            viewModel.currentlyDownloadingState = englishTitle.toString()
            viewModel.mapDownloading.value = true
            viewModel.displayMessages.value = "Downloading "
        }

    }

    private fun uninstallMapPackage(idList: MutableList<Int>) {
        val runUninstalling = {
            viewModel.loading.value = true
            val success: Boolean = mapLoader.uninstallMapPackages(idList)
            if (!success)
                viewModel.displayMessages.value = "MapLoader is being busy with other operations"
            else viewModel.displayMessages.value = "Uninstalling..."
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