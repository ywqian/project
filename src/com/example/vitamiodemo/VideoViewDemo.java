package com.example.vitamiodemo;

import com.example.vitamiodemo.MyMediaController.OnMyMediaControllerListener;
import com.xlink.linkwil.sdk.IpcApi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.widget.VideoView;

public class VideoViewDemo extends Activity {

	private String path = ""; // 视频地址
	private VideoView mVideoView;
	private MyMediaController mMediaController;
	
	private View mLoadingView; // 加载界面
	private TextView mLoadRateView, mDownloadRateView; // 速度/加载进度
	/**
	 * 声音界面
	 */
	private View mVolumeBrightnessLayout;
	private ImageView mOperationBg;
	private ImageView mOperationPercent;
	private AudioManager mAudioManager;
	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	private GestureDetector mGestureDetector; // 手势监听
	
	/** 是否需要自动恢复播放，用于自动暂停，恢复播放 */
	private boolean needResume;
	private int mLayout = VideoView.VIDEO_LAYOUT_SCALE; // 画面全屏
	private int mVideoHeight, mVideoWidth; // 视频宽、高
	private float mVideoAspectRatio = 0.0f; // 视频宽高比
	private long mDuration = 0;
	private long currentPosition = 0; // 播放的当前位置
	
	private CheckBox mLiveLocalTransform; // 1直播，0本地

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		 // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏状态栏,全屏显示
//      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
		setContentView(R.layout.videoview);
		initView();
	}
	
	private void initView() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			path = bundle.getString("path", "");
		}
		if (path == "") {
			path = "http://42.121.255.86:6080/group1/M00/1C/C9/L_9ltgfMWmVzWaD7JP.mp4";
		}
		Log.i("qqq", path);
		mMediaController = new MyMediaController(this, new OnBackForwardListener());
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mLoadingView = findViewById(R.id.video_loading);
		mDownloadRateView = (TextView) findViewById(R.id.download_rate);
		mLoadRateView = (TextView) findViewById(R.id.load_rate);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
		mLiveLocalTransform = (CheckBox) findViewById(R.id.live_local_transform);
		mLiveLocalTransform.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) { // 直播
					
				} else { // 本地
					
				}
			}
		});
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
			
		mVideoView.setVideoPath(path);
		mVideoView.setMediaController(mMediaController);
//		mVideoView.setMediaController(new MediaController(this));
		mVideoView.setKeepScreenOn(true);
		mVideoView.setBufferSize(512 * 1024);
		/**
		 * 视频预处理完成后调用
		 */
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				
				mVideoWidth = mediaPlayer.getVideoWidth();
				mVideoHeight = mediaPlayer.getVideoHeight();
				mVideoAspectRatio =  mediaPlayer.getVideoAspectRatio();
				mDuration = mediaPlayer.getDuration();
				
				mediaPlayer.setPlaybackSpeed(1.0f);
				startPlayer();
			}
		});
		mVideoView.setOnInfoListener(mInfoListener);
		// 网络视频流缓冲变化时调用
		mVideoView.setOnBufferingUpdateListener(mBufferingUpdateListener);
		// 异步操作调用过程中发生错误时调用
		mVideoView.setOnErrorListener(mErrorListener);
		// 视频播放完成后调用
		mVideoView.setOnCompletionListener(mCompletionListener);
		mVideoView.setOnSeekCompleteListener(mSeekCompleteListener);
		mVideoView.requestFocus();
	}
	
	/**
	 * 错误处理
	 */
	private OnErrorListener mErrorListener = new OnErrorListener() {
		
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			switch (what) {
			case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
				break;
			case MediaPlayer.MEDIA_ERROR_UNKNOWN: // 播放错误，未知错误。
				Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_LONG).show();
				break;
			case MediaPlayer.MEDIA_ERROR_IO:
				Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_LONG).show();
				break;
			case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
				break;
			default:
				break;
			}
			return true;
		}
	};
	
	private OnInfoListener mInfoListener = new OnInfoListener() {

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START: // 开始缓冲
				//开始缓存，暂停播放
				if (isPlaying()) {
					stopPlayer();
					needResume = true;
				}
				mLoadingView.setVisibility(View.VISIBLE);
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END: // 缓冲结束
				//缓存完成，继续播放
				Log.i("qqq", "缓冲完成：" + mVideoView.getCurrentPosition());
				if (needResume)
					startPlayer();
				mLoadingView.setVisibility(View.GONE);
				break;
			case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED: // 下载速度变化
				//显示 下载速度
				mDownloadRateView.setText("" + extra + "kb/s" + "  ");
				break;
			}
			return true;
		}
	};
	
	private OnCompletionListener mCompletionListener = new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
