package com.tuya.smart.share

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thingclips.smart.api.MicroContext
import com.thingclips.smart.sharemanager.api.AbsShareManager
import com.thingclips.smart.sharemanager.constant.ShareType

/**
 * ShareActivity
 */
class ShareActivity : AppCompatActivity(), View.OnClickListener{

    private var mShareManager = MicroContext.getServiceManager().findServiceByInterface<AbsShareManager>(AbsShareManager::class.java.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        findViewById<Button>(R.id.share_wx).setOnClickListener(this)
        findViewById<Button>(R.id.share_sms).setOnClickListener(this)
        findViewById<Button>(R.id.share_copy).setOnClickListener(this)
        findViewById<Button>(R.id.share_pic).setOnClickListener(this)
        findViewById<Button>(R.id.share_txt).setOnClickListener(this)
        findViewById<Button>(R.id.share_email).setOnClickListener(this)
        findViewById<Button>(R.id.share_sys_email).setOnClickListener(this)
        findViewById<Button>(R.id.share_all_sys_app).setOnClickListener(this)
        findViewById<Button>(R.id.launch_share_by_type).setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.share_wx -> shareWx()
            R.id.share_sms -> shareSms()
            R.id.share_copy -> shareCopy()
            R.id.share_pic -> sharePic()
            R.id.share_txt -> shareText()
            R.id.share_email -> shareEmail()
            R.id.share_sys_email -> shareSysEmail()
            R.id.share_all_sys_app -> shareAllApp()
            R.id.launch_share_by_type -> launchSharePlatform()
        }
    }

    private fun initWx() {
        var wxAppId = mShareManager?.getWxAppId()
        if (wxAppId == null) {
            Toast.makeText(this, "wxAppId is nof config", Toast.LENGTH_SHORT).show()
        }
         mShareManager.initWxShare(wxAppId)
    }

    private fun shareWx() {
        initWx()
        if (mShareManager.isWxInstalled()) {
            mShareManager.doShareByWeChat("share content")
        } else {
            Toast.makeText(this, "weixin is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareSms() {
        mShareManager.doShareBySms(this, "share content")
    }

    private fun shareCopy() {
        mShareManager.doShareByCopy("share content")
    }

    private fun checkHasEmail() : Boolean {
        return mShareManager.checkEmailApkExist(null)
    }

            //emailUrl: email address
    private fun shareEmail() {
        mShareManager.doShareByEmail(this, "title", "share content", "")
    }

    private fun shareSysEmail() {
        if (!checkHasEmail()) {
            Toast.makeText(this, "has no email", Toast.LENGTH_SHORT).show()
            return
        }
        mShareManager.doShareByEmail(this, "share content")
    }

    private fun shareAllApp() {
        mShareManager.doShareBySystemDefault(this, "share content")
    }

    private fun sharePic() {
        //Pass in image imagePath
        mShareManager.doShareByImage(this, "")
    }

    private fun shareText() {
        mShareManager.doShareByText(this, "share content")
    }

    /**
     * General entry
     * Can be called according to type
     */
    private fun launchSharePlatform() {
        mShareManager.launchSharePlatform(this, ShareType.TYPE_MORE, "share content")
    }
}