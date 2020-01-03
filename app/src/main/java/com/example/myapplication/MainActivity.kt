package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import filemanager.data.FileModel
import filemanager.fragment.FileExplorerFragment
import filemanager.fragment.FileExplorerFragment.Companion.ARG_PATH
import filemanager.utils.PermissionManager
import filemanager.viewmodel.FileViewModel

import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    interface OnItemClickListener {
        fun onClick(position : Int, fileModel: FileModel)

        fun onLongClick(position : Int, fileModel: FileModel)
    }

    val mPermissionManager = PermissionManager.instance
    var mViewModel : FileViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mViewModel = ViewModelProviders.of(this).get(FileViewModel::class.java)
        val permissionList = ArrayList<String>(1)
        var isPermissionGranted = true
        if (!mPermissionManager.havePermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, null)) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            isPermissionGranted = false
            Log.d("permissions", "has permission false")
        }
        if (permissionList.size > 0) {
            var strings = permissionList.toTypedArray()
            mPermissionManager.requestMultiplePermissions(
                this,
                object : PermissionManager.PermissionGrantListener {
                    override fun onPermissionDenied() {
                        Log.d("permissions", "onPermissionDenied")
                        showPermissionErrorView()
                    }

                    override fun onPermissionReceived() {
                        Log.d("permissions", "onPermissionReceived")
                        showMainView()
                        initFragment()
                    }
                },
                *strings
            )
        }
        if (!isPermissionGranted) {
            //showPermissionErrorView()
        } else {
            showMainView()
            initFragment()
        }
    }

    fun initFragment() {
        val fileexpfrag = FileExplorerFragment()
        val args = Bundle()
        args.putString(ARG_PATH, Environment.getExternalStorageDirectory().absolutePath)
        fileexpfrag.arguments = args
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, fileexpfrag, fileexpfrag.javaClass.simpleName)
            .addToBackStack(fileexpfrag.javaClass.simpleName)
            .commit()
    }

    fun showPermissionErrorView() {
        fragmentContainerError.visibility = View.VISIBLE
        fragmentContainer.visibility = View.GONE
    }

    fun showMainView() {
        fragmentContainerError.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        mPermissionManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        if (getActiveFragment() is FileExplorerFragment) {
            (getActiveFragment() as FileExplorerFragment).onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    fun getActiveFragment(): Fragment? {
        if (supportFragmentManager.backStackEntryCount == 0) {
            return null
        }
        val tag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
        return supportFragmentManager.findFragmentByTag(tag)
    }
}
