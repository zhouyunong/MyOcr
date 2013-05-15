package com.example.myocr.activity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.example.myocr.R;
import com.example.myocr.recognise.OCR;
import com.example.myocr.recognise.OcrUtil;
import com.example.myocr.translate.Basic;
import com.example.myocr.translate.TranslateResult;
import com.example.myocr.translate.TranslateUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotographActivity extends Activity {
	private final String TAG = "PhotographActivity";
	private final int ON_RECOG_FINISHED = 0;
	private final int ON_TRANSLATE_FINISHED = 1;
	public  final static int Activity_Flag = 1;
	public static final String TRANSLATE_RESULT = "word_result" ;

	private SurfaceView sv_camara;
	//private Button btn_lock_word;
	private Button btn_word_detail;
	private TextView tv_recognised_word;
	private TextView tv_translated_word;
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
	private WordScanTask wordScanTask;
	private AutoFocusCallback autoFocusCallback;
	private UiHandler uiHandler;
	private OCR myOcrUtil = new OCR();
	private AutoFocusThread autoFocusThread;
	private boolean isThreadWaiting = false;
	private TranslateResult translateResult_this_moment;

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
	//	btn_lock_word = (Button) findViewById(R.id.btn_lock_word);
		btn_word_detail = (Button) findViewById(R.id.btn_word_details);
		autoFocusThread = new AutoFocusThread();

		llayout_word_area = (LinearLayout) findViewById(R.id.llayout_word_area);
		uiHandler = new UiHandler();
		ViewTreeObserver vto_sv_camera = sv_camara.getViewTreeObserver();
		vto_sv_camera.addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				// TODO Auto-generated method stub
				if (!surface_area_hasMessured) {
					sv_camara.getLocationInWindow(sv_area_location);
					// llayout_word_area.getLocationInWindow(word_area_location);
//					Toast.makeText(PhotographActivity.this,
//							sv_area_location[0] + "  " + sv_area_location[1],
//							Toast.LENGTH_LONG).show();
					sv_area_height = sv_camara.getMeasuredHeight();
					sv_area_width = sv_camara.getMeasuredWidth();
					surface_area_hasMessured = true;
				}
				return true;
			}
		});

		btn_word_detail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (translateResult_this_moment!=null) {
					Intent intent = new Intent(PhotographActivity.this,TranslateActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(TRANSLATE_RESULT, translateResult_this_moment);
					intent.putExtras(bundle);
					intent.setFlags(Activity_Flag);
					hasSurface = false;
					
					startActivity(intent);
					finish();
				}
			}
		});
