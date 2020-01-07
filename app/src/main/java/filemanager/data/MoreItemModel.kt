package filemanager.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Ashok on 2020-01-07.
 */
@Parcelize
data class MoreItemModel(
    var iconName: String? = null,
    var desc: String? = null
) : Parcelable