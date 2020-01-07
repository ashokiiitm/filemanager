package filemanager.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.new_folder_fragment.*

/**
 * Created by Ashok on 2020-01-07.
 */
class NewFolderDialogFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.new_folder_fragment, null, false)
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
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newFolderEdit.hint = resources.getString(R.string.create_folder_hint)
    }
}