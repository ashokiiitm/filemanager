package filemanager.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.android.material.snackbar.Snackbar
import filemanager.adapter.FilesRvAdapter
import filemanager.data.FileModel
import filemanager.utils.FileUtils
import filemanager.viewmodel.FileViewModel
import kotlinx.android.synthetic.main.fragment_file_explorer.*
import java.util.*
import android.widget.TextView
import filemanager.utils.FileUtils.Companion.getMediaFiles
import java.util.concurrent.Executors


/**
 * Created by Ashok on 2019-12-30.
 */
class FileExplorerFragment : Fragment(), MainActivity.OnItemClickListener {

    private lateinit var mFilesRvAdapter: FilesRvAdapter
    private lateinit var PATH: String
    var mViewModel : FileViewModel? = null
    var mGridLayoutManager = GridLayoutManager(context, 3)
    var mLinearLayoutManager = LinearLayoutManager(context)
    var persistentSnackBar: Snackbar? = null
    var executorService = Executors.newSingleThreadExecutor()
    var mHandler = Handler()
    lateinit var mProgressDialog : ProgressDialog

    companion object {
        const val ARG_PATH: String = "file_path"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_file_explorer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity == null) {
            return
        }
        mProgressDialog = ProgressDialog(activity as Activity)
        mViewModel = ViewModelProviders.of(requireActivity()).get(FileViewModel::class.java)
        val filePath = arguments?.getString(ARG_PATH)
        if (filePath == null) {
            Toast.makeText(context, "Path should not be null!", Toast.LENGTH_SHORT).show()
            return
        }
        filesListRv.layoutManager = mLinearLayoutManager
        mFilesRvAdapter = FilesRvAdapter(activity as Activity)
        mFilesRvAdapter.onItemClickListener = this
        filesListRv.adapter = mFilesRvAdapter
        PATH = filePath
        mViewModel?.mPathStack?.value = Stack()
        mViewModel?.mPathStack?.value?.push(PATH)
        mViewModel?.mPathStack?.observe(requireActivity(), Observer {
            updateData()
        })
        iv1.setOnClickListener {
            if (filesListRv.layoutManager is GridLayoutManager) {
                filesListRv.layoutManager = mLinearLayoutManager
                mFilesRvAdapter.isGridLayout  = false
                iv1.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_grid, context?.getTheme()))
            } else {
                filesListRv.layoutManager = mGridLayoutManager
                mFilesRvAdapter.isGridLayout  = true
                iv1.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_list, context?.getTheme()))
            }
        }
        iv2.setOnClickListener {
            Collections.sort(mFilesRvAdapter.filesList, AlphabeticalSortComparator())
            mFilesRvAdapter.notifyDataSetChanged()
        }
        iv3.setOnClickListener {
            NewFolderDialogFragment().show(activity?.supportFragmentManager, NewFolderDialogFragment::class.java.simpleName)
            //MoreOptionsDialogFragment().show(activity?.supportFragmentManager, MoreOptionsDialogFragment::class.java.simpleName)
            /*getCustomizedSnackbar(resources.getString(R.string.backup_error1, 2),
                resources.getString(R.string.backup_error_actiontext1), View.OnClickListener {  },
                resources.getColor(R.color.color_ffffff), resources.getColor(R.color.color_d83a3a),
                resources.getColor(R.color.color_c72c2c),resources.getDrawable(R.drawable.ic_tick),
                resources.getDrawable(R.drawable.ic_tick))
            if ((persistentSnackBar as Snackbar).isShown) {
                persistentSnackBar?.dismiss()
            } else {
                persistentSnackBar?.show()
            }
            if (actionPanel.visibility == View.GONE) {
                actionPanel.visibility = View.VISIBLE
            } else {
                actionPanel.visibility = View.GONE
            }*/
        }
        selectAll.setOnClickListener {
            selectAll()
        }
        skipNow.setOnClickListener {
            actionPanel.visibility = View.GONE
        }
        backupNow.setOnClickListener {
            actionPanel.visibility = View.GONE
        }
        viewStubHeader.inflate()
    }

    fun updateData() {
        var path = mViewModel?.mPathStack?.value?.peek()
        if (path == null) {
            return
        }
        if (!mProgressDialog.isShowing) {
            mProgressDialog.show()
        }
        executorService.submit({
            var files = getMediaFiles(activity as Activity, path)
            mHandler.post({
                if (mProgressDialog.isShowing) {
                    mProgressDialog.dismiss()
                }
                if (files.isEmpty()) {
                    showErrorView()
                } else {
                    showListView()
                }
                mFilesRvAdapter.updateData(files)
            })
        })
    }

    fun showErrorView() {
        emptyLayout.visibility = View.VISIBLE
        filesListRv.visibility = View.GONE
        titleItem1.visibility = View.GONE
        iv3.visibility = View.GONE
        iv2.visibility = View.GONE
        iv1.visibility = View.GONE
        selectAll.visibility = View.GONE
    }

    fun showListView() {
        emptyLayout.visibility = View.GONE
        filesListRv.visibility = View.VISIBLE
        titleItem1.visibility = View.VISIBLE
        var size = mViewModel?.mPathStack?.value?.size
        if (size != null && size == 1) {
            titleItem1.text = resources.getText(R.string.all_files_title)
        } else {
            titleItem1.text = resources.getText(R.string.files_title)
        }
        iv3.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_newfolder, context?.getTheme()))
        iv3.visibility = View.VISIBLE
        iv2.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_sort, context?.getTheme()))
        iv2.visibility = View.VISIBLE
        if (mFilesRvAdapter.isGridLayout) {
            iv1.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_list, context?.getTheme()))
        } else {
            iv1.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_grid, context?.getTheme()))
        }
        iv1.visibility = View.VISIBLE
        selectAll.visibility = View.GONE
    }

    fun setLongPressMode(value : Boolean) {
        if (value) {
            titleItem1.visibility = View.INVISIBLE
            iv3.visibility = View.INVISIBLE
            iv2.visibility = View.INVISIBLE
            iv1.visibility = View.INVISIBLE
            selectAll.visibility = View.VISIBLE
        } else {
            titleItem1.visibility = View.VISIBLE
            iv3.visibility = View.VISIBLE
            iv2.visibility = View.VISIBLE
            iv1.visibility = View.VISIBLE
            selectAll.visibility = View.INVISIBLE
            unselectAll()
        }
    }

    override fun onClick(position : Int, fileModel: FileModel) {
        if (mFilesRvAdapter.isLongPressMode) {
            toggleSelectItem(position, fileModel)
        } else {
            if (fileModel.fileType == FileUtils.FileType.ROOT_IMAGE) {
                mViewModel?.mPathStack?.value?.push(FileUtils.MediaType.IMAGE.name)
                mViewModel?.mPathStack?.value = mViewModel?.mPathStack?.value
            } else if (fileModel.fileType == FileUtils.FileType.ROOT_VIDEO) {
                mViewModel?.mPathStack?.value?.push(FileUtils.MediaType.VIDEO.name)
                mViewModel?.mPathStack?.value = mViewModel?.mPathStack?.value
            } else if (fileModel.fileType == FileUtils.FileType.ROOT_AUDIO) {
                mViewModel?.mPathStack?.value?.push(FileUtils.MediaType.AUDIO.name)
                mViewModel?.mPathStack?.value = mViewModel?.mPathStack?.value
            }
        }
    }

    fun toggleSelectItem(position : Int, fileModel: FileModel) {
        fileModel.isSelected = !fileModel.isSelected
        if (fileModel.isSelected) {
            mFilesRvAdapter.mSelectedList.add(position)
        } else {
            mFilesRvAdapter.mSelectedList.remove(position)
        }
        mFilesRvAdapter.notifyItemChanged(position)
    }

    override fun onLongClick(position : Int, fileModel: FileModel) {
        if (!mFilesRvAdapter.isLongPressMode) {
            mFilesRvAdapter.isLongPressMode = true
            fileModel.isSelected = !fileModel.isSelected
            if (fileModel.isSelected) {
                mFilesRvAdapter.mSelectedList.add(position)
            } else {
                mFilesRvAdapter.mSelectedList.remove(position)
            }
            mFilesRvAdapter.notifyDataSetChanged()
            setLongPressMode(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel?.mPathStack?.removeObservers(requireActivity())
    }

    fun onBackPressed() {
        if (mFilesRvAdapter.isLongPressMode) {
            mFilesRvAdapter.isLongPressMode = false
            mFilesRvAdapter.notifyDataSetChanged()
            setLongPressMode(false)
        } else {
            var size = mViewModel?.mPathStack?.value?.size
            if (size != null && size > 1) {
                mViewModel?.mPathStack?.value?.pop()
                mViewModel?.mPathStack?.value = mViewModel?.mPathStack?.value
            } else {
                activity?.finish()
            }
        }
    }

    fun selectAll() {
        mFilesRvAdapter.mSelectedList.clear()
        var count = 0
        for (files in mFilesRvAdapter.filesList) {
            files.isSelected = true
            mFilesRvAdapter.mSelectedList.add(count)
            count++
        }
        mFilesRvAdapter.notifyDataSetChanged()
    }

    fun unselectAll() {
        mFilesRvAdapter.mSelectedList.clear()
        for (files in mFilesRvAdapter.filesList) {
            files.isSelected = false
        }
        mFilesRvAdapter.notifyDataSetChanged()
    }

     inner class AlphabeticalSortComparator : Comparator<FileModel> {
        override fun compare(obj1: FileModel, obj2: FileModel): Int {
            if (obj1.name != null && obj2.name != null) {
                if ((obj1.name as String).toLowerCase().compareTo((obj2.name as String).toLowerCase()) < 0) {
                    return -1
                } else if ((obj1.name as String).toLowerCase().compareTo((obj2.name as String).toLowerCase()) > 0) {
                    return 1
                } else {
                    return 0
                }
            }
            return 0
        }
    }

    fun getCustomizedSnackbar(messageText : String, actionText : String, listener : View.OnClickListener, actionTextColor : Int?,
                              bgColor : Int?, actionBtnColor : Int?, infoDrawable : Drawable?, actionTextDrawable : Drawable?) {
        if (persistentSnackBar == null) {
            var view = getView()
            if (view == null) {
                return
            }
            persistentSnackBar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE)
        }
        persistentSnackBar?.setText(messageText)?.setAction(actionText, listener)
        var sbView = persistentSnackBar?.getView()
        sbView?.setPadding(0, 0, 0, 0)
        if (bgColor != null) {
            sbView?.setBackgroundColor(bgColor)
        }
        var tv = sbView?.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        tv.gravity = Gravity.CENTER_VERTICAL
        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(infoDrawable, null, null, null)
        tv.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.dp5)

        var actionBtn = sbView?.findViewById(com.google.android.material.R.id.snackbar_action) as TextView
        actionBtn.gravity = Gravity.CENTER_VERTICAL
        actionBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(actionTextDrawable, null, null, null)
        actionBtn.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.dp5)
        if (actionBtnColor != null) {
            actionBtn.setBackgroundColor(actionBtnColor)
        }
        actionBtn.setPadding(resources.getDimensionPixelSize(R.dimen.dp5), 0, resources.getDimensionPixelSize(R.dimen.dp5), 0)
        var params = actionBtn.layoutParams as LinearLayout.LayoutParams
        params.height = LinearLayout.LayoutParams.MATCH_PARENT
        actionBtn.layoutParams = params

        if (actionTextColor != null) {
            persistentSnackBar?.setActionTextColor(actionTextColor)
        }
    }
}