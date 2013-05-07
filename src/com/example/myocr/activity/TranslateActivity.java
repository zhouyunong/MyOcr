package com.example.myocr.activity;

import com.example.myocr.R;
import com.example.myocr.translate.TranslateResult;
import com.example.myocr.translate.TranslateUtil;
import com.example.myocr.translate.WebTranslation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class TranslateActivity extends Activity {

	EditText edt_text;
	TextView tv_query, tv_result;
	LinearLayout ll_explains, ll_webexplains;
	Button btn_tran;
	Handler handler;
	LinearLayout.LayoutParams params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.translate);

		edt_text = (EditText) findViewById(R.id.edt_text);
		tv_query = (TextView) findViewById(R.id.tv_query);
		tv_result = (TextView) findViewById(R.id.tv_result);
		ll_explains = (LinearLayout) findViewById(R.id.ll_explains);
		ll_webexplains = (LinearLayout) findViewById(R.id.ll_webexplains);

		btn_tran = (Button) findViewById(R.id.btn_tran);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					ll_explains.removeAllViews();
					ll_webexplains.removeAllViews();
					TranslateResult result = (TranslateResult) msg.getData()
							.getSerializable("result");
					tv_result.setText(result.getQueryString());
					tv_query.setText(result.getTranslation());
					params = new LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					for (String item : result.getBasicExplain().getExplains()) {
						TextView textView = new TextView(TranslateActivity.this);
						textView.setText(item);
						ll_explains.addView(textView, params);
					}

					for (WebTranslation webTranslation : result
							.getWebTranslation()) {
						
						TextView tv_key = new TextView(TranslateActivity.this);
						tv_key.setText(webTranslation.getKey());
						StringBuilder builder = new StringBuilder();
						for (String value : webTranslation.getValues()) {
							builder.append(value + "; ");
						}
						String valueString = builder.toString();
						Log.i("zhou", valueString);
						TextView tv_values = new TextView(
								TranslateActivity.this);
						tv_values.setText(valueString);
						ll_webexplains.addView(tv_key, params);
						ll_webexplains.addView(tv_values, params);
						// ll_webexplains.addView(ll_webTrans,params);
					}

					// tv_query.invalidate();
					// tv_result.invalidate();
					// ll_explains.invalidate();
					// ll_webexplains.requestLayout();
					ll_explains.requestLayout();
					// mainLayout.requestLayout();
					// StringBuilder stringBuilder = new StringBuilder();
					//
					// for (int i = 0; i <
					// result.getBasicExplain().getExplains()
					// .size(); i++) {
					// stringBuilder.append(result.getBasicExplain()
					// .getExplains().get(i)
					// + ";");
					// String resultString = result.getQueryString() + "  "
					// + result.getTranslation() + "   "
					// + stringBuilder;
					//
					// tv_result.setText(resultString);
					//
					// tv_result.invalidate();
					// break;
					// }
				}
			};
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

}
