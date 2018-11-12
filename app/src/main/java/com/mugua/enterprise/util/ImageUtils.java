package com.mugua.enterprise.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by tian on 14/10/20:下午11:46.
 */
public class ImageUtils {

    public static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory()
            + "/DCIM/Camera");

    public static final int PHOTO_PICKED_WITH_DATA = 3021;

    public static final int TAKE_OR_CHOOSE_PHOTO = 3024;

    public static File mCurrentPhotoFile;

    public static File mTempCropFile;

    public static Intent getCropImageIntent(Uri photoUri, Context context, int width,int height) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("aspectX", 5);
        intent.putExtra("aspectY", 3);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", false);
        if (mTempCropFile == null) {
            File dir = getCacheDir(context);
            if (dir == null) {
                return null;
            }
            dir.mkdirs();
            mTempCropFile = new File(dir, "temp_crop.png");
        }
        mTempCropFile.delete();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempCropFile));
        return intent;
    }

    public static Intent getCropImageIntent2(Uri photoUri, Context context) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 1440);
        intent.putExtra("outputY", 1440);
        intent.putExtra("return-data", false);
        if (mTempCropFile == null) {
            File dir = getCacheDir(context);
            if (dir == null) {
                return null;
            }
            dir.mkdirs();
            mTempCropFile = new File(dir, "temp_crop.png");
        }
        mTempCropFile.delete();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempCropFile));
        return intent;
    }

    public static Intent getCropImageIntent1(Uri photoUri, Context context) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("aspectX", 672);
        intent.putExtra("aspectY", 410);
        intent.putExtra("outputX", 1344);
        intent.putExtra("outputY", 820);
        intent.putExtra("return-data", false);
        if (mTempCropFile == null) {
            File dir = getCacheDir(context);
            if (dir == null) {
                return null;
            }
            dir.mkdirs();
            mTempCropFile = new File(dir, "temp_crop.png");
        }
        mTempCropFile.delete();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempCropFile));
        return intent;
    }

    public static File getCacheDir(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            File sdDir = Environment.getExternalStorageDirectory();
            cacheDir = new File(sdDir, "/android/data/" + context.getPackageName()
                    + "/cache");
        }
        return cacheDir;

    }

    public static void takeOrChoosePhoto(Activity context, int requestCode) {
        PHOTO_DIR.mkdirs();
        mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
        Uri outputFileUri = Uri.fromFile(mCurrentPhotoFile);
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName,
                    res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);//ACTION_GET_CONTENT

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "选择照片");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[]{}));

        context.startActivityForResult(chooserIntent, requestCode);
    }

    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.US);
        return dateFormat.format(date) + ".png";
    }

    public static File getPhotoFromResult(Context context, Intent data) {
        final boolean isCamera;
        if (data == null) {
            isCamera = true;
        } else {
            isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
        }
        File f = null;
        if (isCamera) {
            f = mCurrentPhotoFile;
        } else {
            if (data != null) {
                try {
                    String path = getRealPathFromURI(context, data.getData());
                    f = new File(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return f;
    }

    private static String getRealPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (uri == null) {
            //FIXME: workaround for null uri, will try to get the last picture!
            Cursor myCursor = null;
            try {
                String[] largeFileProjection = {
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATA};

                String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
                myCursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        largeFileProjection, null, null, largeFileSort);
                String largeImagePath = "";

                if (myCursor.moveToFirst()) {
                    largeImagePath = myCursor.getString(1);
                    Uri imageCaptureUri = Uri.fromFile(new File(largeImagePath));
                    return imageCaptureUri.getPath();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }
            }

        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if ((cursor != null) && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static void doCropPhoto(Activity context, File f,int width,int height) {
        if ((f == null) || !f.exists()) {
            Toast.makeText(context, "文件不存在，无法裁剪照片", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            // Add the image to the media store
            MediaScanFile(context, f.getAbsolutePath());
            // Launch gallery to crop the photo
            if(width == 1)
            {
                final Intent intent = getCropImageIntent1(Uri.fromFile(f), context);
                context.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
            }
            else if (width == 2)
            {
                final Intent intent = getCropImageIntent2(Uri.fromFile(f), context);
                context.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
            }
            else if (width > 3)
            {
                final Intent intent = getCropImageIntent(Uri.fromFile(f), context,width,height);
                context.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
            }
        } catch (Exception e) {
            Toast.makeText(context, "无法裁剪照片", Toast.LENGTH_LONG).show();
        }
    }

    public static void MediaScanFile(Context context, String path) {
        MediaScannerConnection
                .scanFile(context, new String[]{path},
                        new String[]{null}, null);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static Bitmap getCroppedImage() {
        if ((mTempCropFile == null) || !mTempCropFile.exists()) {
            return null;
        }
        Bitmap bmp = BitmapFactory.decodeFile(mTempCropFile.getAbsolutePath());
        return bmp;
    }
}
