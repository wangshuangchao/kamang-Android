package com.mugua.enterprise.banben;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.mugua.enterprise.MainActivity;
import com.mugua.enterprise.MyApplication;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.Constant;

public class DownloadService extends Service {
	private static final int NOTIFY_ID = 0;
	private int progress;
	private NotificationManager mNotificationManager;
	private boolean canceled;
	// 返回的安装包url
	public String apkUrl = Constant.apkUrl;
	/* 下载包安装路径 */
	private static final String savePath = "/sdcard/update/";

	private static final String saveFileName = savePath + "咔芒.apk";
	private MainActivity.ICallbackResult callback;
	private DownloadBinder binder;
	private MyApplication app;
	private boolean serviceIsDestroy = false;

	private Context mContext = this;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					app.setDownload(false);
					// 下载完毕
					// 取消通知
//					mNotificationManager.cancel(NOTIFY_ID);
					installApk();
					break;
				case 2:
					app.setDownload(false);
					// 这里是用户界面手动取消，所以会经过activity的onDestroy();方法
					// 取消通知
					mNotificationManager.cancel(NOTIFY_ID);
					break;
				case 1:
					int rate = msg.arg1;
					app.setDownload(true);
					if (rate < 100) {
						RemoteViews contentview = notification.contentView;
						contentview.setTextViewText(R.id.tv_progress, rate + "%");
						contentview.setProgressBar(R.id.progressbar, 100, rate, false);
					} else {
						// 下载完毕后变换通知形式
						notification.flags = Notification.FLAG_AUTO_CANCEL;
						notification.contentView = null;

						notification = new Notification.Builder(mContext)
								.setOngoing(true)
								.setContentTitle("下载完成")
								.setContentText("文件已下载完毕")
								.setSmallIcon(R.mipmap.logo)
								.setWhen(System.currentTimeMillis())
								.build();
						serviceIsDestroy = true;
						stopSelf();// 停掉服务自身
					}
					mNotificationManager.notify(NOTIFY_ID, notification);
					break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub

		super.onRebind(intent);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		binder = new DownloadBinder();
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		setForeground(true);// 这个不确定是否有作用
		app = (MyApplication) getApplication();
	}

	private boolean setForeground(boolean b) {
		// TODO Auto-generated method stub
		return true;
	}

	public class DownloadBinder extends Binder {
		public void start() {
			if (downLoadThread == null || !downLoadThread.isAlive()) {

				progress = 0;
				setUpNotification();
				new Thread() {
					public void run() {
						// 下载
						startDownload();
					};
				}.start();
			}
		}

		public void cancel() {
			canceled = true;
		}

		public int getProgress() {
			return progress;
		}

		public void cancelNotification() {
			mHandler.sendEmptyMessage(2);
		}

		public void addCallback(MainActivity.ICallbackResult callback) {
			DownloadService.this.callback = callback;
		}
	}

	private void startDownload() {
		// TODO Auto-generated method stub
		canceled = false;
		downloadApk();
	}

	// 通知栏
	/**
	 * 创建通知
	 */
	private Notification notification;
//	private NotificationManager notificationManager;
//	private NotificationCompat.Builder builder;
	private void setUpNotification() {
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification_layout);
		contentView.setImageViewResource(R.id.btn_cancel, R.drawable.forum_tag_close);
		contentView.setTextViewText(R.id.name, "咔芒.apk 正在下载...");

		Intent intentClick = new Intent(this, NotificationBroadcastReceiver.class);
		intentClick.setAction("stop");
		intentClick.putExtra(NotificationBroadcastReceiver.TYPE, 0);
		PendingIntent pIntentPlay = PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
		contentView.setOnClickPendingIntent(R.id.btn_cancel, pIntentPlay);//为play控件注册事件


		notification = new Notification.Builder(this)
				.setOngoing(true)
				.setContentTitle("咔芒")
				.setContentText("开始下载")
				.setSmallIcon(R.mipmap.logo)
				.setDeleteIntent(pIntentPlay)
				.setWhen(System.currentTimeMillis())
				.build();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.contentView = contentView;
		mNotificationManager.notify(NOTIFY_ID, notification);


//		//实例化通知管理器
//		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		//实例化通知
//		builder = new NotificationCompat.Builder(this);
//		builder.setAutoCancel(true);
//		builder.setContentTitle("咔芒");
//		builder.setContentText("开始下载");
//		builder.setSmallIcon(R.mipmap.logo);
//		builder.setDeleteIntent(pIntentPlay);
//		builder.setPriority(Notification.PRIORITY_HIGH);
//		builder.setWhen(System.currentTimeMillis());
//		notification = builder.build();
//		notification.flags = Notification.FLAG_ONGOING_EVENT;
//		notification.contentView = contentView;
//		notificationManager.notify(NOTIFY_ID, notification);
	}

	//
	/**
	 * 下载apk
	 *
	 * @param url
	 */
	private Thread downLoadThread;

	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 *@ url
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
		callback.OnBackResult("finish");
	}

	private int lastRate = 0;
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					Message msg = mHandler.obtainMessage();
					msg.what = 1;
					msg.arg1 = progress;
					if (progress >= lastRate + 1) {
						mHandler.sendMessage(msg);
						lastRate = progress;
						if (callback != null)
							callback.OnBackResult(progress);
					}
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(0);
						// 下载完了，cancelled也要设置
						canceled = true;
						break;
					}
					fos.write(buf, 0, numread);
				} while (!canceled);// 点击取消就停止下载.

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
}
