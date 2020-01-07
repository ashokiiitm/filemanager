package filemanager.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import filemanager.data.MoreItemModel
import kotlinx.android.synthetic.main.more_option_item.view.*
import kotlinx.android.synthetic.main.more_options_fragment.*

/**
 * Created by Ashok on 2020-01-07.
 */
class MoreOptionsDialogFragment : BottomSheetDialogFragment() {

    private lateinit var mMoreOptionsAdapter: MoreOptionsAdapter
    var mOptionsList = ArrayList<MoreItemModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.more_options_fragment, null, false)
    }

    override fun onStart() {
        super.onStart()
        var bottomSheet: View? = dialog?.findViewById(R.id.design_bottom_sheet)
        bottomSheet?.background = null
        view?.post {
            val params = bottomSheet?.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            bottomSheetBehavior?.setPeekHeight((view as View).measuredHeight)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (activity == null) {
            return
        }
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moreOptionsRv.layoutManager = LinearLayoutManager(activity)
        mMoreOptionsAdapter = MoreOptionsAdapter(activity as Activity)
        moreOptionsRv.adapter = mMoreOptionsAdapter
        getMoreOptionsData()
    }

    fun getMoreOptionsData() {
        mOptionsList.clear()
        mOptionsList.add(MoreItemModel(resources.getString(R.string.more_option_1), resources.getString(R.string.more_option_1)))
        mOptionsList.add(MoreItemModel(resources.getString(R.string.more_option_2), resources.getString(R.string.more_option_2)))
        mOptionsList.add(MoreItemModel(resources.getString(R.string.more_option_3), resources.getString(R.string.more_option_3)))
        mOptionsList.add(MoreItemModel(resources.getString(R.string.more_option_4), resources.getString(R.string.more_option_4)))
        mMoreOptionsAdapter.updateData(mOptionsList)
    }
}

class MoreOptionsAdapter(val mContext : Context) : RecyclerView.Adapter<MoreOptionsAdapter.ViewHolder>() {

    var moreIcon1  : VectorDrawableCompat?
    var moreIcon2  : VectorDrawableCompat?
    var moreIcon3  : VectorDrawableCompat?
    var moreIcon4  : VectorDrawableCompat?

    init {
        moreIcon1 = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_more_icon_new_folder, mContext.getTheme())
        moreIcon2 = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_more_icon_upload_file, mContext.getTheme())
        moreIcon3 = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_more_icon_info, mContext.getTheme())
        moreIcon4 = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_more_icon_remove, mContext.getTheme())
    }


    var moreItemList = listOf<MoreItemModel>()

    fun updateData(moreItemList: List<MoreItemModel>) {
        this.moreItemList = moreItemList
        notifyDataSetChanged()
    }

    fun getIcon(iconName : String?) : VectorDrawableCompat? {
        when (iconName) {
            mContext.resources.getString(R.string.more_option_1) -> return moreIcon1
            mContext.resources.getString(R.string.more_option_2) -> return moreIcon2
            mContext.resources.getString(R.string.more_option_3) -> return moreIcon3
            mContext.resources.getString(R.string.more_option_4) -> return moreIcon4
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreOptionsAdapter.ViewHolder {
        var view  = LayoutInflater.from(parent.context).inflate(R.layout.more_option_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoreOptionsAdapter.ViewHolder, position: Int) = holder.bindView(position)

    override fun getItemCount() = moreItemList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            //todo
            val a = 12
        }

        fun bindView(position : Int) {
            val item = moreItemList[position]
            itemView.moreOptionNameTv.text = item.desc
            itemView.moreIconIv.setImageDrawable(getIcon(item.iconName))
        }
    }
}