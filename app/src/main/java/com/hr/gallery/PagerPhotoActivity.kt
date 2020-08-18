package com.hr.gallery

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ToastUtils
import com.hr.gallery.adapter.PagerPhotoListAdapter
import kotlinx.android.synthetic.main.activity_pager_photo.*
import kotlinx.android.synthetic.main.pager_photo_view.view.*
import kotlinx.coroutines.*


class PagerPhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager_photo)

        var bundleExtra = intent.getBundleExtra("PHOTO_DATA")
        var photoArrayList = bundleExtra.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        var position = bundleExtra.getInt("PHOTO_POSITION")

        PagerPhotoListAdapter().apply {
            //设置适配器
            viewpager2.adapter = this
            //设置数据
            submitList(photoArrayList)
        }

        viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//                photoTag.text = "${position + 1}/${photoArrayList?.size}"
                //字符串模板
                photoTag.text = getString(R.string.photo_tag,position + 1, photoArrayList?.size)
            }
        })

        viewpager2.setCurrentItem(position,false)

        //下载
        saveButton.setOnClickListener {
            //请求权限
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //弹出对话框
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
                }
            }else {
                //权限已经有了
//                savePhoto()

                lifecycleScope.launch {
                    savePhoto2()
                }
            }
        }

    }

    private fun savePhoto() {
        var myViewHolder =
            (viewpager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewpager2.currentItem)
                    as PagerPhotoListAdapter.MyViewHolder
        var toBitmap = myViewHolder.itemView.pagerPhoto.drawable.toBitmap()
        var url = MediaStore.Images.Media.insertImage(this.contentResolver,toBitmap,"","");
        if(url == null) {
            ToastUtils.showShort("存储失败")
        }else {
            ToastUtils.showShort("存储成功")
            //发送广播, 重新刷新图库
            val scannerIntent =
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(url))
            sendBroadcast(scannerIntent)
        }
    }

    //使用协成处理  suspend允许挂起
    private suspend fun savePhoto2() {
        // withContext(Dispatchers.IO) 能允许跑在子线程
        withContext(Dispatchers.IO) {
            var myViewHolder =
                (viewpager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewpager2.currentItem)
                        as PagerPhotoListAdapter.MyViewHolder
            var toBitmap = myViewHolder.itemView.pagerPhoto.drawable.toBitmap()

            //如果 saveUri是空的, 则执行后面run的操作
           val saveUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())?: kotlin.run {
               ToastUtils.showShort("存储失败")
               return@withContext
           }

            //写进去
            contentResolver.openOutputStream(saveUri).use {
                var flag = toBitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
                if(flag) {
                    //Toast要在主线程操作
                    MainScope().launch {
                        ToastUtils.showShort("存储成功")
                        //发送广播, 重新刷新图库
                        val scannerIntent =
                            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(saveUri.toString()))
                        sendBroadcast(scannerIntent)
                    }
                }else {
                    MainScope().launch { ToastUtils.showShort("存储失败") }

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            // 请求回调
            1 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    savePhoto()
                    lifecycleScope.launch {
                        savePhoto2()
                    }
                }else {
                    ToastUtils.showShort("存储权限拒绝")
                }
            }
        }
    }
}