//		btn_lock_word.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});

		// 在onCreate方法中获取控件位置等信息的方法
		ViewTreeObserver vto_graph = llayout_word_area.getViewTreeObserver();
		vto_graph.addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				// TODO Auto-generated method stub
				if (!word_area_hasMessured) {
					llayout_word_area.getLocationInWindow(word_area_location);
					// llayout_word_area.getLocationInWindow(word_area_location);
//					Toast.makeText(
//							PhotographActivity.this,
//							word_area_location[0] + "  "
//									+ word_area_location[1], Toast.LENGTH_LONG)
//							.show();
					word_area_height = llayout_word_area.getMeasuredHeight();
					word_area_width = llayout_word_area.getMeasuredWidth();
					word_area_hasMessured = true;
				}

				return true;
			}
		});
		// 结束

		autoFocusCallback = new AutoFocusCallback() {

			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				// TODO Auto-generated method stub
				if (success)// success表示对焦成功
				{
					Log.i("tag", "myAutoFocusCallback: success...");
					cameraManager.camera
							.setOneShotPreviewCallback(new MyPreviewCallBackHandler());

				} else {
					// 未对焦成功

					Log.i("tag", "myAutoFocusCallback: 失败了...");

					// 这里也可以加上myCamera.autoFocus(myAutoFocusCallback)，如果聚焦失败就再次启动聚焦。

				}
			}
		};

	}

	@Override
	protected void onResume() {
	
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
					if (autoFocusThread != null) {
						if (!autoFocusThread.isAlive()) {
							autoFocusThread.start();
						}
						
					}

					// cameraManager.camera.autoFocus(autoFocusCallback);
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
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		cameraManager.stopPreview();
		cameraManager.closeDriver();
		super.onPause();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			cameraManager.openDriver(surfaceHolder);// 打开照相机

			cameraManager.startPreview();// 进行预览
			// new OcrThread().start();
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

		// public void requestPreviewFrame(final OutputStream stream) {
		// if (camera != null && preview) {
		// camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
		//
		// @Override
		// public void onPreviewFrame(byte[] data, Camera camera) {
		// // TODO Auto-generated method stub
		//
		//
		// }
		// });
		// }
		// }

		public synchronized void  requestAutoFocus() {
			if (camera != null && preview) {
				camera.autoFocus(autoFocusCallback);
			}
		}

		private void setCameraParameters() {
			Point mScreenResolution = getScreenResolution();
			Camera.Parameters parameters = camera.getParameters();
			parameters.setPreviewSize(mScreenResolution.x, mScreenResolution.y);
			parameters.setPictureFormat(PixelFormat.JPEG);
			parameters.setPictureSize(sv_area_height, sv_area_width);
			Log.i("sv_height", sv_area_height + "   " + sv_area_width);
			parameters
					.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
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

	class MyPictureCallback implements PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			Matrix matrix = new Matrix();
			matrix.postRotate((float) 90.0);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, false);
			bitmap = Bitmap.createScaledBitmap(bitmap, sv_area_width,
					sv_area_height, false);
			bitmap = Bitmap.createBitmap(bitmap, word_area_location[0],
					word_area_location[1], word_area_width, word_area_height);

			long dataTake = System.currentTimeMillis();
			File img_file = new File(Environment.getExternalStorageDirectory()
					+ "/takedphoto" + dataTake + ".jpg");
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
				OutputStream outputStream = new FileOutputStream(img_file);
				BufferedOutputStream bs = new BufferedOutputStream(outputStream);
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

	}

	// class OcrThread extends Thread {
	//
	// public OcrThread() {
	//
	// }
	//
	// @Override
	// public void run() {
	// while (hasSurface) {
	// cameraManager.requestAutoFocus();
	// try {
	// Thread.sleep(3000);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// // TODO Auto-generated method stub
	//
	// }
	//
	// }

	private class WordScanTask extends AsyncTask<Void, Void, Void> {
		private byte[] data;

		public WordScanTask(byte[] data) {
			// TODO Auto-generated constructor stub
			this.data = data;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Size size = cameraManager.camera.getParameters().getPreviewSize();// 获取预览大小
			final int preview_width = size.width;
			final int preivew_height = size.height;
			final YuvImage image = new YuvImage(data, ImageFormat.NV21,
					preview_width, preivew_height, null);
			ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
			if (!image.compressToJpeg(new Rect(0, 0, preview_width,
					preivew_height), 100, os)) {
				return null;
			}
			byte[] tmp = os.toByteArray();
			Bitmap bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);

			Matrix matrix = new Matrix();
			matrix.postRotate((float) 90.0);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, false);
			bitmap = Bitmap.createScaledBitmap(bitmap, sv_area_width,
					sv_area_height, false);
			bitmap = Bitmap.createBitmap(bitmap, word_area_location[0],
					word_area_location[1], word_area_width, word_area_height);

			String ocrString = myOcrUtil.doOcr(bitmap);
			String ocrResult = OcrUtil.readMidWord(ocrString);
			
			if (ocrResult!=null) {
				Message msg = new Message();
				msg.what = ON_RECOG_FINISHED;
				msg.obj = ocrResult;
				uiHandler.sendMessage(msg);

				TranslateResult translateResult = new TranslateResult();
				try {
					translateResult = TranslateUtil.translateJson(ocrResult);

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// tv_translated_word.setText(translateResult.getTranslation());
				Message msg_translate_finished = new Message();

				msg_translate_finished.what = ON_TRANSLATE_FINISHED;
				msg_translate_finished.obj = translateResult;
				uiHandler.sendMessage(msg_translate_finished);
			}
			

			// Toast.makeText(PhotographActivity.this, ocrResut,
			// Toast.LENGTH_SHORT).show();
			// 将获取的图片保存至储存卡
			// long dataTake = System.currentTimeMillis();
			// File img_file = new
			// File(Environment.getExternalStorageDirectory()
			// + "/preview" + dataTake + ".jpg");
			// if (!img_file.exists()) {
			// try {
			// // 按照指定的路径创建文件夹
			// img_file.createNewFile();
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			// }
			// Log.i("img_path", img_file.getAbsolutePath() + "执行了呀");
			// try {
			// OutputStream outputStream = new FileOutputStream(img_file);
			// BufferedOutputStream bs = new BufferedOutputStream(outputStream);
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
			// bs.flush();
			// bs.close();
			//
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO: handle exception
			// e.printStackTrace();
			// }

			return null;
		}

	}

	class MyPreviewCallBackHandler implements Camera.PreviewCallback {

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			camera.setPreviewCallback(null);
			System.out.println(data);

			if (null != wordScanTask) {
				switch (wordScanTask.getStatus()) {
				case RUNNING:
					return;
				case PENDING:
					wordScanTask.cancel(false);
					break;
				}
			}
			wordScanTask = new WordScanTask(data);
			wordScanTask.execute((Void) null);
		}

	}

	class UiHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case ON_RECOG_FINISHED:
				
				tv_recognised_word.setText((String) msg.obj);

				break;
			case ON_TRANSLATE_FINISHED:
				// Toast.makeText(PhotographActivity.this, text, duration);
				translateResult_this_moment = (TranslateResult) msg.obj;
				Basic basicExplains = translateResult_this_moment
						.getBasicExplain();
				StringBuilder builder = new StringBuilder();
				for (String explainResult : basicExplains.getExplains()) {
					builder.append(explainResult + "; ");
				}

				tv_translated_word.setText(builder.toString());

			}

		}
	}

	class AutoFocusThread extends Thread {
		

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// cameraManager.camera.autoFocus();

			// TODO Auto-generated method stub

//			try {
//				synchronized (this) {
//					if (waiting) {
//						this.wait();
//					}
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//			}

			while ((!Thread.currentThread().isInterrupted())&&hasSurface) {
				cameraManager.requestAutoFocus();
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}

			}
		}
	}

}
