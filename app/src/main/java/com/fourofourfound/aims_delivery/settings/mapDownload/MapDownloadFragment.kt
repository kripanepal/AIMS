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
import com.here.android.mpa.common.MapSettings
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.odml.MapLoader
import com.here.android.mpa.odml.MapLoader.ResultCode
import com.here.android.mpa.odml.MapPackage
import java.io.File


/**
 * Map download fragment
 * This fragment is responsible for downloading map data.
 * @constructor Create empty Map download fragment
 */
class MapDownloadFragment : Fragment() {

    /**
     * Map loader
     * The MapLoader class provides a set of APIs that allow manipulation of the map data stored on the device.
     */
    lateinit var mapLoader: MapLoader

    /**
     * List adapter
     * This acts as a bridge between an AdapterView and the underlying data for that view.
     */
    lateinit var listAdapter: MapDownloaderAdapter

    /**
     * Binding
     * This allows to write code more easily that interacts with views.
     */
    lateinit var binding: FragmentMapDownloadBinding

    /**
     * View model
     * View Model that contains the information about the map.
     */
    lateinit var viewModel: MapDownloadViewModel

    /**
     * Progress bar
     * A user interface element that indicates the progress of an operation.
     */
    lateinit var progressBar: ProgressBar

    /**
     * Progress text
     * A view that holds the progress text.
     */
    lateinit var progressText: TextView

    /**
     * Dialog
     * The dialog box that is to be shown when the action if triggered.
     */
    lateinit var dialog: AlertDialog

    /**
     * On create view
     * This method initializes the fragment.
     * @param inflater the inflater that is used to inflate the view
     * @param container the container that holds the fragment
     * @param savedInstanceState called when fragment is starting
     * @return the view that is inflated
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //create a binding object
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_map_download, container, false)
        listAdapter =
            MapDownloaderAdapter(CurrentItemClickHandler { state -> onListItemClicked(state) })

        //initialize viewModel and assign value to the viewModel in xml file
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

    /**
     * Observe map downloading state
     * This method shows a progress bar when the map is downloading.
     */
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

    /**
     * Observe toast messages
     * This methods observes the toast message.
     */
    private fun observeToastMessages() {
        viewModel.displayMessages.observe(viewLifecycleOwner)
        {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.displayMessages.value = null
            }
        }
    }

    /**
     * Observe loading state
     * This method makes the loading bar appear and disappear when required.
     */
    private fun observeLoadingState() {
        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) binding.downloadProgressBar.visibility = View.VISIBLE
            else binding.downloadProgressBar.visibility = View.GONE

        }
    }

    /**
     * Observe active package
     * This method observes if the selected package if active or not.
     */
    private fun observeActivePackage() {
        viewModel.packageList.observe(viewLifecycleOwner) {
            if (it != null) listAdapter.submitList(it.toList())

        }
    }

    /**
     * Build download dialog
     * This method builds the download dialog.
     */
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

    /**
     * Init map engine
     * This method initializes the here map engine.
     */
    private fun initMapEngine() {
        val path: String = File(requireActivity().getExternalFilesDir(null), ".here-map-data")
            .absolutePath
        MapSettings.setDiskCacheRootPath(path)
        MapEngine.getInstance().init(
            ApplicationContext(requireActivity())
        ) { error ->
            if (error == OnEngineInitListener.Error.NONE) getMapPackages()
            else viewModel.displayMessages.value = "Something went wrong"
        }
    }

    /**
     * Get map packages
     * This method gets the map package that are available for download.
     */
    private fun getMapPackages() {
        mapLoader = MapLoader.getInstance()
        mapLoader.addListener(listener)
        mapLoader.mapPackages
    }

    /**
     * Listener
     * This method tracks the state of each map data and allows to
     * install and uninstall map data.
     */
    private val listener: MapLoader.Listener = object : MapLoader.Listener {

        /**
         * On progress
         * This method sets how much percentage download has completed to view model which is used in
         * UI.
         * @param p0 the map download percentage
         */
        override fun onProgress(p0: Int) {
            viewModel.mapDownloadingPercentage.value = p0
        }

        override fun onInstallationSize(l: Long, l1: Long) {}

        /**
         * On get map packages complete
         * This method is called when the list of map packages are available.
         * @param rootMapPackage root map package
         * @param resultCode result code to check if operation is successful or busy
         */
        override fun onGetMapPackagesComplete(rootMapPackage: MapPackage?, resultCode: ResultCode) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) findUsaStates(rootMapPackage)
            else if (resultCode == ResultCode.OPERATION_BUSY) mapLoader.mapPackages
        }

        /**
         * On check for update complete
         * This method checks if the new version of the map is available or not and updates
         * to the latest version if available
         * @param updateAvailable check if there is new map available
         * @param current current version of the map
         * @param update update version of the map
         * @param resultCode result code to check if operation is successful or busy
         */
        override fun onCheckForUpdateComplete(
            updateAvailable: Boolean, current: String?, update: String?,
            resultCode: ResultCode
        ) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                if (updateAvailable) {
                    val success: Boolean = mapLoader.performMapDataUpdate()
                    if (!success) viewModel.displayMessages.value =
                        "MapLoader is being busy with other operations"
                    else viewModel.displayMessages.value =
                        "Starting map update from current version:$current to $update"
                } else viewModel.displayMessages.value == "Current map version: $current is the latest"
            } else if (resultCode == ResultCode.OPERATION_BUSY) mapLoader.checkForMapDataUpdate()
        }

        /**
         * On perform map data update complete
         * This method is called when map data update is completed.
         * @param rootMapPackage root map package
         * @param resultCode result code to check if operation is successful or busy
         */
        override fun onPerformMapDataUpdateComplete(
            rootMapPackage: MapPackage?,
            resultCode: ResultCode
        ) {
            if (resultCode == ResultCode.OPERATION_SUCCESSFUL) {
                viewModel.displayMessages.value = "Map update is completed"
                refreshListView(ArrayList(rootMapPackage!!.children))
            }
        }

        /**
         * On install map packages complete
         * This method is is called when map package installation is completed.
         * @param rootMapPackage root map package
         * @param resultCode result code to check if operation is successful or busy
         */
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

        /**
         * On uninstall map packages complete
         * This method is is called when map package un-installation is completed.
         * @param rootMapPackage root map package
         * @param resultCode result code to check if operation is successful or busy
         */
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

    /**
     * Find usa states
     * This method gives the list of U.S states
     * @param rootMapPackage root map package
     */
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

    /**
     * Refresh list view
     * This method loads the updated list in the recycler view after map download is finished.
     * @param list list of the map data
     */
    private fun refreshListView(list: ArrayList<MapPackage>) {
        viewModel.loading.value = false
        viewModel.setPackageList(list)
    }

    /**
     * On list item clicked
     * This method downloads the desired map and uninstalls it if
     * one is already installed.
     * @param clickedMapPackage map package to install or uninstall
     */
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

    /**
     * Install map package
     * This method installs the map package.
     * @param idList list of map packages.
     * @param englishTitle the name of the currently downloading state
     */
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

    /**
     * Uninstall map package
     * This methods uninstalls the map package.
     * @param idList list of map packages.
     */
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