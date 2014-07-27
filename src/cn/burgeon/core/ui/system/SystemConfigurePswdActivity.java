package cn.burgeon.core.ui.system;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.ui.BaseActivity;
import cn.burgeon.core.utils.PreferenceUtils;

public class SystemConfigurePswdActivity extends BaseActivity {
	
	EditText oldPswdET,newPswdET,confirmPswdET;
	Button saveBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_configure_pswd);
		
		init();
	}

	private void init() {
		oldPswdET = (EditText) findViewById(R.id.oldpswdEditText);
		newPswdET = (EditText) findViewById(R.id.newpswdEditText);
		confirmPswdET = (EditText) findViewById(R.id.confirmpswdEditText);
		saveBtn = (Button) findViewById(R.id.savePswd);
		saveBtn.setOnClickListener(clickListener);
	}
	
	View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			String oldPswdInPref = App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.config_pswd);
			String oldpswd = oldPswdET.getText().length() == 0 ? "":oldPswdET.getText().toString();
			String newpswd = newPswdET.getText().length() == 0 ? "":newPswdET.getText().toString();
			String confirmpswd = confirmPswdET.getText().length() == 0 ? "":confirmPswdET.getText().toString();
			if(!oldPswdInPref.equals(oldpswd)){
				showAlertMsg(R.string.oldpswderror);
				return;
			}else if(!newpswd.equals(confirmpswd)){
				showAlertMsg(R.string.newpswderror);
				return;
			}
			App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.config_pswd, confirmpswd);
			showAlertMsg(R.string.configpswdsuccess);
		}
	};
}
