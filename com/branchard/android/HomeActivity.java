package com.branchard.android;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private Button lookBtn;
	private Button myselfBtn, publishBtn;
	private ListView result;
	private String userId, sign;
	private TextView userName, mood;
	private ImageView head;
	private String date2, body2, headName;
	MyDatabaseHelper mydbHelper;
	SimpleAdapter adapter;

	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		CloseActivityClass.activityList.add(this);
		setContentView(R.layout.activity_myhome);

		lookBtn = (Button) findViewById(R.id.lookBtn);
		myselfBtn = (Button) findViewById(R.id.myselfBtn);
		publishBtn = (Button) findViewById(R.id.publishBtn);
		result = (ListView) findViewById(R.id.result);

		Intent intent = getIntent();
		userId = intent.getStringExtra("userId");
		ShowMes(userId);

		lookBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						LookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});

		myselfBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						MyselfActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});

		publishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						PublishActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("sign", "0");
				intent.putExtra("isBackLook", "no");
				startActivity(intent);
				System.exit(0);
			}
		});

		mydbHelper = new MyDatabaseHelper(HomeActivity.this, "topic.db", null,
				1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

		adapter = new ImageSimpleAdapter(this, getData(db, userId),
				R.layout.activity_list, new String[] { "date", "body",
						"commNumber" }, new int[] { R.id.topic_date,
						R.id.topic_content, R.id.commentTv });
		result.setAdapter(adapter);
		result.setOnItemClickListener(new ItemClickEvent());
		result.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				HashMap<String, String> map = (HashMap<String, String>) result
						.getItemAtPosition(arg2);

				date2 = map.get("date");
				body2 = map.get("body");

				Builder builder = new AlertDialog.Builder(HomeActivity.this);
				builder.setTitle("提示");
				builder.setMessage("确认删除？");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								DeleteTopic(date2, body2, userId);
								ridTopicNumber();
								Intent intent = new Intent(HomeActivity.this,
										HomeActivity.class);
								Toast.makeText(HomeActivity.this, "删除成功！",
										Toast.LENGTH_SHORT).show();
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								intent.putExtra("userId", userId);
								startActivity(intent);
								System.exit(0);
							}
						});
				builder.create().show();
				return true;
			}
		});

	}

	public class ItemClickEvent implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			HashMap<String, String> map = (HashMap<String, String>) result
					.getItemAtPosition(arg2);
			Intent intent = new Intent(HomeActivity.this,
					CommentsActivity.class);
			intent.putExtra("userId", userId);
			intent.putExtra("sign", "backHome");
			intent.putExtra("user_id", userId);
			intent.putExtra("userName", userName.getText());
			intent.putExtra("userComments", userName.getText());
			intent.putExtra("body", map.get("body"));
			intent.putExtra("date", map.get("date"));
			intent.putExtra("headName", headName);
			startActivity(intent);
		}

	}

	public void DeleteTopic(String date, String body, String userId) {
		mydbHelper = new MyDatabaseHelper(HomeActivity.this, "topic.db", null,
				1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

		db.execSQL(
				"delete from topic_tb where body=? and date=? and user_id=?",
				new String[] { body, date, userId });
	}

	public void ShowMes(String userId) {
		userName = (TextView) findViewById(R.id.Qname);
		mood = (TextView) findViewById(R.id.qianName);
		head = (ImageView) findViewById(R.id.head);

		mydbHelper = new MyDatabaseHelper(HomeActivity.this, "user.db", null, 1);// 创建数据库辅助类
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

	private List<Map<String, Object>> getData(SQLiteDatabase db, String userId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Cursor cur = db.query("topic_tb", new String[] { "date", "body",
				"commNumber" }, "user_id='" + userId + "'", null, null, null,
				"_id desc");
		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				String date = cur.getString(0);
				String body = cur.getString(1);
				String number = cur.getString(2) + " 人评论";
				map = new HashMap<String, Object>();
				map.put("date", date);
				map.put("body", body);
				map.put("commNumber", number);
				list.add(map);
			}
		}
		return list;
	}

	public void ridTopicNumber() {
		String number = "";
		mydbHelper = new MyDatabaseHelper(HomeActivity.this, "user.db", null, 1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

		Cursor cur = db.query("user_tb", new String[] { "topicNumber" },
				"userId='" + userId + "'", null, null, null, null);
		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				number = cur.getString(0);
			}
		}
		number = String.valueOf(Integer.parseInt(number) - 1);
		db.execSQL("update user_tb set topicNumber=? where userId=?",
				new String[] { number, userId });
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

	protected void onResume() {
		super.onResume();
		result.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (mydbHelper != null) {
			mydbHelper.close();
		}
	}

}
