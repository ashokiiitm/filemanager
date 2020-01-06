package filemanager.adapter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import filemanager.data.FileModel
import filemanager.utils.FileUtils
import kotlinx.android.synthetic.main.file_list_item.view.*
import android.provider.MediaStore

/**
 * Created by Ashok on 2019-12-30.
 */
class FilesRvAdapter(mContext : Context) : RecyclerView.Adapter<FilesRvAdapter.ViewHolder>() {

    var onItemClickListener : MainActivity.OnItemClickListener? = null
    var filesList = listOf<FileModel>()
    var isGridLayout = false
    var isLongPressMode = false
    var mSelectedList = HashSet<Int>()
    var folderDrawable : VectorDrawableCompat?
    var rootImageDrawable : VectorDrawableCompat?
    var rootVideoDrawable : VectorDrawableCompat?
    var rootAudioDrawable : VectorDrawableCompat?
    var moreDrawable : VectorDrawableCompat?
    var checkBoxDrawable : VectorDrawableCompat?
    var checkBoxUncheckDrawable : VectorDrawableCompat?
    var defaultImageFrame : VectorDrawableCompat?
    var defaultVideoFrame : VectorDrawableCompat?
    var defaultAudioFrame : VectorDrawableCompat?

    init {
        folderDrawable = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_folder, mContext.getTheme())
        rootImageDrawable = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_photos_folder, mContext.getTheme())
        rootVideoDrawable = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_videos_folder, mContext.getTheme())
        rootAudioDrawable = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_music_folder, mContext.getTheme())
        moreDrawable = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_more, mContext.getTheme())
        checkBoxDrawable = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_check_box_blue, mContext.getTheme())
        checkBoxUncheckDrawable = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_check_box_blue_uncheck, mContext.getTheme())
        defaultImageFrame = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_photoframe, mContext.getTheme())
        defaultVideoFrame = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_videoframe, mContext.getTheme())
        defaultAudioFrame = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_musicframe, mContext.getTheme())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View? = null
        if (viewType == 1) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.file_list_item_grid, parent, false)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.file_list_item, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount() = filesList.size

    override fun getItemViewType(position: Int): Int {
        if (isGridLayout) {
            return 1
        } else {
            return 2
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindView(position)

    fun updateData(filesList: List<FileModel>) {
        this.filesList = filesList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemClickListener?.onClick(adapterPosition, filesList[adapterPosition])
        }

        override fun onLongClick(v: View?): Boolean {
            onItemClickListener?.onLongClick(adapterPosition, filesList[adapterPosition])
            return true
        }

        fun bindView(position: Int) {
            val fileModel = filesList[position]
            itemView.nameTextView.text = fileModel.name
            if (fileModel.fileType == FileUtils.FileType.ROOT_IMAGE) {
                itemView.totalFilesTextView.text = "${fileModel.subFiles} files"
                itemView.folderIv.setImageDrawable(rootImageDrawable)
            } else if (fileModel.fileType == FileUtils.FileType.ROOT_VIDEO) {
                itemView.totalFilesTextView.text = "${fileModel.subFiles} files"
                itemView.folderIv.setImageDrawable(rootVideoDrawable)
            } else if (fileModel.fileType == FileUtils.FileType.ROOT_AUDIO) {
                itemView.totalFilesTextView.text = "${fileModel.subFiles} files"
                itemView.folderIv.setImageDrawable(rootAudioDrawable)
            } else if (fileModel.fileType == FileUtils.FileType.IMAGE){
                itemView.totalFilesTextView.text = FileUtils.getDate(itemView.context.resources.getString(R.string.date_format1), fileModel.lastModified)
                if (fileModel.mBitmap == null) {
                    fileModel.mBitmap = MediaStore.Images.Thumbnails.getThumbnail(itemView.context.contentResolver,
                        fileModel._id, MediaStore.Images.Thumbnails.MICRO_KIND, null)
                }
                if (fileModel.mBitmap != null) {
                    itemView.folderIv.setImageBitmap(fileModel.mBitmap)
                } else {
                    itemView.folderIv.setImageDrawable(defaultImageFrame)
                }
            } else if (fileModel.fileType == FileUtils.FileType.VIDEO){
                itemView.totalFilesTextView.text = FileUtils.getDate(itemView.context.resources.getString(R.string.date_format1), fileModel.lastModified)
                if (fileModel.mBitmap == null) {
                    fileModel.mBitmap = MediaStore.Video.Thumbnails.getThumbnail(itemView.context.contentResolver,
                        fileModel._id, MediaStore.Video.Thumbnails.MICRO_KIND, null)
                }
                if (fileModel.mBitmap != null) {
                    itemView.folderIv.setImageBitmap(fileModel.mBitmap)
                } else {
                    itemView.folderIv.setImageDrawable(defaultVideoFrame)
                }
            } else if (fileModel.fileType == FileUtils.FileType.AUDIO){
                itemView.totalFilesTextView.text = FileUtils.getDate(itemView.context.resources.getString(R.string.date_format1), fileModel.lastModified)
                itemView.folderIv.setImageDrawable(defaultAudioFrame)
            } else {
                itemView.totalFilesTextView.text = null
                itemView.folderIv.setImageDrawable(null)
            }
            if (!isLongPressMode) {
                itemView.moreIcon.buttonDrawable = moreDrawable
                val outValue = TypedValue()
                itemView.context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
                itemView.moreIcon.setBackgroundResource(outValue.resourceId)
                itemView.moreIcon.isClickable = true
                itemView.moreIcon.isFocusable = true
                itemView.moreIcon.setOnClickListener{
                    //todo
                }
            } else {
                if (fileModel.isSelected) {
                    itemView.moreIcon.buttonDrawable = checkBoxDrawable
                } else {
                    itemView.moreIcon.buttonDrawable = checkBoxUncheckDrawable
                }
                itemView.moreIcon.background = null
                itemView.moreIcon.isClickable = false
                itemView.moreIcon.isFocusable = false
                itemView.moreIcon.setOnClickListener{
                    var parent : View?
                    if (isGridLayout) {
                        parent = it.parent.parent as View
                    } else {
                        parent = it.parent as View
                    }
                    parent.performClick()
                }
            }
            if (!isGridLayout) {
                itemView.totalSizeTextView.text = "${String.format("%.2f", fileModel.sizeInMB)} MB"
                itemView.nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemView.resources.getDimension(R.dimen.sp14))
                itemView.divider.visibility = View.VISIBLE
            } else {
                itemView.nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemView.resources.getDimension(R.dimen.sp12))
            }
        }
    }
}