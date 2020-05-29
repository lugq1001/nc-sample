package com.nextcont.mobilization.ui.face

import com.baidu.idl.face.platform.FaceStatusEnum
import com.baidu.idl.face.platform.ui.FaceLivenessActivity
import com.nextcont.mobilization.util.DialogUtil
import java.util.*

class FaceLivenessExpActivity : FaceLivenessActivity() {

    companion object {
        const val CODE_SUCCESS = 90
    }

    override fun onLivenessCompletion(status: FaceStatusEnum, message: String, base64ImageMap: HashMap<String, String>?) {
        super.onLivenessCompletion(status, message, base64ImageMap)

        if (status == FaceStatusEnum.OK && mIsCompletion) {
            setResult(CODE_SUCCESS)
            finish()
        } else if (status == FaceStatusEnum.Error_DetectTimeout || status == FaceStatusEnum.Error_LivenessTimeout || status == FaceStatusEnum.Error_Timeout) {
            DialogUtil.showAlert(this, "检测超时") {
                finish()
            }
        }
    }


}