package filemanager.data

import android.graphics.Bitmap
import android.os.Parcelable
import filemanager.utils.FileUtils
import kotlinx.android.parcel.Parcelize

/**
 * Created by Ashok on 2019-12-30.
 */
@Parcelize
data class FileModel(
    var path: String? = null,
    var fileType: FileUtils.FileType,
    var name: String? = null,
    var sizeInMB: Double = 0.0,
    var extension: String = "",
    var subFiles: Int = 0,
    var lastModified : Long = 0,
    var _id : Long = 0,
    var mBitmap : Bitmap? = null,
    var isSelected : Boolean = false
) : Parcelable