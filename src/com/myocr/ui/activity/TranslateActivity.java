package com.myocr.ui.activity;

import com.example.myocr.R;
import com.myocr.framework.translate.TranslateResult;
import com.myocr.framework.translate.TranslateUtil;
import com.myocr.framework.translate.WebTranslation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class TranslateActivity extends Activity {

	private EditText edt_text;
	private TextView tv_query, tv_result;
	private LinearLayout ll_explains, ll_webexplains;
	private Button btn_tran;
	private Button btn_webTranslation;
	private Handler handler;
	private LinearLayout.LayoutParams params;
	private Intent uper_intent;
	private TranslateResult translateResult = new TranslateResult();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.translate);
		edt_text = (EditText) findViewById(R.id.edt_text);
		tv_query = (TextView) findViewById(R.id.tv_query);
		tv_result = (TextView) findViewById(R.id.tv_result);
		ll_explains = (LinearLayout) findViewById(R.id.ll_explains);
		ll_webexplains = (LinearLayout) findViewById(R.id.ll_webexplains);
		btn_webTranslation = (Button)findViewById(R.id.btn_webTranslation);
		uper_intent = getIntent();
		if (uper_intent != null) {

			if (uper_intent.getFlags() == PhotographActivity.Activity_Flag) {
				translateResult = (TranslateResult) uper_intent.getExtras()
						.get(PhotographActivity.TRANSLATE_RESULT);
				drawTranslateResult();

			}

		}

		
		
		btn_webTranslation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showWebTranslation();	
				
				
			}
		});
		edt_text.setText(translateResult.getQueryString());
		btn_tran = (Button) findViewById(R.id.btn_tran);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					
					translateResult = (TranslateResult) msg.getData()
					.getSerializable("result");
					 drawTranslateResult();
					
				}
			}

			;
		};
		btn_tran.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!edt_text.getText().toString().equals("")) {
					new Thread(new MyRunnable()).start();
				} else {
					Toast.makeText(TranslateActivity.this, "请输入待翻译的文本",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.translate, menu);
		return true;
	}

	class MyRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				TranslateResult result = TranslateUtil.translateJson(edt_text
						.getText().toString());
				Message message = new Message();
				message.what = 1;
				Bundle bundle = new Bundle();
				// bundle.putString("resultString", resultString);
				bundle.putSerializable("result", result);
				message.setData(bundle);

				handler.sendMessage(message);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	
	private void drawTranslateResult() {
		ll_explains.removeAllViews();
		ll_webexplains.removeAllViews();
		
		tv_result.setText(translateResult.getQueryString());
		tv_query.setText(translateResult.getTranslation());
		params = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		for (String item : translateResult.getBasicExplain().getExplains()) {
			TextView textView = new TextView(TranslateActivity.this);
			textView.setTextColor(getResources().getColor(R.color.deep_gray));
			textView.setText(item);
			
			ll_explains.addView(textView, params);
		}

		
		ll_explains.requestLayout();
	}

	private void showWebTranslation() {
		for (WebTranslation webTranslation : translateResult
				.getWebTranslation()) {

			TextView tv_key = new TextView(TranslateActivity.this);
			tv_key.setTextColor(getResources().getColor(R.color.black));
			tv_key.setText(webTranslation.getKey());
			StringBuilder builder = new StringBuilder();
			for (String value : webTranslation.getValues()) {
				builder.append(value + "; ");
			}
			String valueString = builder.toString();
			Log.i("zhou", valueString);
			TextView tv_values = new TextView(
					TranslateActivity.this);
			tv_values.setTextColor(getResources().getColor(R.color.deep_gray));
			tv_values.setText(valueString);
		
			ll_webexplains.addView(tv_key, params);
			ll_webexplains.addView(tv_values, params);
			ll_webexplains.invalidate();
		}
	}
}
