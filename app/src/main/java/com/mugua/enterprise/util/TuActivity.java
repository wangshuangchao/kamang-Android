package com.mugua.enterprise.util;

import android.graphics.Bitmap;
import android.os.Handler;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Administrator on 2017/5/19.
 */

public class TuActivity {

    private static DisplayImageOptions options;
    public static DisplayImageOptions tu(int tu)
    {
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showStubImage(tu)			// 设置图片下载期间显示的图片
                .showImageForEmptyUri(tu)	// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(tu)		// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
                .cacheOnDisc(false)							// 设置下载的图片是否缓存在SD卡中
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)//或者.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
                .handler(new Handler())
                .build();
        return options;
    }

    public static DisplayImageOptions tu1(int tu)
    {
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showStubImage(tu)			// 设置图片下载期间显示的图片
                .showImageForEmptyUri(tu)	// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(tu)		// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
                .cacheOnDisc(false)							// 设置下载的图片是否缓存在SD卡中
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)//或者.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .displayer(new RoundedBitmapDisplayer(0))	// 设置成圆角图片
                .handler(new Handler())
                .build();
        return options;
    }
}
