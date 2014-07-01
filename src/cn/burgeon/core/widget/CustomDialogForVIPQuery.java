package cn.burgeon.core.widget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.burgeon.core.R;

public class CustomDialogForVIPQuery {

    private Dialog ad;
    private Context mContext;

    private EditText cardNO, mobileNO;
    private EditText startTime, endTime;
    private Button okButton, cancelButton;

    private Calendar c = Calendar.getInstance();

    private CustomDialogForVIPQuery(Context context) {
        super();
        this.mContext = context;
        View customView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_for_vip_query, null);
        ad = new Dialog(mContext, R.style.CheckDialog);
        ad.setContentView(customView);
        ad.show();

        cardNO = (EditText) customView.findViewById(R.id.cardNo);
        mobileNO = (EditText) customView.findViewById(R.id.mobileNo);

        startTime = (EditText) customView.findViewById(R.id.startTime);
        Date currDate = new Date();
        startTime.setText(new SimpleDateFormat("yyyyMMdd").format(currDate));
        startTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int startmYear = c.get(Calendar.YEAR);
                int startmMonth = c.get(Calendar.MONTH);
                int startmDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog startdialog = new DatePickerDialog(mContext, new startTimeDateSetListener(), startmYear, startmMonth,
                        startmDay);
                startdialog.show();
            }
        });
        endTime = (EditText) customView.findViewById(R.id.endTime);
        endTime.setText(new SimpleDateFormat("yyyyMMdd").format(currDate));
        endTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int startmYear = c.get(Calendar.YEAR);
                int startmMonth = c.get(Calendar.MONTH);
                int startmDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog enddialog = new DatePickerDialog(mContext, new endmDateSetListener(), startmYear, startmMonth, startmDay);
                enddialog.show();
            }
        });

        okButton = (Button) customView.findViewById(R.id.okButton);
        cancelButton = (Button) customView.findViewById(R.id.cancelButton);
    }

    /**
     * 按键监听
     *
     * @param listener
     */
    public void setOnKeyListener(OnKeyListener listener) {
        ad.setOnKeyListener(listener);
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        try {
            ad.dismiss();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void setOKButton(CharSequence title, OnClickListener listener) {
        okButton.setText(title);
        okButton.setOnClickListener(listener);
    }

    public void setCancelButton(CharSequence title, OnClickListener listener) {
        cancelButton.setText(title);
        cancelButton.setOnClickListener(listener);
    }

    /**
     * 弹出对话框的时候 点击Back健是否取消对话框
     */
    public void setCancelable(boolean flag) {
        ad.setCancelable(flag);
    }

    /**
     * 如果你触摸屏幕其它区域,消失Dialog
     *
     * @param flag
     */
    public void setCanceledOnTouchOutside(boolean flag) {
        ad.setCanceledOnTouchOutside(flag);
    }

    /**
     * 判断Ｄｉａｌｏｇ是否在显示
     *
     * @return
     */
    public boolean isShowing() {
        return ad.isShowing();
    }

    public static class Builder {
        private AlertParams params;

        public Builder(Context context) {
            params = new AlertParams(context);
        }

        public Builder setTitle(CharSequence title) {
            params.mTitle = title;
            return this;
        }

        public Builder setPositiveButton(CharSequence title, OnClickListener listener) {
            params.mOKButtonText = title;
            params.mOKButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence title, OnClickListener listener) {
            params.mCancelButtonText = title;
            params.mCancelButtonListener = listener;
            return this;
        }

        public CustomDialogForVIPQuery show() {
            CustomDialogForVIPQuery dialog = new CustomDialogForVIPQuery(params.context);
            dialog.setOKButton(params.mOKButtonText, params.mOKButtonListener);
            dialog.setCancelButton(params.mCancelButtonText, params.mCancelButtonListener);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }

    public static class AlertParams {
        public Context context;

        public CharSequence mTitle;
        public CharSequence mOKButtonText;
        public CharSequence mCancelButtonText;
        public OnClickListener mOKButtonListener;
        public OnClickListener mCancelButtonListener;
        public String[] stateVals;

        public AlertParams(Context context) {
            this.context = context;
        }
    }

    class startTimeDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            // Month is 0 based so add 1

            String month = String.valueOf(mMonth + 1).length() == 2 ? String.valueOf(mMonth + 1) : "0" + String.valueOf(mMonth + 1);
            String day = String.valueOf(mDay).length() == 2 ? String.valueOf(mDay) : "0" + String.valueOf(mDay);
            startTime.setText(new StringBuilder().append(mYear).append(month).append(day));
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
            endTime.setText(new StringBuilder().append(mYear).append(month).append(day));
        }
    }

    public String getCardNo() {
        return cardNO.getText().toString();
    }
    
    public String getMobileNo() {
        return mobileNO.getText().toString();
    }

    public String getStartTime() {
        return startTime.getText().toString();
    }

    public String getEndTime() {
        return endTime.getText().toString();
    }

}
