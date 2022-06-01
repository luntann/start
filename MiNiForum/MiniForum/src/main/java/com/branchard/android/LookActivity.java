package com.branchard.android;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LookActivity extends Activity {
	private Button myHomeBtn, myselfBtn, publishBtn;
	private ListView result;
	private TextView userName, mood;
	private String userId, name, headName;
	private ImageView head;
	MyDatabaseHelper mydbHelper;

	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		CloseActivityClass.activityList.add(this);
		setContentView(R.layout.activity_look);

		myHomeBtn = (Button) findViewById(R.id.homeBtn);
		myselfBtn = (Button) findViewById(R.id.myselfBtn);
		result = (ListView) findViewById(R.id.result);
		publishBtn = (Button) findViewById(R.id.publishBtn);

		// 获取传递信息
		Intent intent = getIntent();
		userId = intent.getStringExtra("userId");

		myHomeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LookActivity.this,
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});

		myselfBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LookActivity.this,
						MyselfActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});

		publishBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LookActivity.this,
						PublishActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("sign", "0");
				intent.putExtra("isBackLook", "yes");
				startActivity(intent);
				System.exit(0);
			}
		});

		mydbHelper = new MyDatabaseHelper(LookActivity.this, "topic.db", null,
				1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库
		ShowMes(userId);// 显示用户信息

		// 配置适配器显示话题列表
		SimpleAdapter adapter = new ImageSimpleAdapter(this, getData(db),
				R.layout.listview_look, new String[] { "date", "body",
						"userName", "user_id", "imageHead", "commNumber" },
				new int[] { R.id.look_date, R.id.topic_content, R.id.Qname,
						R.id.userId, R.id.head, R.id.commentTv });
		result.setAdapter(adapter);
		result.setOnItemClickListener(new ItemClickEvent());

	}

	// 点击进入该话题评论界面
	public class ItemClickEvent implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			HashMap<String, String> map = (HashMap<String, String>) result
					.getItemAtPosition(arg2);
			Intent intent = new Intent(LookActivity.this,
					CommentsActivity.class);
			intent.putExtra("userId", userId);
			intent.putExtra("sign", "backLook");
			intent.putExtra("user_id", map.get("user_id"));
			intent.putExtra("userName", map.get("userName"));
			intent.putExtra("userComments", userName.getText());
			intent.putExtra("body", map.get("body"));
			intent.putExtra("date", map.get("date"));

			mydbHelper = new MyDatabaseHelper(LookActivity.this, "user.db",
					null, 1);// 创建数据库辅助类
			SQLiteDatabase db2 = mydbHelper.getReadableDatabase();// 获取SQLite数据库
			setNameAndHead(db2, map.get("user_id"));
			intent.putExtra("headName", headName);
			startActivity(intent);
		}

	}

	public void ShowMes(String userId) {
		userName = (TextView) findViewById(R.id.Qname);
		mood = (TextView) findViewById(R.id.qianName);
		head = (ImageView) findViewById(R.id.head);

		mydbHelper = new MyDatabaseHelper(LookActivity.this, "user.db", null, 1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库
		Cursor cur = db.query("user_tb", new String[] { "userName", "mood",
				"head" }, "userId='" + userId + "'", null, null, null, null);

		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				userName.setText(cur.getString(0));
				mood.setText(cur.getString(1));
				headName = cur.getString(2);
				String path = "/sdcard/head/" + headName + ".jpg";
				File file = new File(path);
				if (file.exists()) {
					Bitmap bm = BitmapFactory.decodeFile(path);
					// 将图片显示到ImageView中
					head.setImageBitmap(bm);
				} else {
					head.setImageResource(R.drawable.head);
				}
			}
		}
	}

	private List<Map<String, Object>> getData(SQLiteDatabase db) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Cursor cur = db.query("topic_tb", new String[] { "date", "body",
				"user_id", "commNumber" }, null, null, null, null, "_id desc");
		mydbHelper = new MyDatabaseHelper(LookActivity.this, "user.db", null, 1);// 创建数据库辅助类
		SQLiteDatabase db2 = mydbHelper.getReadableDatabase();// 获取SQLite数据库
		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				String date = cur.getString(0);
				String body = cur.getString(1);
				String user_id = cur.getString(2);
				String commNumber = cur.getString(3) + " 人评论";
				map = new HashMap<String, Object>();
				map.put("date", date);
				map.put("body", body);
				this.setNameAndHead(db2, user_id);
				map.put("userName", name);
				map.put("user_id", user_id);
				map.put("commNumber", commNumber);
				String path = "/sdcard/head/" + headName + ".jpg";
				Bitmap bm = BitmapFactory.decodeFile(path);
				map.put("imageHead", bm);
				list.add(map);
			}
		}
		return list;
	}

	public void setNameAndHead(SQLiteDatabase db, String userId) {

		Cursor cur = db.query("user_tb", new String[] { "userName", "head" },
				"userId='" + userId + "'", null, null, null, null);
		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				this.name = cur.getString(0);
				this.headName = cur.getString(1);
			}
		}

	}

	private void showTips() {

		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提醒")
				.setMessage("是否退出程序")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_HOME);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}

				}).setNegativeButton("取消",

				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).create(); // 创建对话框
		alertDialog.show(); // 显示对话框
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showTips();
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	protected void onDestroy() {
		if (mydbHelper != null) {
			mydbHelper.close();
		}
		super.onDestroy();
	}

}
