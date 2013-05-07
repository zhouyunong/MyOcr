package com.example.myocr.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.myocr.R;

public class MainActivity extends Activity {
	private Button btn_word_from_camera, btn_text_from_image;
	public static final int RESULT_LOAD_IMAGE = 1;
	public static final int RESULT_CAMERA_IMAGE = 2;
	private Uri photoUri ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		btn_word_from_camera = (Button) findViewById(R.id.btn_word_from_camera);
		btn_text_from_image = (Button) findViewById(R.id.btn_text_from_image);
		final Builder builder = new AlertDialog.Builder(this);

		btn_word_from_camera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						PhotographActivity.class);
				startActivity(intent);
			}
		});
		btn_text_from_image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				builder.setIcon(R.drawable.ic_launcher);
				builder.setTitle("选择文本模式");
				builder.setItems(new String[] { "从相册中选择", "直接拍照" },
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								switch (which) {
								case 0:
									Intent intent = new Intent(
											Intent.ACTION_PICK,
											android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									startActivityForResult(intent,
											RESULT_LOAD_IMAGE);
									break;
								case 1:

									Intent intent2 = new Intent(
											MediaStore.ACTION_IMAGE_CAPTURE);
									SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
									String filaName = dateFormat.format(new Date());
									ContentValues values = new ContentValues();
									values.put(Media.TITLE, filaName);
									photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
									intent2.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
									startActivityForResult(intent2,RESULT_CAMERA_IMAGE);
									
									
									
									
									
//									if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
//										Toast.makeText(MainActivity.this, "SD卡不可用,请安装后重试", Toast.LENGTH_SHORT).show();
//										
//									}else {
//
//										File file = new File(Environment.getExternalStorageDirectory(), "forocr.jpg");
//										Uri outputFile = Uri.fromFile(file);
//										intent2.putExtra(MediaStore.EXTRA_OUTPUT,outputFile);
//										startActivityForResult(intent2,
//												RESULT_CAMERA_IMAGE);
//									}
									

									break;

								}
							}
						});

				builder.create().show();

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		super.onActivityResult(requestCode, resultCode, data);

		Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);

		switch (requestCode) {
		case RESULT_CAMERA_IMAGE:
			Uri uri = null ;
			if (resultCode == RESULT_OK && data != null) {
//				
				uri = data.getData();
				
				
			}
			if (uri==null) {
				if (photoUri!=null) {
					uri = photoUri;
				}
			}	
			
			intent.putExtra("img_uri", uri);
			intent.setFlags(RESULT_CAMERA_IMAGE);
			startActivity(intent);
				
			
			break;

		case RESULT_LOAD_IMAGE:
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK && data != null) {
				
				Uri selectedImage = data.getData();
				
				//HACK:选择多个图片，是否有必要
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				// ImageView imageView = (ImageView) findViewById(R.id.imgView);
				
				//Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
				intent.putExtra("img_path", picturePath);
				intent.setFlags(RESULT_LOAD_IMAGE);
				//intent.putExtra("data", bitmap);
				startActivity(intent);
				//bundle.putParcelable(key, value)
//				String s = OcrUtil.doOcr(bitmap);
//				Toast.makeText(this, s, Toast.LENGTH_LONG).show();
			}
			break;
		}

	}
	
	

}
