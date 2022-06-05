package com.branchard.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button loginBtn;
	private Button registerBtn, comeinBtn;
	private EditText name, psd;// 用户名和密码
	private CheckBox rememberPsdBox, autoLoginBox;// 记住密码、自动登陆复选框
	private ImageView head;
	private String headName;
	SharedPreferences loginPreferences;// 保存登陆信息
	SharedPreferences.Editor loginEditor;// 对应的编辑器
	String userName;// 用户名
	String userPsd;// 密码
	boolean isSavePsd, isAutoLogin;
	MyDatabaseHelper mydbHelper;

	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		putHeadToCard();

		name = (EditText) findViewById(R.id.userName);
		psd = (EditText) findViewById(R.id.userPassword);

		loginPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
		loginEditor = loginPreferences.edit();

		userName = loginPreferences.getString("name", null);
		userPsd = loginPreferences.getString("psd", null);
		isSavePsd = loginPreferences.getBoolean("isSavePsd", false);
		isAutoLogin = loginPreferences.getBoolean("isAutoLogin", false);

		if (isAutoLogin) {
			if (checkUser(userName, userPsd).equals("出错")) {
				Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("出错提示");
				builder.setMessage("用户名或密码错误！");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								psd.setText("");
							}
						});
				builder.create().show();
				loginEditor.putBoolean("isAutoLogin", false);
				loginEditor.commit();
				loadActivity();
			} else {

				MainActivity.this.setContentView(R.layout.activity_welcome);
				Toast.makeText(MainActivity.this, "登录成功！", Toast.LENGTH_SHORT)
						.show();
				this.setHead(userName);

				comeinBtn = (Button) findViewById(R.id.comein);
				comeinBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this,
								LookActivity.class);
						intent.putExtra("userId", userName);
						startActivity(intent);
						System.exit(0);
					}
				});
			}

		} else {
			loadActivity();
		}
	}

	public void loadActivity() {
		this.setContentView(R.layout.activity_main);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		rememberPsdBox = (CheckBox) findViewById(R.id.rememberPsdBox);
		autoLoginBox = (CheckBox) findViewById(R.id.autoLoginBox);
		name = (EditText) findViewById(R.id.userName);
		psd = (EditText) findViewById(R.id.userPassword);
		comeinBtn = (Button) findViewById(R.id.comein);
		if (isSavePsd) {
			psd.setText(userPsd);
			name.setText(userName);
			rememberPsdBox.setChecked(true);
		}
		loginBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loginEditor.putString("name", name.getText().toString());
				loginEditor.putString("psd", psd.getText().toString());
				loginEditor.putBoolean("isSavePsd", rememberPsdBox.isChecked());
				loginEditor.putBoolean("isAutoLogin", autoLoginBox.isChecked());
				loginEditor.commit();
				String checkResult = checkInfo();
				if (checkResult != null) {
					Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("出错提示");
					builder.setMessage(checkResult);
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									psd.setText("");
								}
							});
					builder.create().show();
				} else {
					String userId = name.getText().toString();
					String userPsd = psd.getText().toString();
					if (checkUser(userId, userPsd).equals("出错")) {
						Builder builder = new AlertDialog.Builder(
								MainActivity.this);
						builder.setTitle("出错提示");
						builder.setMessage("用户名或密码错误！");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										psd.setText("");
									}
								});
						builder.create().show();
					} else {
						MainActivity.this
								.setContentView(R.layout.activity_welcome);
						Toast.makeText(MainActivity.this, "登录成功！",
								Toast.LENGTH_SHORT).show();
						userName = name.getText().toString();
						setHead(userName);
						comeinBtn = (Button) findViewById(R.id.comein);
						comeinBtn.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(MainActivity.this,
										LookActivity.class);
								intent.putExtra("userId", name.getText()
										.toString());
								startActivity(intent);
								System.exit(0);
							}
						});
					}
				}

			}
		});

		registerBtn = (Button) findViewById(R.id.regisBtn);
		registerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						RegisterActivity.class);
				startActivity(intent);
				System.exit(0);
			}
		});
	}

	public String checkInfo() {
		name = (EditText) findViewById(R.id.userName);
		psd = (EditText) findViewById(R.id.userPassword);
		if (name.getText().toString() == null
				|| name.getText().toString().equals("")) {
			return "用户名不能为空";
		}
		if (psd.getText().toString() == null
				|| psd.getText().toString().equals("")) {
			return "密码不能为空";
		}
		return null;
	}

	public String checkUser(String name, String psd) {
		mydbHelper = new MyDatabaseHelper(MainActivity.this, "user.db", null, 1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

		Cursor cur = db.query("user_tb",
				new String[] { "userName", "passWord" }, "userId='" + name
						+ "'", null, null, null, null);

		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				String userName = cur.getString(0);
				String passWord = cur.getString(1);

				if (psd.equals(passWord)) {
					db.execSQL("update user_tb set isOnline='1' where userId='"
							+ name + "'");
					return userName;
				} else {
					return "出错";
				}
			}

		}
		return "出错";
	}

	public void setHead(String userId) {
		head = (ImageView) findViewById(R.id.head);
		mydbHelper = new MyDatabaseHelper(MainActivity.this, "user.db", null, 1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

		Cursor cur = db.query("user_tb", new String[] { "head" }, "userId='"
				+ userId + "'", null, null, null, null);

		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				headName = cur.getString(0);
			}

		}
		String path = "/sdcard/head/" + headName + ".jpg";
		File file = new File(path);
		if (file.exists()) {
			Bitmap bm = BitmapFactory.decodeFile(path);
			// 将图片显示到ImageView中
			head.setImageBitmap(bm);
		} else {
			head.setImageResource(R.drawable.head01);
		}
	}
	
	public void putHeadToCard(){
		String[] head={"head01.jpg","head02.jpg","head03.jpg","head04.jpg","head05.jpg","head06.jpg","head07.jpg","head08.jpg","head09.jpg","head10.jpg"};
		Bitmap[] bm={BitmapFactory.decodeResource(getResources(), R.drawable.head01),
				BitmapFactory.decodeResource(getResources(), R.drawable.head01),
				BitmapFactory.decodeResource(getResources(), R.drawable.head02),
				BitmapFactory.decodeResource(getResources(), R.drawable.head03),
				BitmapFactory.decodeResource(getResources(), R.drawable.head04),
				BitmapFactory.decodeResource(getResources(), R.drawable.head05),
				BitmapFactory.decodeResource(getResources(), R.drawable.head06),
				BitmapFactory.decodeResource(getResources(), R.drawable.head07),
				BitmapFactory.decodeResource(getResources(), R.drawable.head08),
				BitmapFactory.decodeResource(getResources(), R.drawable.head09),
				BitmapFactory.decodeResource(getResources(), R.drawable.head10)};
		for(int i=0;i<10;i++){
			File f = new File("/sdcard/head/", head[i]);
			if (f.exists()) {
				f.delete();
			}
			else{
				File f2 = new File("/sdcard/head/");
				f2.mkdirs();
			}
			try {
				FileOutputStream out = new FileOutputStream(f);
				bm[i].compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void onDestroy() {
		super.onDestroy();
		if (mydbHelper != null) {
			mydbHelper.close();
		}
	}

}
