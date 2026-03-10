package com.musicbase.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.musicbase.R;
import com.musicbase.preferences.Preferences;


public class DialogUtils {

	// 对话框数据
	public static Dialog dialog;
	public static int selected_item = -1;

	/**
	 * 显示对话框
	 * 
	 * @param id
	 *            对话框的类型
	 * @param title
	 *            对话框里的提示信息（进度条不需要设为null即可）
	 * @param listener
	 *            点击确定对应的操作（不需要任何操作设为null即可）
	 */
	@SuppressLint({ "NewApi", "CutPasteId" })
	public static void showMyDialog(final Context context, int id, String title, String message, OnClickListener listener) {
		dismissMyDialog();
		switch (id) {
			case Preferences.SHOW_PROGRESS_DIALOG:
				dialog = new Dialog(context, R.style.my_dialog);
				dialog.setContentView(R.layout.dialog_progress);
				int tv_progess = context.getResources().getIdentifier("tv_progess", "id", context.getPackageName());
				TextView textView = (TextView) dialog.findViewById(tv_progess);
				textView.setText(Html.fromHtml(message));
				dialog.setCancelable(true);
				dialog.show();
				break;
			case Preferences.SHOW_PROGRESS_DIALOG_NO:
				dialog = new Dialog(context, R.style.my_dialog);
				dialog.setContentView(R.layout.dialog_progress);
				int tv_progess1 = context.getResources().getIdentifier("tv_progess", "id", context.getPackageName());
				TextView textView1= (TextView) dialog.findViewById(tv_progess1);
				textView1.setText(Html.fromHtml(message));
				dialog.setCancelable(false);
				dialog.show();
				break;
			case Preferences.SHOW_ERROR_DIALOG:
				dialog = new Dialog(context, R.style.my_dialog);
				dialog.setContentView(R.layout.dialog_error);
				dialog.setCancelable(true);
				TextView tv_title01 = (TextView) dialog.findViewById(R.id.dialog_titile);
				tv_title01.setText(title);
				TextView textView01 = (TextView) dialog.findViewById(R.id.error_message);
				textView01.setText(message);

				Button button = (Button) dialog.findViewById(R.id.error_back);
				if (null == listener) {
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dismissMyDialog();
						}
					});
				} else {
					button.setOnClickListener(listener);
				}
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				break;
			case Preferences.SHOW_CONFIRM_DIALOG:
				dialog = new Dialog(context, R.style.my_dialog);
				dialog.setContentView(R.layout.dialog_login_jiesuo);
				TextView textView02 = (TextView) dialog.findViewById(R.id.dialog_titile);
				textView02.setText(Html.fromHtml(title));
				TextView textView03 = (TextView) dialog.findViewById(R.id.error_message);
				textView03.setText(Html.fromHtml(message));
				Button button01 = (Button) dialog.findViewById(R.id.btn_right);
                button01.setText("确认");
				Button button02 = (Button) dialog.findViewById(R.id.btn_left);
				button01.setOnClickListener(listener);
				button02.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dismissMyDialog();
					}
				});
				dialog.setCancelable(true);
				dialog.show();
				break;
			case Preferences.SHOW_BUTTON_DIALOG:
				dialog = new Dialog(context, R.style.my_dialog);
				dialog.setContentView(R.layout.dialog_button);
				Button button03 = (Button) dialog.findViewById(R.id.btn_right);
				button03.setText(title);
				Button button04 = (Button) dialog.findViewById(R.id.btn_left);
				button04.setText(message);
				button03.setOnClickListener(listener);
				button04.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dismissMyDialog();
					}
				});

				dialog.setCancelable(false);
				dialog.show();
				break;
			 case Preferences.SHOW_JIESUO_DIALOG:
	                dialog = new Dialog(context, R.style.my_dialog);
	                dialog.setContentView(R.layout.dialog_login_jiesuo);
	                Button button07 = (Button) dialog.findViewById(R.id.btn_right);
	                Button button08 = (Button) dialog.findViewById(R.id.btn_left);
	                button07.setOnClickListener(listener);
	                button08.setOnClickListener(new OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                        dismissMyDialog();
	                    }
	                });
	                dialog.setCancelable(true);
	                dialog.show();
	                break;
		}
	}

	// TODO 取消对话框
	public static void dismissMyDialog() {
		try {
			if (null != dialog) {
				dialog.cancel();
				dialog = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
