package filemanager.utils

import android.text.format.DateFormat
import filemanager.data.FileModel
import java.io.File
import java.util.*

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
    }


    enum class FileType {
        FILE,
        FOLDER;

        companion object {
            fun getFileType(file: File) = when (file.isDirectory) {
                true -> FOLDER
                false -> FILE
            }
        }
    }
}