package cn.burgeon.core.ui.system;

import java.util.ArrayList;

import cn.burgeon.core.R;
import cn.burgeon.core.adapter.SystemConfigurationFragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class SystemConfigurationActivity extends FragmentActivity {
    private static final String TAG = "SystemConfigurationActivity";
    
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragmentsList;
    private ImageView mBottomLine;
    private TextView mTabNetConfig;
    private TextView mTabStoreInfo;
    private TextView mTabParamConfig;
    private TextView mTabPrintFormat;

    private int mCurrIndex = 0;
    private int mBottomLineWidth;
    private int mPosition1;
    private int mPosition2;
    private int mPosition3;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        setContentView(R.layout.activity_system_configuration);
        
        initWidth();
        initTextView();
        initViewPager();
    }

    // 设置程序全屏显示
    public void setupFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }
    
    private void initTextView() {
    	mTabNetConfig = (TextView) findViewById(R.id.titleNetConfig);
        mTabStoreInfo = (TextView) findViewById(R.id.titleStoreInfo);
        mTabParamConfig = (TextView) findViewById(R.id.titleParamConfig);
        mTabPrintFormat = (TextView) findViewById(R.id.titlePrintFormat);

        mTabNetConfig.setOnClickListener(new TabOnClickListener(0));
		mTabStoreInfo.setOnClickListener(new TabOnClickListener(1));
		mTabParamConfig.setOnClickListener(new TabOnClickListener(2));
		mTabPrintFormat.setOnClickListener(new TabOnClickListener(3));
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mFragmentsList = new ArrayList<Fragment>();

		Fragment netSettingsFragment = SystemConfigurationNetConfigFragment.newInstance();
		Fragment storeInfoFragment = SystemConfigurationStoreInfoFragment.newInstance();
		Fragment friendsFragment = SystemConfigurationParamConfigFragment.newInstance();
		Fragment chatFragment = SystemConfigurationPrintFormatFragment.newInstance();

        mFragmentsList.add(netSettingsFragment);
        mFragmentsList.add(storeInfoFragment);
        mFragmentsList.add(friendsFragment);
        mFragmentsList.add(chatFragment);
        
        mViewPager.setAdapter(new SystemConfigurationFragmentPagerAdapter(getSupportFragmentManager(), mFragmentsList));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void initWidth() {
        mBottomLine = (ImageView) findViewById(R.id.titleBottomLine);
        mBottomLineWidth = mBottomLine.getLayoutParams().width;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        int offset = (int) ((screenW / 4.0 - mBottomLineWidth) / 2);

        mPosition1 = (int) (screenW / 4.0);
        mPosition2 = mPosition1 * 2;
        mPosition3 = mPosition1 * 3;
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
            case 0:
                if (mCurrIndex == 1) {
                    animation = new TranslateAnimation(mPosition1, 0, 0, 0);
                } else if (mCurrIndex == 2) {
                    animation = new TranslateAnimation(mPosition2, 0, 0, 0);
                } else if (mCurrIndex == 3) {
                    animation = new TranslateAnimation(mPosition3, 0, 0, 0);
                }
                break;
            case 1:
                if (mCurrIndex == 0) {
                    animation = new TranslateAnimation(0, mPosition1, 0, 0);
                } else if (mCurrIndex == 2) {
                    animation = new TranslateAnimation(mPosition2, mPosition1, 0, 0);
                } else if (mCurrIndex == 3) {
                    animation = new TranslateAnimation(mPosition3, mPosition1, 0, 0);
                }
                break;
            case 2:
                if (mCurrIndex == 0) {
                    animation = new TranslateAnimation(0, mPosition2, 0, 0);
                } else if (mCurrIndex == 1) {
                    animation = new TranslateAnimation(mPosition1, mPosition2, 0, 0);
                } else if (mCurrIndex == 3) {
                    animation = new TranslateAnimation(mPosition3, mPosition2, 0, 0);
                }
                break;
            case 3:
                if (mCurrIndex == 0) {
                    animation = new TranslateAnimation(0, mPosition3, 0, 0);
                } else if (mCurrIndex == 1) {
                    animation = new TranslateAnimation(mPosition1, mPosition3, 0, 0);
                } else if (mCurrIndex == 2) {
                    animation = new TranslateAnimation(mPosition2, mPosition3, 0, 0);
                }
                break;
            }
            mCurrIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
            mBottomLine.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
    
    @Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	public class TabOnClickListener implements View.OnClickListener {
        private int index = 0;
 
        public TabOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(index);
        }
    };
}