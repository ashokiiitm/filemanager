package filemanager.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

import androidx.core.app.ActivityCompat

import com.example.myapplication.R

import java.util.HashMap

/**
 * Created by Ashish Singh on 9/14/16.
 */
class PermissionManager {
    /**
     * this map for
     */
    private val permissionListenerHashMap = HashMap<Int, PermissionGrantListenerParent?>()
    private var count = 0x0001

    /**
     * @param activity
     * @param permission
     * @param listener
     * @return
     */
    fun havePermission(
        activity: Context?,
        permission: String?,
        listener: PermissionGrantListenerParent?
    ): Boolean {
        if (activity == null || permission == null || permission.isEmpty())
            return false

        if (!hasSelfPermission(activity, permission)) {
            if (activity is Activity)
                requestPermission(activity, permission, listener)
        } else {
            return true
        }
        return false
    }

    fun requestMultiplePermissions(
        activity: Activity,
        listener: PermissionGrantListenerParent?,
        vararg permissions: String
    ) {
        requestMultiplePermissions(
            activity,
            listener,
            R.string.allowing_this_permission_will_help,
            *permissions
        )
    }

    fun requestMultiplePermissions(
        activity: Activity,
        listener: PermissionGrantListenerParent?,
        messageId: Int,
        vararg permissions: String
    ) {
        val requestCode = ++count
        permissionListenerHashMap[requestCode] = listener
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(activity.applicationContext.getString(messageId))
            builder.setPositiveButton(activity.applicationContext.getString(R.string.ok)) { dialog, which ->
                dialog?.dismiss()
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
            }
            val alertDialog = builder.create()
            alertDialog.setCancelable(true)
            alertDialog.setOnCancelListener {
                if (listener is PermissionGrantListenerNeverAgain)
                    listener.onPermissionDenied()
            }
            alertDialog.show()
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }

    private fun requestPermission(
        activity: Activity,
        permission: String,
        listener: PermissionGrantListenerParent?
    ) {
        requestMultiplePermissions(activity, listener, *arrayOf(permission))
    }

    fun showSettings(activity: Activity, requestCode: Int, messageID: Int) {
        val listener = permissionListenerHashMap[requestCode]
        val builder = AlertDialog.Builder(activity.applicationContext)
        builder.setMessage(activity.applicationContext.getString(messageID))
        builder.setPositiveButton(activity.applicationContext.getString(R.string.ok)) { dialog, which ->
            dialog?.dismiss()
            callPermissionSettings(activity, requestCode)
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.setOnCancelListener { listener?.onPermissionDenied() }
        alertDialog.show()
    }

    private fun callPermissionSettings(activity: Activity, requestcode: Int) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivityForResult(intent, requestcode)
    }

    //TODO : In All permissions received/denied, send array of permissions to take individual actions
    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        val listener = permissionListenerHashMap[requestCode] ?: return false
        if (grantResults.size == permissions.size) {

            var allPermissionsGiven = true
            var isDeniedNeverAgain = false
            var i = 0
            while (i < grantResults.size) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGiven = false
                }
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        permissions[i]
                    ) && grantResults[i] != PackageManager.PERMISSION_GRANTED
                    && listener is PermissionGrantListenerNeverAgain
                ) {
                    isDeniedNeverAgain = true
                    allPermissionsGiven = false
                    break
                }
                i++
            }
            if (allPermissionsGiven) {
                listener.onPermissionReceived()
            } else {
                if (isDeniedNeverAgain && listener is PermissionGrantListenerNeverAgain) {
                    listener.onPermissionNeverAgain(requestCode)
                } else {
                    listener.onPermissionDenied()
                }
            }

        } else {
            var i = 0
            var isDenied = false
            while (i < permissions.size) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        permissions[i]
                    )
                ) {
                    isDenied = true
                    break
                }
                i++
            }
            if (isDenied && listener is PermissionGrantListenerNeverAgain) {
                listener.onPermissionNeverAgain(requestCode)
            } else {
                listener.onPermissionDenied()
            }
        }
        permissionListenerHashMap.remove(requestCode)

        return true
    }

    /**
     * This Interface use for call back when permission grant
     */
    interface PermissionGrantListenerParent {
        fun onPermissionReceived()
        fun onPermissionDenied()
    }

    interface PermissionGrantListener : PermissionGrantListenerParent
    interface PermissionGrantListenerNeverAgain : PermissionGrantListenerParent {
        fun onPermissionNeverAgain(requestCode: Int)
    }

    companion object {

        /**
         * this class
         */

        val instance = PermissionManager()

        private fun hasSelfPermission(activity: Context, permission: String): Boolean {
            try {
                return ActivityCompat.checkSelfPermission(
                    activity,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            } catch (e: RuntimeException) {
                return false
            }

        }

        fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            //todo cannot check multiple permission
            if (context != null && permissions != null) {
                for (permission in permissions) {
                    if (!hasSelfPermission(context, permission)) {
                        return false
                    }
                }
            }
            return true
        }
    }

}
