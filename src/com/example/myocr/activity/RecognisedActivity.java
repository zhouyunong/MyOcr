package com.example.myocr.activity;

import com.example.myocr.R;
import com.example.myocr.translate.TranslateResult;
import com.example.myocr.translate.TranslateUtil;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class RecognisedActivity extends Activity {

	private static final int TRANSLATE_SUCCESS = 3;

	private EditText edt_recognise;
	private EditText edt_translated;
	private Button btn_translate;
	private Handler handler;
	private TranslateResult translateResult;
	private String translatedResultString;
	private Intent uperIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recognised);
		edt_recognise = (EditText) findViewById(R.id.edt_recognised);
		edt_translated = (EditText) findViewById(R.id.edt_translated);
		btn_translate = (Button) findViewById(R.id.btn_translate);
		uperIntent = getIntent();
		String recogResult = uperIntent.getStringExtra("recogresult");
		edt_recognise.setText(recogResult);
		//translateEditText();
		// Hack:handler拉出去写
		handler = new MyHandler();

		btn_translate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Log.i("zhou", recogniseString);
				translateEditText();

			}

		});
	}

	private void translateEditText() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String recogniseString = edt_recognise.getText().toString();
				// TODO Auto-generated method stub
				try {
				translateResult = TranslateUtil
							.translateJson(recogniseString);
					//translatedResult = TranslateUtil.translateParagraph(recogniseString);
					Message msg = new Message();
					msg.what = TRANSLATE_SUCCESS;
					handler.sendMessage(msg);

					// String result = translateResult.

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private class MyHandler extends Handler {

		public MyHandler() {
			// TODO Auto-generated constructor stub
			super();
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TRANSLATE_SUCCESS:
				if (!translateResult.equals(null)) {
					translatedResultString = translateResult.getTranslation();
					edt_translated.setText(translatedResultString);
					edt_translated.setVisibility(View.VISIBLE);
		 		edt_translated.invalidate();
				}

				break;

			}

		}
	}

}
