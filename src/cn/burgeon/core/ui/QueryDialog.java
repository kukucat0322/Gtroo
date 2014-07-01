package cn.burgeon.core.ui;

import java.util.Calendar;

import cn.burgeon.core.R;
import android.R.color;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;

public class QueryDialog extends AlertDialog {

	Context mContext;
	EditText startDate, endDate;

	public QueryDialog(Context context) {
		super(context);
	}

	public QueryDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.querydialog);
		this.setTitle("查询");
		Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.width = 400; // 宽度
        lp.height = 300; // 高度
        lp.x = 40; // 新位置X坐标
        lp.y = 200; // 新位置Y坐标
        lp.alpha = 0.8f; // 透明度
        dialogWindow.setAttributes(lp);
        
		startDate = (EditText) findViewById(R.id.queryDialogStarDate);
		startDate.setOnClickListener(onClickListener);
		endDate = (EditText) findViewById(R.id.queryDialogEndDate);
		endDate.setOnClickListener(onClickListener);
	}
	
	Calendar c = Calendar.getInstance();
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
	        case R.id.queryDialogStarDate:
	            int startmYear = c.get(Calendar.YEAR);
	            int startmMonth = c.get(Calendar.MONTH);
	            int startmDay = c.get(Calendar.DAY_OF_MONTH);
	            DatePickerDialog startdialog = new DatePickerDialog(mContext, new startmDateSetListener(), startmYear, startmMonth, startmDay);
	            startdialog.show();
	            break;
	        case R.id.queryDialogEndDate:
	            int endmYear = c.get(Calendar.YEAR);
	            int endmMonth = c.get(Calendar.MONTH);
	            int endmDay = c.get(Calendar.DAY_OF_MONTH);
	            DatePickerDialog enddialog = new DatePickerDialog(mContext, new endmDateSetListener(), endmYear, endmMonth, endmDay);
	            enddialog.show();
	            break;
			}
		}
	};
	
    class startmDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            // Month is 0 based so add 1

            String month = String.valueOf(mMonth + 1).length() == 2 ? String.valueOf(mMonth + 1) : "0" + String.valueOf(mMonth + 1);
            String day = String.valueOf(mDay).length() == 2 ? String.valueOf(mDay) : "0" + String.valueOf(mDay);
            startDate.setText(new StringBuilder().append(mYear).append(month).append(day));
        }
    }
    
    class endmDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            // Month is 0 based so add 1

            String month = String.valueOf(mMonth + 1).length() == 2 ? String.valueOf(mMonth + 1) : "0" + String.valueOf(mMonth + 1);
            String day = String.valueOf(mDay).length() == 2 ? String.valueOf(mDay) : "0" + String.valueOf(mDay);
            endDate.setText(new StringBuilder().append(mYear).append(month).append(day));
        }
    }

}
