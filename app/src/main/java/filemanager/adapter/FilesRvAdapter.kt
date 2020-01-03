package filemanager.adapter

import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import filemanager.data.FileModel
import filemanager.fragment.FileExplorerFragment
import filemanager.utils.FileUtils
import kotlinx.android.synthetic.main.file_list_item.view.*


/**
 * Created by Ashok on 2019-12-30.
 */
class FilesRvAdapter : RecyclerView.Adapter<FilesRvAdapter.ViewHolder>() {

    var onItemClickListener : MainActivity.OnItemClickListener? = null
    var filesList = listOf<FileModel>()
    var isGridLayout = false
    var isLongPressMode = false
    var mSelectedList = HashSet<Int>()


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
            if (fileModel.fileType == FileUtils.FileType.FOLDER) {
                itemView.totalFilesTextView.text = "${fileModel.subFiles} files"
                itemView.folderIv.setImageDrawable(VectorDrawableCompat.create(itemView.resources, R.drawable.ic_folder, itemView.context.getTheme()))
            } else {
                itemView.totalFilesTextView.text = FileUtils.getDate(itemView.context.resources.getString(R.string.date_format1), fileModel.lastModified)
                itemView.folderIv.setImageDrawable(null)
            }
            if (!isLongPressMode) {
                itemView.moreIcon.buttonDrawable = VectorDrawableCompat.create(itemView.resources, R.drawable.ic_more, itemView.context.getTheme())
                val outValue = TypedValue()
                itemView.context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
                itemView.moreIcon.setBackgroundResource(outValue.resourceId)
                itemView.moreIcon.isClickable = true
                itemView.moreIcon.isFocusable = true
                itemView.moreIcon.setOnClickListener{
                    Log.d("clicked", "clicked")
                }
            } else {
                if (fileModel.isSelected) {
                    itemView.moreIcon.buttonDrawable = VectorDrawableCompat.create(itemView.resources, R.drawable.ic_check_box_blue, itemView.context.getTheme())
                } else {
                    itemView.moreIcon.buttonDrawable = VectorDrawableCompat.create(itemView.resources, R.drawable.ic_check_box_blue_uncheck, itemView.context.getTheme())
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
                if (position == filesList.size - 1) {
                    itemView.divider.visibility = View.INVISIBLE
                } else {
                    itemView.divider.visibility = View.VISIBLE
                }
            } else {
                itemView.nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemView.resources.getDimension(R.dimen.sp12))
            }
        }
    }
}