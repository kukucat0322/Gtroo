package cn.burgeon.core.ui.system;

import cn.burgeon.core.App;
import cn.burgeon.core.R;
import cn.burgeon.core.utils.PreferenceUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class SystemConfigurationParamConfigFragment extends Fragment{
    private static final String TAG = "SystemConfigurationParamConfigFragment";
    
    //输入控件
    private Spinner scanBarcodeInterceptNumberSpinner;
    private Spinner moneyDataAccuracySpinner;
    private Spinner moenyRoundingStyleSpinner;    
    private CheckBox enableMallsSettlementCheckBox;
    private CheckBox controlSalesmanLowestRebateCheckBox;
    private CheckBox useOutsideBarcodeCheckBox;
    
    //当前输入内容
    private String scanBarcodeInterceptNumber;
    private String moneyDataAccuracy;
    private String moenyRoundingStyle;   
    private boolean enableMallsSettlement;
    private boolean controlSalesmanLowestRebate;
    private boolean useOutsideBarcode;

    //上次输入内容
    private String scanBarcodeInterceptNumberLastInput;
    private String moneyDataAccuracyLastInput;
    private String moenyRoundingStyleLastInput;   
    private boolean enableMallsSettlementLastInput;
    private boolean controlSalesmanLowestRebateLastInput;
    private boolean useOutsideBarcodeLastInput;
    
    //保存按钮
    private Button saveButton;
    
    //保存过标志位
    private boolean saved;
    
    static SystemConfigurationParamConfigFragment newInstance() {
    	SystemConfigurationParamConfigFragment newFragment = new SystemConfigurationParamConfigFragment();
        return newFragment;
    }
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.system_configuration_param_config_fragment, null);
		
		scanBarcodeInterceptNumberSpinner = (Spinner) view.findViewById(R.id.scanBarcodeInterceptNumberSpinner);
		moneyDataAccuracySpinner = (Spinner) view.findViewById(R.id.moneyDataAccuracySpinner);
		moenyRoundingStyleSpinner = (Spinner) view.findViewById(R.id.moenyRoundingStyleSpinner);
		
		enableMallsSettlementCheckBox = (CheckBox) view.findViewById(R.id.enableMallsSettlementCheckBox);
		controlSalesmanLowestRebateCheckBox = (CheckBox) view.findViewById(R.id.controlSalesmanLowestRebateCheckBox);
		useOutsideBarcodeCheckBox = (CheckBox) view.findViewById(R.id.useOutsideBarcodeCheckBox);
		
		saveButton = (Button) view.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				checkToSave();				
			}
		});
		
		return view;
	}

	//保存数据
	private void checkToSave(){
    	//获取用户输入数据
    	getInputData();
    	//数据检查
    	if(sameData()){
    		showTips(R.string.tipsInputDataNotChanged);
    		return;
    	}
    	//保存到设备
    	saveToMemery();

    	//记录上次输入内容
    	saveLastInput();
	}
	
	private void getInputData(){
		scanBarcodeInterceptNumber = scanBarcodeInterceptNumberSpinner.getSelectedItem().toString();
		moneyDataAccuracy = moneyDataAccuracySpinner.getSelectedItem().toString();
		moenyRoundingStyle = moenyRoundingStyleSpinner.getSelectedItem().toString();
		
		enableMallsSettlement = enableMallsSettlementCheckBox.isChecked();
		controlSalesmanLowestRebate = controlSalesmanLowestRebateCheckBox.isChecked();
		useOutsideBarcode = useOutsideBarcodeCheckBox.isChecked();
	}

	//检测数据是否发生变化
	private boolean sameData(){
		if(saved && scanBarcodeInterceptNumberLastInput.equals(scanBarcodeInterceptNumber)
				&& moneyDataAccuracyLastInput.equals(moneyDataAccuracy)
				&& moenyRoundingStyleLastInput.equals(moenyRoundingStyle)
				&& enableMallsSettlementLastInput == enableMallsSettlement
				&& controlSalesmanLowestRebateLastInput == controlSalesmanLowestRebate
				&& useOutsideBarcodeLastInput == useOutsideBarcode){
			
			return true;
		}
		return false;
	}

    //记录上次输入内容
    private void saveLastInput(){
    	scanBarcodeInterceptNumberLastInput = scanBarcodeInterceptNumber;
    	moneyDataAccuracyLastInput = moneyDataAccuracy;
    	moenyRoundingStyleLastInput = moenyRoundingStyle;
    	
    	enableMallsSettlementLastInput = enableMallsSettlement;
    	controlSalesmanLowestRebateLastInput = controlSalesmanLowestRebate;
    	useOutsideBarcodeLastInput = useOutsideBarcode;
    }
	
    //保存到设备
    private void saveToMemery(){
    	
		App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.scanBarcodeInterceptNumberKey, scanBarcodeInterceptNumber);
		App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.moneyDataAccuracyKey, moneyDataAccuracy);
		App.getPreferenceUtils().savePreferenceStr(PreferenceUtils.moenyRoundingStyleKey, moenyRoundingStyle);
		
		App.getPreferenceUtils().savePreferenceBoolean(PreferenceUtils.enableMallsSettlementKey, enableMallsSettlement);
		App.getPreferenceUtils().savePreferenceBoolean(PreferenceUtils.controlSalesmanLowestRebateKey, controlSalesmanLowestRebate);
		App.getPreferenceUtils().savePreferenceBoolean(PreferenceUtils.useOutsideBarcodeKey, useOutsideBarcode);
    	
    	saved = true;
    	showTips(R.string.tipsSaveSucess);
    }
	
    //显示对话框
    private void showTips(int whichTips){
    	LayoutInflater inflater = this.getActivity().getLayoutInflater();
    	//布局文件待添加！！！！！！！！！！
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
}
