package cn.burgeon.core.ui.system;

import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.utils.PreferenceUtils;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SystemConfigurationNetConfigFragment extends Fragment {
    private static final String TAG = "SystemConfigurationNetConfigFragment";
    
    //交互服务器
    private EditText interactiveURLAddressEditText;
    private String interactiveURLAddress;
    private String interactiveURLAddressLastInput;
    //下载服务器
    private EditText downloadURLAddressEditText;
    private String downloadURLAddress;
    private String downloadURLAddressLastInput;
    //各种button
    private Button mServerTest;
    private Button mSystemUpdate;
    private Button mDataDownload;
    private Button mChangePassword;
    private Button mSaveButton;

    static SystemConfigurationNetConfigFragment newInstance() {
        SystemConfigurationNetConfigFragment newFragment = new SystemConfigurationNetConfigFragment();
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "=====onCreate====");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.system_configuration_net_config_fragment, container, false);
        
        interactiveURLAddressEditText = (EditText) view.findViewById(R.id.interactiveURLAddressEditText);
        downloadURLAddressEditText = (EditText) view.findViewById(R.id.downloadURLAddressEditText);
        mServerTest = (Button) view.findViewById(R.id.serverTestButton);
        mSystemUpdate = (Button) view.findViewById(R.id.systemUpdateConfigButton);
        mDataDownload = (Button) view.findViewById(R.id.dataDownloadButton);
        mChangePassword = (Button) view.findViewById(R.id.changePasswordButton);
        mSaveButton = (Button) view.findViewById(R.id.saveButton);
        
        displaySettedServerAddress();
        
        mServerTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SystemConfigurationNetConfigFragment.this.getActivity(), 
						SystemNetTestActivity.class);
				startActivity(intent);	
			}
		});
        
        mSystemUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	
			}
		});
        
        mDataDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SystemConfigurationNetConfigFragment.this.getActivity(), 
						SystemDataDownloadActivity.class);
				startActivity(intent);
			}
		});
        
        mChangePassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SystemConfigurationNetConfigFragment.this.getActivity(), 
						SystemConfigurePswdActivity.class);
				startActivity(intent);
			}
		});
        
        mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkToSave();
			}
		});
        return view;
    }

    private void displaySettedServerAddress(){
    	String interactiveServer = App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.interactiveURLAddressKey);
    	String downloadServer = App.getPreferenceUtils().getPreferenceStr(PreferenceUtils.downloadURLAddressKey);
    	if( !TextUtils.isEmpty(interactiveServer)){
    		interactiveURLAddressEditText.setText(interactiveServer);
    	}
    	if( !TextUtils.isEmpty(downloadServer)){
    		downloadURLAddressEditText.setText(downloadServer);
    	}
    }
    
    private void checkToSave(){

    	getInputData();

    	if(noInput()){
			showTips(R.string.tipsNoInputData);
    		return;
    	}
    	if(sameData()){
    		showTips(R.string.tipsInputDataNotChanged);
    		return;
    	}

    	saveToMemory();

    	saveLastInput();
    }
  

    private boolean sameData(){    	
    	if(saveOnce() && interactiveURLAddress.equals(interactiveURLAddressLastInput)
    			&& downloadURLAddress.equals(downloadURLAddressLastInput)){
    		return true;
    	}
    	return false;
    }

    //http://g.burgeon.cn:2080/xepospda/DownloadFiles
    //http://g.burgeon.cn:290/servlets/binserv/Rest
    private void saveToMemory(){
    	if( !TextUtils.isEmpty(interactiveURLAddress)){
    		App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.interactiveURLAddressKey, interactiveURLAddress);
    	}
    	if( !TextUtils.isEmpty(downloadURLAddress)){
    		App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.downloadURLAddressKey, downloadURLAddress);
    	}
    	showTips(R.string.tipsSaveSucess);
    }
    

    private void getInputData(){
    	interactiveURLAddress = interactiveURLAddressEditText.getText().toString();
    	interactiveURLAddress = interactiveURLAddress != null ? interactiveURLAddress.trim() : "";
    	downloadURLAddress = downloadURLAddressEditText.getText().toString();
    	downloadURLAddress = downloadURLAddress != null ? downloadURLAddress.trim() : "";
    }


    private boolean noInput(){
    	if(TextUtils.isEmpty(interactiveURLAddress) && TextUtils.isEmpty(downloadURLAddress)){
    		return true;
    	}
    	return false;
    }


    private boolean saveOnce(){
    	return !TextUtils.isEmpty(interactiveURLAddress) || !TextUtils.isEmpty(downloadURLAddress);
    }
    

    private void saveLastInput(){
    	interactiveURLAddressLastInput = interactiveURLAddress;
    	downloadURLAddressLastInput = downloadURLAddress;
    }
    

    private void showTips(int whichTips){
    	LayoutInflater inflater = this.getActivity().getLayoutInflater();

    	View tipsLayout = inflater.inflate(R.layout.inventory_refresh_tips, 
    			(ViewGroup)this.getActivity().findViewById(R.id.inventoryRefreshTipsLayout));
    	TextView tipsText = (TextView) tipsLayout.findViewById(R.id.inventoryRefreshingTipsText);
    	tipsText.setText(whichTips);
    	
    	new AlertDialog.Builder(this.getActivity())
    		.setTitle(getString(R.string.tipsDataDownload))
    		.setView(tipsLayout)
    		.setPositiveButton(getString(R.string.confirm),null)
    		.show();
    	
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
