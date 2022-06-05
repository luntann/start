package com.branchard.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class RegisterActivity extends Activity {

	private Button sureBtn, cancelBtn;
	private EditText name, id, passWord, passAgain;
	private RadioButton male;
	MyDatabaseHelper mydbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		sureBtn = (Button) findViewById(R.id.regi_sureBtn);
		cancelBtn = (Button) findViewById(R.id.register_cancel);
		id = (EditText) findViewById(R.id.name);
		name = (EditText) findViewById(R.id.Qname);
		passWord = (EditText) findViewById(R.id.psd1);
		passAgain = (EditText) findViewById(R.id.psd2);
		male = (RadioButton) findViewById(R.id.male);

		// 提交注册信息
		sureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String passW = passWord.getText().toString();
				String checkResult = checkInfo();
				if (checkResult != null) {
					Builder builder = new AlertDialog.Builder(
							RegisterActivity.this);
					builder.setTitle("出错提示");
					builder.setMessage(checkResult);
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									passWord.setText("");
									passAgain.setText("");
								}
							});
					builder.create().show();
				} else {
					String userId = id.getText().toString();
					String userName = name.getText().toString();
					String gender = male.isChecked() ? "男" : "女";
					mydbHelper = new MyDatabaseHelper(RegisterActivity.this,
							"user.db", null, 1);// 创建数据库辅助类
					SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库
					addUser(db, userId, userName, passW, gender);// 向用户表插入数据
					Builder builder = new AlertDialog.Builder(
							RegisterActivity.this);
					builder.setTitle("恭喜！");
					builder.setMessage("注册成功！");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											RegisterActivity.this,
											MainActivity.class);
									startActivity(intent);
									System.exit(0);
								}
							});
					builder.create().show();
				}
			}
		});

		// 取消
		cancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);
				System.exit(0);
			}
		});
	}

	// 检查输入格式
	public String checkInfo() {
		// System.out.println(name);
		if (id.getText().toString() == null
				|| id.getText().toString().equals("")) {
			System.out.println("***********");
			return "账户不能为空";
		}
		if (name.getText().toString() == null
				|| name.getText().toString().equals("")) {
			System.out.println("***********");
			return "用户名不能为空";
		}
		if (passWord.getText().toString().trim().length() < 6
				|| passWord.getText().toString().trim().length() > 15) {
			return "密码位数应该6~15之间";
		}
		if (!passWord.getText().toString()
				.equals(passAgain.getText().toString())) {
			return "两次输入的密码不一致";
		}
		return null;
	}

	// 增加用户
	public void addUser(SQLiteDatabase db, String userId, String userName,
			String passW, String sex) {
		db.execSQL("insert into user_tb values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
				new String[] { null, userId, userName, passW, "未设置", "未设置",
						sex, "0", "head", "0", "0", "0", "0" });
	}

	protected void onDestroy() {
		super.onDestroy();
		if (mydbHelper != null) {
			mydbHelper.close();
		}
	}
}
