package com.example.myocr.activity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import com.example.myocr.R;
import com.example.myocr.R.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnDrawListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotographActivity extends Activity {
	private final String TAG = "PhotographActivity";
	private SurfaceView sv_camara;
	private Button btn_takePhoto;
	private TextView tv_recognised_word;
	private TextView tv_translated_word;
	private ImageView img_scanned;
	private boolean hasSurface;// SurfaceView是否已经创建完成
	private CameraManager cameraManager;
	private LinearLayout llayout_word_area;// 单词所在的区域
	private int[] word_area_location = { 1, 2 };// 单词所在区域在屏幕中的绝对位置
	private int[] sv_area_location = { 1, 2 };// 单词所在区域在屏幕中的绝对位置
	private boolean word_area_hasMessured = false;// 用于有没有被测量过
	private boolean surface_area_hasMessured = false;// 用于SurfaceView
	private int word_area_height;// word区域的高度
	private int word_area_width;// word区域的宽度
	private int sv_area_height;// surfaceView的高度
	private int sv_area_width;// surfaceView的宽度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.photograph);
		cameraManager = new CameraManager(this);
		sv_camara = (SurfaceView) findViewById(R.id.sv_camara);
		tv_recognised_word = (TextView) findViewById(R.id.tv_recognised_word);
		tv_translated_word = (TextView) findViewById(R.id.tv_translated_word);
		img_scanned = (ImageView) findViewById(R.id.img_scanned);
		llayout_word_area = (LinearLayout) findViewById(R.id.llayout_word_area);

		ViewTreeObserver vto_sv_camera = sv_camara.getViewTreeObserver();
		vto_sv_camera.addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				// TODO Auto-generated method stub
				if (!surface_area_hasMessured) {
					sv_camara.getLocationInWindow(sv_area_location);
					// llayout_word_area.getLocationInWindow(word_area_location);
					Toast.makeText(
							PhotographActivity.this,
							word_area_location[0] + "  "
									+ word_area_location[1], Toast.LENGTH_LONG)
							.show();
					sv_area_height = sv_camara.getMeasuredHeight();
					sv_area_width = sv_camara.getMeasuredWidth();
					surface_area_hasMessured = true;
				}
				return true;
			}
		});

		// 在onCreate方法中获取控件位置等信息的方法
		ViewTreeObserver vto_graph = llayout_word_area.getViewTreeObserver();
		vto_graph.addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				// TODO Auto-generated method stub
				if (!word_area_hasMessured) {
					llayout_word_area.getLocationInWindow(word_area_location);
					// llayout_word_area.getLocationInWindow(word_area_location);
					Toast.makeText(
							PhotographActivity.this,
							word_area_location[0] + "  "
									+ word_area_location[1], Toast.LENGTH_LONG)
							.show();
					word_area_height = llayout_word_area.getMeasuredHeight();
					word_area_width = llayout_word_area.getMeasuredWidth();
					word_area_hasMessured = true;
				}
				return true;
			}
		});
		// 结束

		btn_takePhoto = (Button) findViewById(R.id.btn_takephoto);

		btn_takePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cameraManager.requestAutoFocus();
				llayout_word_area.getLocationInWindow(word_area_location);
				Toast.makeText(PhotographActivity.this,
						word_area_location[0] + "  " + word_area_location[1],
						Toast.LENGTH_LONG).show();

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				cameraManager.takePicture(null, null, new PictureCallback() {

					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						// TODO Auto-generated method stub
						int[] pixels = new int[100 * 100];// the size of the
															// array is the
															// dimensions of the
															// sub-photo
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
								data.length);
						Matrix matrix = new Matrix();
						matrix.postRotate((float) 90.0);
						bitmap = Bitmap.createBitmap(bitmap, 0, 0,
								bitmap.getWidth(), bitmap.getHeight(), matrix,
								false);
						bitmap = Bitmap.createScaledBitmap(bitmap,
								sv_area_width, sv_area_height, false);
						bitmap = Bitmap.createBitmap(bitmap,
								word_area_location[0], word_area_location[1],
								word_area_width, word_area_height);
						
						
						
						
						
						
						
						
						long dataTake = System.currentTimeMillis();
						File img_file = new File(Environment
								.getExternalStorageDirectory()
								+ "/takedphoto"
								+ dataTake + ".jpg");
						if (!img_file.exists()) {
							try {
								// 按照指定的路径创建文件夹
								img_file.createNewFile();
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						Log.i("img_path", img_file.getAbsolutePath() + "执行了呀");
						try {
							OutputStream outputStream = new FileOutputStream(
									img_file);
							BufferedOutputStream bs = new BufferedOutputStream(
									outputStream);
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
							bs.flush();
							bs.close();

						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO: handle exception
							e.printStackTrace();
						}

					}
				});

				// File file = new
				// File(Environment.getExternalStorageDirectory(),
				// "mmy.jpg");
				// Log.i("img_path", file.getAbsolutePath());
				// try {
				// OutputStream outputStream = new FileOutputStream(file);
				// Log.i("img_path", file.getAbsolutePath() + "执行了呀");
				// cameraManager.requestPreviewFrame(outputStream);
				// // cameraManager.requestPreviewFrame(outputStream);
				// } catch (FileNotFoundException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "==onResume==");

		SurfaceHolder holder = sv_camara.getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				hasSurface = false;
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				if (!hasSurface) {
					hasSurface = true;
					initCamera(holder);// 创建完成后立即进行预览
					Log.i(TAG, "===begin preview===");

				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub

			}
		});

		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置推模式，往SurfaceView中输送数据，SurfaceView没有自己的缓冲
		if (hasSurface) {
			initCamera(holder);
			Log.i(TAG, "====onResume begin preview");
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		cameraManager.stopPreview();
		cameraManager.closeDriver();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			cameraManager.openDriver(surfaceHolder);// 打开照相机

			cameraManager.startPreview();// 进行预览
		} catch (IOException e) {
			// TODO: handle exception
			Log.i(TAG, e.toString());
		}

	}

	final class CameraManager {
		private final Context mContext;
		private static final String TAG = "CameraManager";
		private Camera camera;
		private boolean preview;

		public CameraManager(Context context) {
			// TODO Auto-generated constructor stub
			this.mContext = context;
		}

		public void openDriver(SurfaceHolder holder) throws IOException {
			camera = Camera.open();
			setCameraDisplayOrientation(PhotographActivity.this, 0, camera);
			setCameraParameters();
			camera.setPreviewDisplay(holder);

		}

		public void closeDriver() {
			if (camera != null) {
				camera.release();
				camera = null;
			}
		}

		public void startPreview() {
			if (camera != null && !preview) {
				// setDisplayOrientation(camera, 90);

				camera.startPreview();
				preview = true;
			}
		}

		public void stopPreview() {
			if (camera != null && preview) {
				camera.setPreviewCallback(null);
				camera.stopPreview();
			}
		}

		public void requestPreviewFrame(final OutputStream stream) {
			if (camera != null && preview) {
				camera.setPreviewCallback(new Camera.PreviewCallback() {

					@Override
					public void onPreviewFrame(byte[] data, Camera camera) {
						// TODO Auto-generated method stub
						camera.setPreviewCallback(null);
						System.out.println(data);
						/*
						 * Bitmap bitmap = BitmapFactory.decodeByteArray(data,
						 * 0, data.length);
						 */
						// Convert to JPG
						Size previewSize = camera.getParameters()
								.getPreviewSize();
						YuvImage yuvimage = new YuvImage(data,
								ImageFormat.YUY2, previewSize.width,
								previewSize.height, null);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						yuvimage.compressToJpeg(new Rect(0, 0,
								previewSize.width, previewSize.height), 100,
								baos);

						byte[] jdata = baos.toByteArray();

						// Convert to Bitmap
						Bitmap bitmap = BitmapFactory.decodeByteArray(jdata, 0,
								jdata.length);

						try {
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
									stream);
							stream.flush();
							stream.close();
						} catch (Exception e) {
							Log.e(TAG, e.toString());
						}

					}
				});
			}
		}

		public void requestAutoFocus() {
			if (camera != null && preview) {
				camera.autoFocus(null);
			}
		}

		private void setCameraParameters() {
			Point mScreenResolution = getScreenResolution();
			Camera.Parameters parameters = camera.getParameters();
			parameters.setPreviewSize(mScreenResolution.x, mScreenResolution.y);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			// camera.setParameters(parameters);
		}

		/**
		 * 获取屏幕分辨率方法
		 * 
		 * @return Point x表示横向分辨率，y表示纵向分辨率
		 */
		private Point getScreenResolution() {
			WindowManager wm = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			return new Point(display.getWidth(), display.getHeight());
		}

		public void takePicture(ShutterCallback shutterCallback,
				PictureCallback rawCallback, PictureCallback jpegCallback) {
			if (camera != null) {
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);

			}
		}
	}

	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	class OcrThread extends Thread{
		public OcrThread() {
			// TODO Auto-generated constructor stub
		}
	}
	
	
	
}
