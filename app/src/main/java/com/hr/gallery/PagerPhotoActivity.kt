package com.hr.gallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.LogUtils
import com.hr.gallery.adapter.PagerPhotoListAdapter
import kotlinx.android.synthetic.main.activity_pager_photo.*
import java.util.logging.Logger

class PagerPhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager_photo)

        var bundleExtra = intent.getBundleExtra("PHOTO_DATA")
        var photoArrayList = bundleExtra.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        var position = bundleExtra.getInt("PHOTO_POSITION")
        LogUtils.e(photoArrayList)
        LogUtils.e(position)

        PagerPhotoListAdapter().apply {
            //设置适配器
            viewpager2.adapter = this
            //设置数据
            submitList(photoArrayList)
        }

        viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = "${position + 1}/${photoArrayList?.size}"
            }
        })

        viewpager2.setCurrentItem(position,false)
    }
}