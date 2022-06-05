package com.branchard.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MyselfActivity extends Activity {

	private RelativeLayout switchAvatar;
	private ImageView faceImage;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private Button myHomeBtn;
	private Button lookBtn;
	private Button reLoginBtn;
	private Button endBtn, alterBtn, topicNumberBtn, focusNumberBtn,
			fansNumberBtn;
	private EditText mood, userName, passW, birthday, Id, sex;
	private ImageView head;
	private String userId, headName;
	private String topicNumber, focusNumber, fansNumber;
	private String IMAGE_FILE_NAME;
	private String newheadpath;

	SharedPreferences loginPreferences;
	SharedPreferences.Editor loginEditor;
	boolean isAutoLogin;
	MyDatabaseHelper mydbHelper;

	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		CloseActivityClass.activityList.add(this);
		setContentView(R.layout.activity_myself);

		myHomeBtn = (Button) findViewById(R.id.homeBtn);
		lookBtn = (Button) findViewById(R.id.lookBtn);
		reLoginBtn = (Button) findViewById(R.id.reloginBtn);
		endBtn = (Button) findViewById(R.id.endBtn);
		alterBtn = (Button) findViewById(R.id.alterBtn);
		topicNumberBtn = (Button) findViewById(R.id.postNumber);
		focusNumberBtn = (Button) findViewById(R.id.focusNumber);
		fansNumberBtn = (Button) findViewById(R.id.fansNumber);
		mood = (EditText) findViewById(R.id.qianWord);
		userName = (EditText) findViewById(R.id.Qname);
		passW = (EditText) findViewById(R.id.psd);
		birthday = (EditText) findViewById(R.id.birthday);
		Id = (EditText) findViewById(R.id.name);
		sex = (EditText) findViewById(R.id.sex);
		head = (ImageView) findViewById(R.id.head);

		Intent intent = getIntent();
		userId = intent.getStringExtra("userId");

		topicNumberBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MyselfActivity.this,
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});

		birthday.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();// 获取当前日期
				new DatePickerDialog(MyselfActivity.this,// 日期选择器对话框
						new DatePickerDialog.OnDateSetListener() {// 日期改变监听器
							public void onDateSet(DatePicker view, int year,
									int month, int day) {
								birthday.setText(year + "-" + (month + 1) + "-"
										+ day);
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		alterBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String mymood = mood.getText().toString();
				String userN = userName.getText().toString();
				String passWord = passW.getText().toString();
				String birth = birthday.getText().toString();

				mydbHelper = new MyDatabaseHelper(MyselfActivity.this,
						"user.db", null, 1);// 创建数据库辅助类
				SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库
				updateUser(db, mymood, userN, passWord, birth, userId);
				Toast.makeText(MyselfActivity.this, "修改成功,请点击重新登录！",
						Toast.LENGTH_SHORT).show();
			}
		});

		myHomeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyselfActivity.this,
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});

		lookBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyselfActivity.this,
						LookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});

		reLoginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginPreferences = getSharedPreferences("login",
						Context.MODE_PRIVATE);
				loginEditor = loginPreferences.edit();
				loginEditor.putBoolean("isAutoLogin", false);
				loginEditor.commit();

				Intent intent = new Intent(MyselfActivity.this,
						MainActivity.class);
				startActivity(intent);
				CloseActivityClass.exitClient(MyselfActivity.this);
			}
		});

		endBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		showMyself(userId);
		head.setOnClickListener(listener);
	}

	private void showTips() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提醒")
				.setMessage("是否退出程序?")
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

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			showDialog();
		}
	};

	public void showMyself(String userId) {
		mydbHelper = new MyDatabaseHelper(MyselfActivity.this, "user.db", null,
				1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

		Cursor cur = db.query("user_tb", new String[] { "mood", "userId",
				"userName", "passWord", "birthday", "sex", "head",
				"likeNumber", "fansNumber", "topicNumber" }, "userId='"
				+ userId + "'", null, null, null, null);
		while (cur.moveToNext()) {
			for (int i = 0; i < cur.getCount(); i++) {
				cur.moveToPosition(i);
				mood.setText(cur.getString(0));
				Id.setText(cur.getString(1));
				userName.setText(cur.getString(2));
				passW.setText(cur.getString(3));
				birthday.setText(cur.getString(4));
				sex.setText(cur.getString(5));
				headName = cur.getString(6);
				focusNumber = cur.getString(7);
				fansNumber = cur.getString(8);
				topicNumber = cur.getString(9);

				IMAGE_FILE_NAME = userId + cur.getString(2) + "head";
				newheadpath = userId + cur.getString(2) + "head.jpg";
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
		focusNumberBtn.setText(focusNumber);
		fansNumberBtn.setText(fansNumber);
		topicNumberBtn.setText(topicNumber);

	}

	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Tools.hasSdcard()) {

								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory()
									+ IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(MyselfActivity.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 *
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			head.setImageDrawable(drawable);
			saveCroppedImage(photo);
			saveHead();
		}
	}

	public void updateUser(SQLiteDatabase db, String mymood, String userN,
			String passWord, String birth, String userId) {
		db.execSQL(
				"update user_tb set mood=?,userName=?,passWord=?,birthday=? where userId=?",
				new String[] { mymood, userN, passWord, birth, userId });
	}

	public void saveHead() {
		mydbHelper = new MyDatabaseHelper(MyselfActivity.this, "user.db", null,
				1);// 创建数据库辅助类
		SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库
		db.execSQL("update user_tb set head=? where userId=?", new String[] {
				IMAGE_FILE_NAME, userId });
	}

	// 将图片保存到指定路径
	public void saveCroppedImage(Bitmap bm) {
		File f = new File("/sdcard/head/", newheadpath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			Toast.makeText(MyselfActivity.this, "头像更换成功,请点击重新登录！",
					Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (mydbHelper != null) {
			mydbHelper.close();
		}
	}

}
