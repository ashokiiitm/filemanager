package filemanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

/**
 * Created by Ashok on 2019-12-31.
 */
class FileViewModel : ViewModel() {
    var mPathStack : MutableLiveData<Stack<String>> = MutableLiveData()
}