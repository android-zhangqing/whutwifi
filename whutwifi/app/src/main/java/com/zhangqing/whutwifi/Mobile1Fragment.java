package com.zhangqing.whutwifi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Mobile1Fragment extends Fragment implements OnClickListener {

	private Button[] logins = new Button[10];

	private Button btnSaveUserPass;

	private Button btnSynchronized;

	private LinearLayout LayoutNoNetwork;

	private EditText editUser;
	private EditText editPass;

	private TextView textOutput;

	private SharedPreferences sharedPreferences;
	private Editor editor;

	private String ip;
	private String mac;
	private String switchip;
	private String acid;
	private String user;
	private String pass;

	private boolean[] resultBoolean;
	private ArrayList<TypeLogin> typeLoginList;
	ListView lv;

	CheckBox checkBoxDialog;

	private boolean isRunning;

	ProgressDialog progressDialog;
	int progressStatus = 0;

	Thread timerTest;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data;
			String val;
			switch (msg.what) {
			case 1231:
				textOutput.append("\r\n正在自动重新登录：");
				break;
			case 1232:
				data = msg.getData();
				val = data.getString("value");
				Log.i("mylog", "请求结果为-->" + val);
				// TODO
				// UI界面的更新等相关操作
				val = val.replaceAll("<script language=\"javascript\">", "");
				val = val.replaceAll("</script>", "");
				val = val.replaceAll("location=\"", "");
				String aa = "/srun_portal.html\\?action=login_ok\";";//
				val = val.replaceAll(aa, "登录成功啦！！");
				textOutput.append("\r\n处理结果：" + val
						+ "\r\n----------------------");
				for (int i = 0; i < logins.length; i++) {
					logins[i].setClickable(true);
				}
				break;
			case 1233:
				data = msg.getData();
				val = data.getString("value");
				Toast.makeText(getActivity(), val, Toast.LENGTH_SHORT).show();
				break;
			case 1251:

				int count = 0;
				for (int i = 0; i < resultBoolean.length; i++) {
					if (resultBoolean[i] == true) {
						count++;
					}
				}
				progressDialog = new ProgressDialog(getActivity());
				progressDialog.setTitle("正在上传，请稍后...");
				progressDialog.setCancelable(false);
				progressDialog.setMax(count);
				progressDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setIndeterminate(false);
				progressDialog.show();

				break;
			case 1252:
				progressDialog.dismiss();
				break;
			case 1253:
				progressDialog.setProgress(progressStatus);
				break;
			case 1261:
				data = msg.getData();
				String networkType = data.getString("value");

				if (networkType.equals("-1")) {
					if (LayoutNoNetwork.getVisibility() == View.INVISIBLE) {
						LayoutNoNetwork.setVisibility(View.VISIBLE);
						LayoutNoNetwork
								.setLayoutParams(new LinearLayout.LayoutParams(
										LinearLayout.LayoutParams.FILL_PARENT,
										LinearLayout.LayoutParams.WRAP_CONTENT));
					}
				} else {
					if (LayoutNoNetwork.getVisibility() == View.VISIBLE) {
						LayoutNoNetwork.setVisibility(View.INVISIBLE);
						LayoutNoNetwork
								.setLayoutParams(new LinearLayout.LayoutParams(
										1, 1));
					}
				}
				// textOutput.append(networkType + "dd");
				break;
			case 1241:
				data = msg.getData();
				String result = data.getString("value");
				JSONObject jsonObject;
				try {
					jsonObject = JSONObject.fromString(result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				JSONArray jsonArray;
				int currentVersion = jsonObject.getInt("version");
				try {
					jsonArray = jsonObject.getJSONArray("data");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				typeLoginList = new ArrayList<TypeLogin>();
				ArrayList<String> nameViewList = new ArrayList<String>();
				int i = jsonArray.length();
				while (i > 0) {
					jsonObject = jsonArray.getJSONObject(i - 1);
					TypeLogin typeLogin = new TypeLogin(editUser.getText()
							.toString(), "网络", jsonObject.getString("name"),
							jsonObject.getString("mac"),
							jsonObject.getString("ip"),
							jsonObject.getString("switchip"),
							jsonObject.getString("acid"),
							jsonObject.getString("enable"),
							jsonObject.getString("version"),
							jsonObject.getString("createtime"));

					typeLoginList.add(typeLogin);
					nameViewList.add(typeLogin.toString());

					i--;
				}

				for (i = 0; i < logins.length; i++) {
					String name = sharedPreferences.getString("mobile" + i
							+ "postscript", "");
					String mac = sharedPreferences.getString("mobile" + i
							+ "mac", "");
					String ip = sharedPreferences.getString(
							"mobile" + i + "ip", "");
					String switchip = sharedPreferences.getString("mobile" + i
							+ "switchip", "");
					String acid = sharedPreferences.getString("mobile" + i
							+ "acid", "");
					String enable = sharedPreferences.getInt("mobile" + i
							+ "enable", -1)
							+ "";

					TypeLogin typeLogin = new TypeLogin(editUser.getText()
							.toString(), "本地", name, mac, ip, switchip, acid,
							enable, currentVersion + "");

					if (typeLogin.valiable()) {
						int k = 0;
						boolean repeatAdd = false;
						while (k < typeLoginList.size()) {
							TypeLogin tempLogin = typeLoginList.get(k);
							if (tempLogin.getName().equals(typeLogin.getName())
									&& tempLogin.getMac().equals(
											typeLogin.getMac())) {
								repeatAdd = true;
							}

							k++;
						}

						if (repeatAdd == false) {
							typeLoginList.add(typeLogin);
							nameViewList.add(typeLogin.toString());
						}
					}

				}

				if (nameViewList.size() == 0) {
					Toast.makeText(getActivity(), "无任何有效环境", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				String[] items = (String[]) nameViewList
						.toArray(new String[nameViewList.size()]);
				resultBoolean = new boolean[items.length];
				for (int j = 0; j < resultBoolean.length; j++) {
					if (typeLoginList.get(j).isEnable()) {
						resultBoolean[j] = true;
					} else {
						resultBoolean[j] = false;

					}
				}

				AlertDialog builder = new AlertDialog.Builder(getActivity())
						.setTitle("您好，当前网络版本号：" + currentVersion)
						.setMultiChoiceItems(
								items,
								resultBoolean,
								new DialogInterface.OnMultiChoiceClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which, boolean isChecked) {
										// TODO Auto-generated method stub
										resultBoolean[which] = isChecked;
									}
								})
						.setPositiveButton("选择项覆盖到本地",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										int i = 0, j = 0;
										Editor editor = sharedPreferences
												.edit();
										while (i < logins.length
												&& j < resultBoolean.length) {
											if (resultBoolean[j] == true) {
												TypeLogin typeLogin = typeLoginList
														.get(j);
												editor.putString("mobile" + i
														+ "postscript",
														typeLogin.getName());
												editor.putString("mobile" + i
														+ "mac",
														typeLogin.getMac());
												editor.putString("mobile" + i
														+ "ip",
														typeLogin.getIp());
												editor.putString("mobile" + i
														+ "switchip",
														typeLogin.getSwitchip());
												editor.putString("mobile" + i
														+ "acid",
														typeLogin.getAcid());
												editor.putInt("mobile" + i
														+ "enable", Integer
														.parseInt(typeLogin
																.getEnable()));
												i++;
											}

											j++;
										}
										editor.commit();
										Toast.makeText(getActivity(), "重启软件生效",
												Toast.LENGTH_SHORT).show();

									}
								})
						.setNegativeButton("选择项覆盖到网络",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										new Thread(networkTask3).start();
									}
								}).setNeutralButton("取消", null).create();
				lv = builder.getListView();

				builder.show();
				break;

			}

		}
	};

	Runnable networkTask = new Runnable() {

		void postView(int what, String value) {
			Message msg;
			Bundle bundledata = new Bundle();
			msg = new Message();
			msg.what = what;
			bundledata.putString("value", value);
			msg.setData(bundledata);
			handler.sendMessage(msg);
		}

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			String result;
			int i = 0;

			do {
				if (i != 0) {
					postView(1231, "");
				}
				result = doPost(user, pass, mac, switchip, ip, acid);
				postView(1232, result);
				i++;
			} while (result.indexOf("其他位置的登陆已被注销") != -1);
			postView(1233, "OK");
		}
	};

	Runnable networkTask2 = new Runnable() {
		void postView(int what, String value) {
			Message msg;
			Bundle bundledata = new Bundle();
			msg = new Message();
			msg.what = what;
			bundledata.putString("value", value);
			msg.setData(bundledata);
			handler.sendMessage(msg);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String url = "http://whutchina.sinaapp.com/whutwlan/?user="
					+ editUser.getText().toString();
			String result = "";
			try {
				result = Pageservice.getHtml(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				postView(1233, "网络错误");
				return;
			}

			// postView(1233, result);

			postView(1241, result);
		}
	};

	Runnable networkTask3 = new Runnable() {
		void postView(int what, String value) {
			Message msg;
			Bundle bundledata = new Bundle();
			msg = new Message();
			msg.what = what;
			bundledata.putString("value", value);
			msg.setData(bundledata);
			handler.sendMessage(msg);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			postView(1251, "");
			for (int i = 0; i < resultBoolean.length; i++) {
				TypeLogin typeLogin = typeLoginList.get(i);
				if (resultBoolean[i] == true) {
					String result = typeLogin.upLoad();
					// postView(1233, result);
					if (result.indexOf("插入新数据成功") == -1) {
						postView(1233, typeLogin.getName() + " 上传失败");
					}

					progressStatus++;
					postView(1253, "");

				}

			}
			postView(1252, "");

			postView(1233, "上传成功");
		}
	};
	Runnable networkTask4 = new Runnable() {

		void postView(int what, String value) {
			Message msg;
			Bundle bundledata = new Bundle();
			msg = new Message();
			msg.what = what;
			bundledata.putString("value", value);
			msg.setData(bundledata);
			handler.sendMessage(msg);
		}

		private int getType() {
			int apnType = Pageservice.getAPNType(getActivity());
			int connect = apnType;
			if (apnType == 1) { // wifi

				String result = "";
				try {
					result = Pageservice
							.getHtml("http://whutchina.sinaapp.com/whutwlan/collection.php?user="
									+ editUser.getText().toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					connect = -1;
					return connect;
				}
				JSONObject jsonObject = null;

				try {
					jsonObject = JSONObject.fromString(result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					connect = -1;
					return connect;
				}
				if (jsonObject.getString("msg").equals("软件联网正常")) {
					connect = 1;
				} else {
					connect = -1;
				}
				return connect;

			}
			return connect;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (isRunning) {

				int netType = getType();

				if (isRunning) {
					postView(1261, netType + "");
					try {
						Thread.sleep(4 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View messageLayout = inflater.inflate(R.layout.mobile1_layout,
				container, false);

		editUser = (EditText) messageLayout.findViewById(R.id.mobile1_user);
		editPass = (EditText) messageLayout.findViewById(R.id.mobile1_pass);
		LayoutNoNetwork = (LinearLayout) messageLayout
				.findViewById(R.id.mobile1_nonetwork);
		editUser.setHorizontallyScrolling(true);
		editPass.setHorizontallyScrolling(true);
		btnSaveUserPass = (Button) messageLayout
				.findViewById(R.id.mobile1_btn_saveuserpass);
		btnSynchronized = (Button) messageLayout
				.findViewById(R.id.mobile1_btn_synchronized);

		logins[0] = (Button) messageLayout
				.findViewById(R.id.mobile1_button_login0);
		logins[1] = (Button) messageLayout
				.findViewById(R.id.mobile1_button_login1);
		logins[2] = (Button) messageLayout
				.findViewById(R.id.mobile1_button_login2);
		logins[3] = (Button) messageLayout
				.findViewById(R.id.mobile1_button_login3);
		logins[4] = (Button) messageLayout
				.findViewById(R.id.mobile1_button_login4);
		logins[5] = (Button) messageLayout
				.findViewById(R.id.mobile1_button_login5);
		logins[6] = (Button) messageLayout
				.findViewById(R.id.mobile1_button_login6);
		logins[7] = (Button) messageLayout
				.findViewById(R.id.mobile1_button_login7);
		logins[8] = (Button) messageLayout
				.findViewById(R.id.mobile1_button_login8);
		logins[9] = (Button) messageLayout
				.findViewById(R.id.mobile1_button_login9);
		for (int i = 0; i < logins.length; i++) {
			logins[i].setOnClickListener(this);
		}

		btnSynchronized.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String urlString = "";
				try {
					Pageservice.getHtml(urlString);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				new Thread(networkTask2).start();

			}
		});

		btnSaveUserPass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btnSaveUserPass.getText() == "锁定") {
					btnSaveUserPass.setText("解锁");
					editUser.setEnabled(false);
					editPass.setEnabled(false);
					editUser.setTextColor(Color.GRAY);
					editPass.setTextColor(Color.GRAY);
				} else {
					btnSaveUserPass.setText("锁定");
					editUser.setEnabled(true);
					editPass.setEnabled(true);
					editUser.setTextColor(Color.BLACK);
					editPass.setTextColor(Color.BLACK);
				}
			}
		});

		textOutput = (TextView) messageLayout
				.findViewById(R.id.mobile1_editOutput);

		textOutput.setMovementMethod(new ScrollingMovementMethod());

		textOutput.setFocusable(true);
		textOutput.setFocusableInTouchMode(true);
		textOutput.requestFocus();
		textOutput.requestFocusFromTouch();

		sharedPreferences = getActivity().getSharedPreferences("myconfig",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		editUser.setText(sharedPreferences.getString("mobile1user", ""));
		editPass.setText(sharedPreferences.getString("mobile1pass", ""));

		if (!("".equals(editUser.getText().toString()))) {
			btnSaveUserPass.setText("解锁");
			editUser.setEnabled(false);
			editPass.setEnabled(false);
			editUser.setTextColor(Color.GRAY);
			editPass.setTextColor(Color.GRAY);
		}

		for (int i = 0; i < logins.length; i++) {
			String name = sharedPreferences.getString("mobile" + i
					+ "postscript", "");

			if (isEnable(i)) {
				if (!(name.equals(""))) {
					logins[i].setText("[" + i + "]" + name);
				}

				int process = sharedPreferences.getInt("mobile" + i + "height",
						100);

				MyButtonAdjust myButtonAdjust = new MyButtonAdjust();
				myButtonAdjust.set(logins[i], i, process);
				logins[i].post(myButtonAdjust);

			} else {
				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) logins[i]
						.getLayoutParams(); // 控件textView当前的布局参数
				linearParams.height = 1;
				logins[i].setLayoutParams(linearParams); // 使设置好的布局参数应用到控件</pre>
				logins[i].setVisibility(View.INVISIBLE);
			}
		}

		editUser.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				editor.putString("mobile1user", editUser.getText().toString());
				editor.commit();
			}
		});
		editPass.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				editor.putString("mobile1pass", editPass.getText().toString());
				editor.commit();
			}
		});

		textOutput.append("载入完毕！");

		return messageLayout;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//editor.putBoolean("showFirstDialog", true);
		//editor.commit();
		if (firstStart()) {
			Intent intent = new Intent(getActivity(), Guide.class);
			startActivity(intent);

		} else if (showFirstDialog()) {

			AlertDialog builder = new AlertDialog.Builder(getActivity())
					.setTitle("免责声明")
					.setView(
							LayoutInflater.from(getActivity()).inflate(
									R.layout.dialog_firstshow, null))
					.setPositiveButton("我知道了",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									if (checkBoxDialog.isChecked()) {
										editor.putBoolean("showFirstDialog",
												false);
										editor.commit();
									}
								}
							}).create();
			builder.show();
			checkBoxDialog = (CheckBox) builder
					.findViewById(R.id.dialog_firstshow_checkbox);

		}
	}

	private boolean showFirstDialog() {

		if (sharedPreferences.getBoolean("showFirstDialog", true)) {

			return true;
		}
		return false;
	}

	private boolean firstStart() {
		PackageManager pm = getActivity().getPackageManager();
		String currentVersion = "";
		try {
			currentVersion = pm.getPackageInfo(getActivity().getPackageName(),
					0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!sharedPreferences.getString("firstStartVersion", "").equals(
				currentVersion)) {
			editor.putString("firstStartVersion", currentVersion);
			editor.commit();
			return true;
		}
		return false;

	}

	private boolean loginWhutWlan(String mobileN) {
		ip = sharedPreferences.getString(mobileN + "ip", "");
		mac = sharedPreferences.getString(mobileN + "mac", "");
		switchip = sharedPreferences.getString(mobileN + "switchip", "");
		acid = sharedPreferences.getString(mobileN + "acid", "");

		String name = sharedPreferences.getString(mobileN + "postscript", "");
		user = sharedPreferences.getString("mobile1user", "");
		pass = sharedPreferences.getString("mobile1pass", "");

		// textOutput.append("\r\n" + user + "|" + pass + "|" + mac + "|"
		// + switchip + "|" + ip + "|" + acid);
		if ("".equals(ip) || "".equals(mac) || "".equals(switchip)
				|| "".equals(acid) || "".equals(user) || "".equals(pass)) {
			Toast.makeText(getActivity(), "登录参数未填写完整", Toast.LENGTH_SHORT).show();
			return false;
		}
		textOutput.append("\r\n----------------------\r\n环境"
				+ (Integer.parseInt(mobileN.charAt(6) + "") - 1) + "" + "("
				+ name + "|" + mac + ")正在登录");

		for (int i = 0; i < logins.length; i++) {
			logins[i].setClickable(false);
		}
		new Thread(networkTask).start();

		// doPost(user, pass, mac, switchip, ip, acid);
		// textOutput.append("\r\n" +
		// 发送网络数据("http://172.30.16.53/cgi-bin/srun_portal",
		// "action=login","UTF-8",6000));
		// textOutput.append("\r\n" + doPost(user, pass, mac, switchip, ip,
		// acid));
		return true;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		isRunning = true;
		timerTest = new Thread(networkTask4);
		timerTest.start();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		isRunning = false;
	}

	public String doPost(String user, String pass, String mac, String switchip,
			String ip, String acid) {
		String uriAPI = "http://172.30.16.53/cgi-bin/srun_portal";// Post方式没有参数在这里
		String result = "";
		HttpPost httpRequst = new HttpPost(uriAPI);// 创建HttpPost对象

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "login"));
		params.add(new BasicNameValuePair("uid", "-1"));
		params.add(new BasicNameValuePair("is_pad", "0"));
		params.add(new BasicNameValuePair("force", "0"));
		params.add(new BasicNameValuePair("page_error", ""));
		params.add(new BasicNameValuePair("pop", "1"));
		params.add(new BasicNameValuePair("ac_type", "h3c"));
		params.add(new BasicNameValuePair("gateway_auth", "0"));
		params.add(new BasicNameValuePair("local_auth", "1"));
		params.add(new BasicNameValuePair("is_debug", "0"));
		params.add(new BasicNameValuePair("is_ldap", "0"));
		params.add(new BasicNameValuePair("page_succeed", ""));
		params.add(new BasicNameValuePair("save_me", "1"));
		params.add(new BasicNameValuePair("x", "65"));
		params.add(new BasicNameValuePair("y", "6"));
		params.add(new BasicNameValuePair("username", user));
		params.add(new BasicNameValuePair("password", pass));
		params.add(new BasicNameValuePair("user_ip", ip));
		params.add(new BasicNameValuePair("mac", mac));
		params.add(new BasicNameValuePair("nas_ip", switchip));
		params.add(new BasicNameValuePair("ac_id", acid));

		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams
					.setConnectionTimeout(httpParameters, 10 * 1000);// 设置请求超时10秒
			HttpConnectionParams.setSoTimeout(httpParameters, 10 * 1000); // 设置等待数据超时10秒

			httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient(httpParameters)
					.execute(httpRequst);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);// 取出应答字符串
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getMessage().toString();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getMessage().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getMessage().toString();
		} catch (Exception e) {

			result = e.getMessage().toString();
		}
		return result;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.mobile1_button_login0:
			loginWhutWlan("mobile0");
			break;
		case R.id.mobile1_button_login1:
			loginWhutWlan("mobile1");
			break;
		case R.id.mobile1_button_login2:
			loginWhutWlan("mobile2");
			break;
		case R.id.mobile1_button_login3:
			loginWhutWlan("mobile3");
			break;
		case R.id.mobile1_button_login4:
			loginWhutWlan("mobile4");
			break;
		case R.id.mobile1_button_login5:
			loginWhutWlan("mobile5");
			break;
		case R.id.mobile1_button_login6:
			loginWhutWlan("mobile6");
			break;
		case R.id.mobile1_button_login7:
			loginWhutWlan("mobile7");
			break;
		case R.id.mobile1_button_login8:
			loginWhutWlan("mobile8");
			break;
		case R.id.mobile1_button_login9:
			loginWhutWlan("mobile9");
			break;

		}
	}

	private boolean isEnable(int i) {
		int isEnable = sharedPreferences.getInt("mobile" + i + "enable", -1);

		switch (isEnable) {
		case -1:

			String mac = sharedPreferences.getString("mobile" + i + "mac", "");
			String name = sharedPreferences
					.getString("mobile" + i + "name", "");
			if (name.equals("") && mac.equals("")) {
				return false;
			} else {
				return true;
			}

		case 0:
			return false;
		case 1:
			return true;
		}
		return false;

	}

}

