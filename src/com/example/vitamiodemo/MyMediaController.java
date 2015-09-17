package com.example.vitamiodemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import io.vov.vitamio.widget.MediaController;

public class MyMediaController extends MediaController {
	
	private Context mContext;
	private View mRootView;
	
	private OnMyMediaControllerListener mListener;
	
	private ImageButton mBackButton, mForwardButton, mSreenFullButton, mLayoutChangeButton;
	private TextView mTvBattery, mTvTime;
	private SeekBar mSeekBar;

	public MyMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mRootView = this;
	}

	public MyMediaController(Context context, OnMyMediaControllerListener listener) {
		super(context);
		mContext = context;
		mRootView = this;
		mListener = listener;
	}
	
	@Override
	protected View makeControllerView() {
//		return super.makeControllerView();
		
		mRootView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(getResources().getIdentifier("mediacontroller_bottom", "layout", mContext.getPackageName()), this);
		initView();
		return mRootView;
	}
	
	private void initView() {
		mSeekBar = (SeekBar) mRootView.findViewById(R.id.mediacontroller_seekbar);
		mBackButton = (ImageButton) mRootView.findViewById(R.id.mediacontroller_back);
		mForwardButton = (ImageButton) mRootView.findViewById(R.id.mediacontroller_forward);
		mSreenFullButton = (ImageButton) mRootView.findViewById(R.id.mediacontroller_screen_full);
		mTvBattery = (TextView) mRootView.findViewById(R.id.mediacontroller_battery);
		mTvTime = (TextView) mRootView.findViewById(R.id.mediacontroller_time);
		mLayoutChangeButton = (ImageButton) mRootView.findViewById(R.id.mediacontroller_layout_change);
		mLayoutChangeButton.setBackgroundResource(R.drawable.mediacontroller_screen_fit);
//		DateFormat df = new SimpleDateFormat("HH:mm");
//		mTvTime.setText(df.format(new Date()));
		
		mBackButton.setOnClickListener(new ViewClick());
		mForwardButton.setOnClickListener(new ViewClick());
		mSreenFullButton.setOnClickListener(new ViewClick());
		mLayoutChangeButton.setOnClickListener(new ViewClick());
		
//		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//		BatteryReceiver batteryReceiver = new BatteryReceiver();
//		mContext.registerReceiver(batteryReceiver, intentFilter);
	}
	
	/*class BatteryReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//判断它是否是为电量变化的Broadcast Action
			if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
				//获取当前电量
				int level = intent.getIntExtra("level", 0);
				//电量的总刻度
				int scale = intent.getIntExtra("scale", 100);
				//把它转成百分比
				mTvBattery.setText(((level*100)/scale)+"%");
			}
		}
	}*/
	
	public interface OnMyMediaControllerListener {
		public void setBack(SeekBar bar);
		public void setForward(SeekBar bar);
		
		public void setScreenFull();
		public void setLayoutChange(View view);
	}
	
	private final class ViewClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mediacontroller_back:
				mListener.setBack(mSeekBar);
				break;
				
			case R.id.mediacontroller_forward:
				mListener.setForward(mSeekBar);
				break;
				
			case R.id.mediacontroller_screen_full:
				mListener.setScreenFull();
				break;
				
			case R.id.mediacontroller_layout_change:
				mListener.setLayoutChange(mLayoutChangeButton);
				break;

			default:
				break;
			}
			
		}
		
	}
}
