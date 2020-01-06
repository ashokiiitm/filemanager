package filemanager.utils

import android.content.Context
import android.database.Cursor
import android.text.format.DateFormat
import filemanager.data.FileModel
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import android.provider.MediaStore

/**
 * Created by Ashok on 2019-12-30.
 */
class FileUtils {
    companion object {
        fun getFiles(path: String, showHiddenFiles: Boolean = false, onlyFolders: Boolean = false): List<File> {
            val file = File(path)
            return file.listFiles()
                .filter { showHiddenFiles || !it.name.startsWith(".") }
                .filter { !onlyFolders || it.isDirectory }
                .toList()
        }
        fun convertFileToModel(files: List<File>): List<FileModel> {
            return files.map {
                FileModel(it.path, FileType.getFileType(it), it.name, convertFileSizeToMB(it.length()), it.extension, it.listFiles()?.size
                    ?: 0, it.lastModified())
            }
        }
        fun convertFileSizeToMB(sizeInBytes: Long): Double {
            return (sizeInBytes.toDouble()) / (1024 * 1024)
        }
        fun getDate(format: String, timestamp: Long): String {
            if (timestamp == 0L) return ""
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp

            return DateFormat.format(format, cal).toString()
        }
        fun getMediaFiles(context : Context, type : String) : List<FileModel> {
            var list : ArrayList<FileModel> = ArrayList()
            when(type) {
                MediaType.ROOT.name -> {
                    var cursor : Cursor? = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                        null, null, null)
                    var imagescount = 0
                    if (cursor != null) {
                        imagescount = cursor.count
                    }
                    var imagesize = ""
                    var totalimagesize = 0.0
                    while (cursor != null && cursor.moveToNext()) {
                        imagesize = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                        totalimagesize += imagesize.toInt()
                    }
                    totalimagesize/=10000000
                    cursor?.close()
                    cursor = context.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                        null, null, null)
                    var videocount = 0
                    if (cursor != null) {
                        videocount = cursor.count
                    }
                    var videosize = ""
                    var totalvideosize = 0.0
                    while (cursor != null && cursor.moveToNext()) {
                        videosize = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                        totalvideosize += videosize.toInt()
                    }
                    totalvideosize/=10000000
                    cursor?.close()
                    cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                        null, null, null)
                    var audiocount = 0
                    if (cursor != null) {
                        audiocount = cursor.count
                    }
                    var audiosize = ""
                    var totalaudiosize = 0.0
                    while (cursor != null && cursor.moveToNext()) {
                        audiosize = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                        totalaudiosize += audiosize.toInt()
                    }
                    totalaudiosize/=10000000
                    cursor?.close()
                    list.add(FileModel("", FileType.ROOT_AUDIO, audioFolderDisplayName, totalaudiosize, "", audiocount, 0))
                    list.add(FileModel("", FileType.ROOT_IMAGE, imageFolderDisplayName, totalimagesize, "", imagescount, 0))
                    list.add(FileModel("", FileType.ROOT_VIDEO, videoFolderDisplayName, totalvideosize, "", videocount, 0))
                    return list
                }
                MediaType.IMAGE.name -> {
                    var cursor : Cursor? = null
                    try {
                        cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            null, null, null, null)
                        val size = cursor.getCount()
                        if (size != 0) {
                            while (cursor.moveToNext()) {
                                val file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                                val path = cursor.getString(file_ColumnIndex)
                                val fileName = path.substring(path.lastIndexOf("/") + 1, path.length)
                                val filesize = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                                val lastmodified = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
                                val _id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                                list.add(FileModel(path, FileType.IMAGE, fileName, filesize.toDouble()/1000000, "", 0, lastmodified*1000, _id))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        cursor?.close()
                    }
                    return list
                }
                MediaType.VIDEO.name -> {
                    var cursor : Cursor? = null
                    try {
                        cursor = context.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            null, null, null, null)
                        val size = cursor.getCount()
                        if (size != 0) {
                            val thumbID = 0
                            while (cursor.moveToNext()) {
                                val file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                                val path = cursor.getString(file_ColumnIndex)
                                val fileName = path.substring(path.lastIndexOf("/") + 1, path.length)
                                val filesize = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                                val lastmodified = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED))
                                list.add(FileModel(path, FileType.VIDEO, fileName, filesize.toDouble()/1000000, "", 0, lastmodified*1000))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        cursor?.close()
                    }
                    return list
                }
                MediaType.AUDIO.name -> {
                    var cursor : Cursor? = null
                    try {
                        cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            null, null, null, null)
                        val size = cursor.getCount()
                        if (size != 0) {
                            val thumbID = 0
                            while (cursor.moveToNext()) {
                                val file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                                val path = cursor.getString(file_ColumnIndex)
                                val fileName = path.substring(path.lastIndexOf("/") + 1, path.length)
                                val filesize = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                                val lastmodified = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED))
                                list.add(FileModel(path, FileType.AUDIO, fileName, filesize.toDouble()/1000000, "", 0, lastmodified*1000))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        cursor?.close()
                    }
                    return list
                }
                else -> {
                    var list : ArrayList<FileModel> = ArrayList()
                    return list
                }
            }
        }
        var audioFolderDisplayName = "Music"
        var imageFolderDisplayName = "Photos"
        var videoFolderDisplayName = "Videos"
    }


    enum class FileType {
        FILE,
        FOLDER,
        ROOT_IMAGE,
        ROOT_VIDEO,
        ROOT_AUDIO,
        IMAGE,
        VIDEO,
        AUDIO;

        companion object {
            fun getFileType(file: File) = when (file.isDirectory) {
                true -> FOLDER
                false -> FILE
            }
        }
    }

    enum class MediaType {
        IMAGE,
        VIDEO,
        AUDIO,
        ROOT;
    }
}