//			mVideoView.stopPlayback();
			mVideoView.pause();
		}
	};
	
	private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
		
		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			mLoadRateView.setText(percent + "%");
		}
	};
	
	/**
	 * 前进/后退
	 */
	private class OnBackForwardListener implements OnMyMediaControllerListener {

		@Override
		public void setBack(SeekBar bar) {
			
			currentPosition = mVideoView.getCurrentPosition();
//			mVideoView.seekTo(currentPosition - 5000);
			
			if (currentPosition < 5000) {
				currentPosition = 0;
		        mVideoView.seekTo(currentPosition);
		    } else {
		    	currentPosition -= 5000;
		    	mVideoView.seekTo(currentPosition);
		    }
			if (isPlaying()) {
				mVideoView.pause();
			}
			if (mVideoView.isBuffering() == false) {
				mVideoView.start();
			}
		}

		@Override
		public void setForward(SeekBar bar) {
			currentPosition = mVideoView.getCurrentPosition();
			if (currentPosition + 5000 > mDuration) {
		         currentPosition = mDuration;
		         mVideoView.seekTo(currentPosition);
		     } else {
		    	 currentPosition += 5000;
		    	 mVideoView.seekTo(currentPosition);
		     }
			if (isPlaying()) {
				mVideoView.pause();
			}
			if (mVideoView.isBuffering() == false) {
				mVideoView.start();
			}
		}

		@Override
		public void setScreenFull() {
//	        // 横屏
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { // 竖屏
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	        } else {
	        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	        }
		}

		@Override
		public void setLayoutChange(View view) {
			changeLayout(view);
		}
	}
	
	/**
	 * 剪裁画面大小
	 * @param view
	 */
	private void changeLayout(View view) {
		mLayout++;
		if (mLayout == 4) {
			mLayout = 0;
		}
		switch (mLayout) {
		case 0:
			mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
			view.setBackgroundResource(R.drawable.mediacontroller_sreen_size_100);
			Toast.makeText(this, "原始画面大小", Toast.LENGTH_LONG).show();
			break;
		case 1:
			mLayout = VideoView.VIDEO_LAYOUT_SCALE;
			view.setBackgroundResource(R.drawable.mediacontroller_screen_fit);
			Toast.makeText(this, "画面全屏", Toast.LENGTH_LONG).show();
			break;
		case 2:
			mLayout = VideoView.VIDEO_LAYOUT_STRETCH;
			view.setBackgroundResource(R.drawable.mediacontroller_screen_size);
			Toast.makeText(this, "画面拉伸", Toast.LENGTH_LONG).show();
			break;
		case 3:
			mLayout = VideoView.VIDEO_LAYOUT_ZOOM;
			view.setBackgroundResource(R.drawable.mediacontroller_sreen_size_crop);
			Toast.makeText(this, "画面裁剪", Toast.LENGTH_LONG).show();
			break;
		}
		mVideoView.setVideoLayout(mLayout, mVideoAspectRatio);
	}
	
	private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
		
		@Override
		public void onSeekComplete(MediaPlayer mp) {
			Log.i("qqq", "qqq="+mVideoView.getCurrentPosition());
		}
	};
	
	/**
	 * 监听手势变化
	 * @author Administrator
	 *
	 */
	private class MyGestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent arg0) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
                float velocityY) {
			 if (e1.getX() - e2.getX() > 100) {  
//	                changePregress(-0.2f);  
	            } else if (e1.getX() - e2.getX() < -100) {  
//	                changePregress(0.2f);  
	            }  
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float moldX = e1.getX();  
            float moldY = e1.getY();  
            float y = e2.getY();  
            float X = e2.getX();  
            
            Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();
			if (moldX > windowWidth * 4.0 / 5)// 右边滑动  
            	onVolumeSlide((moldY - y) / windowHeight);  
            else if (moldX < windowWidth * 4.0 / 5)// 左边滑动  
            	onBrightnessSlide((moldY - y) / windowHeight); 
          
            return true;  
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			return false;
		}
	}
	
	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;
			// 显示
			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// 变更进度条
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * 滑动改变亮度
	 * 
	 * @param percent
	 */
	/** 当前亮度 */
	private float mBrightness = -1f;
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

			// 显示
			mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
		mOperationPercent.setLayoutParams(lp);
	}
	
	/**  
     * 播放进度  
     *   
     * @param percent  
     */  
	private long mProgress = 0; // 当前进度
    public void changePregress(float percent) {  
        if (mProgress < -1) {  
            mVideoView.pause();  
            mProgress = mVideoView.getCurrentPosition();  
        }  
  
        if (Math.abs(percent) > 0.1) {  
            percent = (float) (percent / Math.abs(percent) * 0.1);  
        }  
  
        long index = (long) (percent * mVideoView.getDuration()) + mProgress;  
        if (index > mVideoView.getDuration()) {  
            index = mVideoView.getDuration();  
        } else if (index < 0) {  
            index = 0;  
        }  
        mVideoView.seekTo(index);  
    }  
	
	//@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;

		// 处理手势结束
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}

		return super.onTouchEvent(event);
	}

	/** 手势结束 */
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;
		mProgress = -2;
		// 隐藏
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 500);
	}
	
	/** 定时隐藏 */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};
	
	private void stopPlayer() {
		if (mVideoView != null)
			mVideoView.pause();
	}

	private void startPlayer() {
		if (mVideoView != null)
			mVideoView.start();
	}

	private boolean isPlaying() {
		return mVideoView != null && mVideoView.isPlaying();
	}

	@Override
	protected void onPause() {
		if (mVideoView != null) {
			currentPosition = mVideoView.getCurrentPosition();
			mVideoView.pause();
		}
		Log.i("qqq", "onPause...........");
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if (currentPosition > 0) {
			mVideoView.seekTo(currentPosition);
			currentPosition = 0;
		}
		Log.i("qqq", "onResume...........");
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		Log.i("qqq", "onDestroy...........");
		if (mVideoView != null) {
			mVideoView.stopPlayback();
		}
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mVideoView != null) {
			if (mVideoView != null) {
				Log.i("qqq", "onConfigurationChanged..........." + mVideoAspectRatio);
				mVideoView.setVideoLayout(mLayout, mVideoAspectRatio);
			}
		}
		super.onConfigurationChanged(newConfig);
	}
	
	private void save(int isLive) { // 1直播，0本地
		Integer privilege = new Integer(0);
        int handle = IpcApi.usrLogIn(privilege, "192.168.1.1", "", "admin", "admin", 80, 0);
        

	}
}
