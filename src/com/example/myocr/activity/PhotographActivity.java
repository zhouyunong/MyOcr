package com.example.myocr.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import com.example.myocr.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class PhotographActivity extends Activity {
	private final String TAG = "PhotographActivity";
	private SurfaceView sv_camara;
	private Button btn_takePhoto;
	private boolean hasSurface;// SurfaceView是否已经创建完成
	CameraManager cameraManager;

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

		btn_takePhoto = (Button) findViewById(R.id.btn_takephoto);

		btn_takePhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cameraManager.requestAutoFocus();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				File file = new File(Environment.getExternalStorageDirectory(),
						"zhou.jpg");
				try {
					OutputStream outputStream = new FileOutputStream(file);
					// ByteArrayOutputStream outputStream = new
					// ByteArrayOutputStream();//往内存输出
					cameraManager.requestPreviewFrame(outputStream);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			 }
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
			// setDisplayOrientation(camera,0);
			// setCameraDisplayOrientation(PhotographActivity.this, 0,camera );
//			 if (Integer.parseInt(Build.VERSION.SDK) >= 8)
//			        setDisplayOrientation(camera, 90);
//			    else
//			    {
//			        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//			        {
//			            p.set("orientation", "portrait");
//			            p.set("rotation", 90);
//			        }
//			        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//			        {
//			            p.set("orientation", "landscape");
//			            p.set("rotation", 90);
//			        }
//			    } 
			camera.setPreviewDisplay(holder);
			setCameraParameters();
		}

		public void closeDriver() {
			if (camera != null) {
				camera.release();
				camera = null;
			}
		}

		public void startPreview() {
			if (camera != null && !preview) {
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
						Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
								data.length);
						try {
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
									stream);

						} catch (Exception e) {
							Log.e(TAG, e.toString());
						}
						// bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						// stream);

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
			camera.setParameters(parameters);
		}

		private Point getScreenResolution() {
			WindowManager wm = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			return new Point(display.getWidth(), display.getHeight());
			//Bitmap

		}

		public void takePicture(ShutterCallback shutterCallback,
				PictureCallback rawCallback, PictureCallback jpegCallback) {
			if (camera != null) {
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);

			}
		}
	}

	@SuppressLint("NewApi")
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

	/*
	 * protected void setDisplayOrientation(Camera camera, int angle){ Method
	 * downPolymorphic; try { downPolymorphic =
	 * camera.getClass().getMethod("setDisplayOrientation", new Class[] {
	 * int.class }); if (downPolymorphic != null) downPolymorphic.invoke(camera,
	 * new Object[] { angle }); } catch (Exception e1) { } }
	 */

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
		}
	}

}
