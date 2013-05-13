package com.example.myocr.activity;

import com.example.myocr.R;
import com.example.myocr.translate.TranslateResult;
import com.example.myocr.translate.TranslateUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecognisedActivity extends Activity {

	private static final int TRANSLATE_SUCCESS = 3;
	private EditText edt_recognise;
	private EditText edt_translated;
	private Button btn_translate;
	private Handler handler;
	private String translatedResultString;
	private Intent uperIntent;
	private TextView tv_recogresult_fullscreen;
	private TextView tv_translate_fullscreen;
	private LinearLayout llayout_recog;
	private LinearLayout llayout_translate;
	private boolean is_recog_fullscreen = false;
	private boolean is_translate_fullscreen = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recognised);
		edt_recognise = (EditText) findViewById(R.id.edt_recognised);
		edt_translated = (EditText) findViewById(R.id.edt_translated);
		btn_translate = (Button) findViewById(R.id.btn_translate);
		tv_recogresult_fullscreen = (TextView) findViewById(R.id.tv_recogresult_fullscreen);
		tv_translate_fullscreen = (TextView)findViewById(R.id.tv_translate_fullscreen);
		llayout_recog = (LinearLayout) findViewById(R.id.llayout_recog);
		llayout_translate = (LinearLayout) findViewById(R.id.llayout_translate);

		uperIntent = getIntent();
				String recogResult = uperIntent.getStringExtra("recogresult");
		Log.i("recogReslt", recogResult);
		
		
		
		edt_recognise.setText(recogResult);
		
		
		
		handler = new MyHandler();

		OnClickListener onclickListener = new MyOnclickListener();
		btn_translate.setOnClickListener(onclickListener);
		tv_recogresult_fullscreen.setOnClickListener(onclickListener);
		tv_translate_fullscreen.setOnClickListener(onclickListener);

	}

	private void translateEditText() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String recogniseString = edt_recognise.getText().toString();
				// TODO Auto-generated method stub
				try {

					System.out.println(recogniseString);
					recogniseString = recogniseString
							.replaceAll("\\n|\\r", " ");
					translatedResultString = TranslateUtil
							.translateParagraph(recogniseString);

					Message msg = new Message();
					msg.what = TRANSLATE_SUCCESS;
					handler.sendMessage(msg);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private class MyHandler extends Handler {

		public MyHandler() {
			super();
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TRANSLATE_SUCCESS:
				if (translatedResultString != null) {

					edt_translated.setText(translatedResultString);
					edt_translated.invalidate();
				}
				break;

			}

		}
	}

	class MyOnclickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_translate:
				translateEditText();
				break;
			case R.id.tv_recogresult_fullscreen:

				if (!is_recog_fullscreen) {
					llayout_translate.setVisibility(View.GONE);

					tv_recogresult_fullscreen.setText("显示翻译结果");
					llayout_translate.invalidate();
					is_recog_fullscreen = true;
				} else {
					llayout_translate.setVisibility(View.VISIBLE);

					tv_recogresult_fullscreen.setText("识别结果全屏");
					llayout_translate.invalidate();
					is_recog_fullscreen = false;
				}

				break;
			case R.id.tv_translate_fullscreen:

				if (!is_translate_fullscreen) {
					llayout_recog.setVisibility(View.GONE);

					tv_translate_fullscreen.setText("显示识别结果");
					llayout_recog.invalidate();
					is_translate_fullscreen = true;
				} else {
					llayout_recog.setVisibility(View.VISIBLE);

					tv_translate_fullscreen.setText("翻译结果全屏");
					llayout_recog.invalidate();
					is_translate_fullscreen = false;
				}

				break;

			}
		}

	}
}
