package com.branchard.android;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CommentsActivity extends Activity {
	private Button back, commbtn, goodBtn;
	private TextView user_Id, user_Name, _date, _body, goodnumberTv;
	private ImageView head;
	private LinearLayout toInFo;
	private String userId, userName, date, body, user_id, userComments,
			headName;
	private String topicId, sign, goodNumber;
	private ListView result;
	MyDatabaseHelper mydbHelper;

	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);

		Intent intent = getIntent();
		userId = intent.getStringExtra("userId");
		user_id = intent.getStringExtra("user_id");
		userName = intent.getStringExtra("userName");
		userComments = intent.getStringExtra("userComments");
		date = intent.getStringExtra("date");
		body = intent.getStringExtra("body");
		sign = intent.getStringExtra("sign");
		headName = intent.getStringExtra("headName");

		back = (Button) findViewById(R.id.back);
		commbtn = (Button) findViewById(R.id.commbtn);
		goodBtn = (Button) findViewById(R.id.isgood);
		user_Id = (TextView) findViewById(R.id.userId);
		user_Name = (TextView) findViewById(R.id.Qname);
		_date = (TextView) findViewById(R.id.look_date);
		_body = (TextView) findViewById(R.id.topic_content);
		goodnumberTv = (TextView) findViewById(R.id.goodnumber);
		head = (ImageView) findViewById(R.id.head);
		toInFo = (LinearLayout) findViewById(R.id.toinfo);
		result = (ListView) findViewById(R.id.result);

		user_Id.setText(user_id);
		user_Name.setText(userName);
		_date.setText(date);
		_body.setText(body);

		String path = "/sdcard/head/" + headName + ".jpg";
		File file = new File(path);
		if (file.exists()) {
			Bitmap bm = BitmapFactory.decodeFile(path);
			// 将图片显示到ImageView中
			head.setImageBitmap(bm);
		} else {
			head.setImageResource(R.drawable.head02);
		}

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sign.equals("backHome")) {
					Intent intent = new Intent(CommentsActivity.this,
							HomeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("userId", userId);
					startActivity(intent);
					System.exit(0);
				} else {
					Intent intent = new Intent(CommentsActivity.this,
							LookActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("userId", userId);
					startActivity(intent);
					System.exit(0);
				}
			}
		});

		toInFo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommentsActivity.this,
						OtherHomeActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("user_id", user_id);
				intent.putExtra("userName", userName);
				intent.putExtra("headName", headName);
				startActivity(intent);
			}
		});

		commbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommentsActivity.this,
						PublishActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("topicId", topicId);
				intent.putExtra("sign", "1");
				intent.putExtra("user_id", user_id);
				intent.putExtra("userName", userName);
				intent.putExtra("userComments", userComments);
				intent.putExtra("body", body);
				intent.putExtra("date", date);
				intent.putExtra("headName", headName);
				startActivity(intent);
				System.exit(0);
			}
		});

		setTopicIdAndGoodNum(user_id, date, body);

		// 点赞
		if (isGood()) {
			goodBtn.setText("已  赞");
		}
		goodBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (goodBtn.getText().equals("精  辟")) {
					addGood();
					goodBtn.setText("已  赞");
				} else {
					ridGood();
					goodBtn.setText("精  辟");
				}
			}
		});

		// 显示评论
		mydbHelper = new MyDatabaseHelper(CommentsActivity.this, "comments.db",
				null, 1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

		SimpleAdapter adapter = new ImageSimpleAdapter(this, getData(db),
				R.layout.listview_comments, new String[] { "date", "body",
						"userName", "userId", "imageHead" }, new int[] {
						R.id.look_date, R.id.topic_content, R.id.Qname,
						R.id.userId, R.id.head });
		result.setAdapter(adapter);

		// 高度自适应
		ListAdapter listAdapter = result.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, result);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = result.getLayoutParams();
		params.height = totalHeight
				+ (result.getDividerHeight() * (listAdapter.getCount() - 1));
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		result.setLayoutParams(params);

	}

	private List<Map<String, Object>> getData(SQLiteDatabase db) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Cursor cur = db.query("comments_tb", new String[] { "date", "body",
				"userName", "userId" }, "topicId='" + topicId + "'", null,
				null, null, "_id desc");
		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				String date = cur.getString(0);
				String body = cur.getString(1);
				String userName = cur.getString(2);
				String userId = cur.getString(3);
				map = new HashMap<String, Object>();
				map.put("date", date);
				map.put("body", body);
				map.put("userName", userName);
				map.put("userId", userId);
				String path = "/sdcard/head/" + getHeadName(userId) + ".jpg";
				Bitmap bm = BitmapFactory.decodeFile(path);
				map.put("imageHead", bm);
				list.add(map);
			}
		}
		return list;
	}

	public String getHeadName(String userId) {
		String head_name = "";
		mydbHelper = new MyDatabaseHelper(CommentsActivity.this, "user.db",
				null, 1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

		Cursor cur = db.query("user_tb", new String[] { "head" }, "userId='"
				+ userId + "'", null, null, null, null);
		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				head_name = cur.getString(0);
			}
		}
		return head_name;
	}

	public void setTopicIdAndGoodNum(String user_id, String date, String body) {
		mydbHelper = new MyDatabaseHelper(CommentsActivity.this, "topic.db",
				null, 1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库
		Cursor cur = db.query("topic_tb", new String[] { "_id", "goodNumber" },
				"user_id=? and date=? and body=?", new String[] { user_id,
						date, body }, null, null, null);

		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				this.topicId = Integer.toString(cur.getInt(0));
				this.goodNumber = cur.getString(1);
			}
		}
		goodnumberTv.setText(goodNumber + " 人觉得精辟");

	}

	public Boolean isGood() {
		mydbHelper = new MyDatabaseHelper(CommentsActivity.this, "zan.db",
				null, 1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

		Cursor cur = db.query("zan_tb", new String[] { "userId" }, "topicId='"
				+ topicId + "'", null, null, null, null);
		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				String zanId = cur.getString(0);
				if (zanId.equals(userId)) {
					return true;
				}
			}
		}
		return false;
	}

	public void addGood() {
		mydbHelper = new MyDatabaseHelper(CommentsActivity.this, "zan.db",
				null, 1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

		int id = Integer.parseInt(topicId);
		db.execSQL("insert into zan_tb values(null,?,?)", new String[] {
				topicId, userId });
		goodNumber = String.valueOf(Integer.parseInt(goodNumber) + 1);
		goodnumberTv.setText(goodNumber + " 人觉得精辟");

		mydbHelper = new MyDatabaseHelper(CommentsActivity.this, "topic.db",
				null, 1);// 创建数据库辅助类
		SQLiteDatabase db2 = mydbHelper.getReadableDatabase();// 获取SQLite数据库
		// 点赞人数数增加
		db2.execSQL("update topic_tb set goodNumber=? where _id=" + id,
				new String[] { goodNumber });
	}

	public void ridGood() {
		mydbHelper = new MyDatabaseHelper(CommentsActivity.this, "fans.db",
				null, 1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库
		int id = Integer.parseInt(topicId);
		db.execSQL("delete from zan_tb where topicId=? and userId=?",
				new String[] { topicId, userId });
		goodNumber = String.valueOf(Integer.parseInt(goodNumber) - 1);
		goodnumberTv.setText(goodNumber + " 人觉得精辟");

		mydbHelper = new MyDatabaseHelper(CommentsActivity.this, "topic.db",
				null, 1);// 创建数据库辅助类
		SQLiteDatabase db2 = mydbHelper.getReadableDatabase();// 获取SQLite数据库
		// 点赞人数减少
		db2.execSQL("update topic_tb set goodNumber=? where _id=" + id,
				new String[] { goodNumber });
	}

	protected void onDestroy() {
		if (mydbHelper != null) {
			mydbHelper.close();
		}
	}
}