class MyButtonAdjust implements Runnable {
	private int i;
	private Button button;
	private int process;

	public void set(Button button, int i, int process) {
		this.button = button;
		this.i = i;
		this.process = process;
	}

	public void run() {
		int height = button.getHeight();
		height = process * height / 100;
		button.setHeight(height);
	}

}

class TypeLogin {
	private String user;
	private String source;
	private String name;
	private String mac;
	private String ip;
	private String switchip;
	private String acid;
	private String enable;
	private String version;
	private String createtime = "";

	public TypeLogin(String user, String source, String name, String mac,
			String ip, String switchip, String acid, String enable,
			String version) {
		this.user = user;
		this.source = source;
		this.name = name;
		this.mac = mac;
		this.ip = ip;
		this.switchip = switchip;
		this.acid = acid;
		this.enable = enable;
		this.version = version;
	}

	public TypeLogin(String user, String source, String name, String mac,
			String ip, String switchip, String acid, String enable,
			String version, String createtime) {
		this(user, source, name, mac, ip, switchip, acid, enable, version);
		this.createtime = createtime;

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (createtime.equals("")) {
			return "[" + source + "端]" + name + "\r\n(" + mac.substring(0, 5)
					+ "|" + ip + ")";
		} else {
			return "[" + source + "端]" + name + "\r\n(" + mac.substring(0, 5)
					+ "|" + ip + ")\r\n更新:" + createtime;
		}
	}

	boolean valiable() {

		if (name.equals("") || mac.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isEnable() {
		if (enable.equals("-1")) {
			return false;
		}
		if (enable.equals("0")) {
			return false;
		}
		if (enable.equals("1")) {
			return true;
		}
		return false;
	}

	public String getSource() {
		return source;
	}

	public String getName() {
		return name;
	}

	public String getMac() {
		return mac;
	}

	public String getIp() {
		return ip;
	}

	public String getSwitchip() {
		return switchip;
	}

	public String getAcid() {
		return acid;
	}

	public String getEnable() {
		return enable;
	}

	public String upLoad() {
		String url;
		try {
			url = "http://whutchina.sinaapp.com/whutwlan/?user=" + user
					+ "&name=" + URLEncoder.encode(name, "utf-8") + "&mac="
					+ mac + "&ip=" + ip + "&switchip=" + switchip + "&acid="
					+ acid + "&enable=" + enable + "&version="
					+ (Integer.parseInt(version) + 1);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
		String result = "";
		try {
			result = Pageservice.getHtml(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return result;

	}

}
