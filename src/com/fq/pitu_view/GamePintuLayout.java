package com.fq.pitu_view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fq.pitu_game.R;
import com.fq.utils.ImagePiece;
import com.fq.utils.ImageSplitterUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;


public class GamePintuLayout extends RelativeLayout implements OnClickListener {

	private int mColumn = 3;
	//容器的内边距
	private int mPadding;
	//图块间距dp
	private int mMargin=3;
	private ImageView[]  mGamePintuItems ;
	
	private int mItemWidth;
	//游戏的图片
	private Bitmap mBitmap;
	private List<ImagePiece> mItemBitmaps;
	private Boolean once=false;
	/**
	 * 容器的宽度
	 */
	private int mWidth;
	
	private boolean isGameSuccess;
	private boolean isGameOver;
	
	public interface GamePintuListener{
		void nextLevel(int nextLevel);
		void timeChanged(int currentTime);
		void gameOver();
	}
	
	private GamePintuListener mListener;
	/**
	 * 设置接口回掉
	 * @param mListener
	 */
	public void setOnGamePintuListener(GamePintuListener mListener) {
		this.mListener = mListener;
	}

	private int mLevel=1;
	private static final int TIME_CHANGED=1;
	private static final int NEXT_LEVEL=2;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TIME_CHANGED:
				if (isGameSuccess||isGameOver||isPause) {
					return;
				}
				if (mListener!=null) {
					mListener.timeChanged(mTime);
				}
				
				if (mTime==0) {
					isGameOver=true;
					mListener.gameOver();
					return;
				}
				mTime--;
				mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);
				break;
			case NEXT_LEVEL:
				mLevel++;
				if (mListener!=null) {
					mListener.nextLevel(mLevel);
				}else {
					nextLevel();
				}
				break;

			default:
				break;
			}
		};
	};
	
	private boolean isTimeEnabled=false;
	private int mTime;
	/**
	 * 设置是否开启时间
	 * @param isTimeEnabled
	 */
	public void setTimeEnabled(boolean isTimeEnabled) {
		this.isTimeEnabled = isTimeEnabled;
	}
	
	public GamePintuLayout(Context context) {
		super(context);
		init();
	}

	public GamePintuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GamePintuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public GamePintuLayout(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	
	private void init() {
		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMargin, 
				getResources().getDisplayMetrics());
		mPadding = min(getPaddingLeft(),getPaddingRight(),getPaddingTop(),getPaddingBottom());
	}
	/**
	 * 获取多个参数的最小值
	 */
	private int min(int...params) {
		int min=params[0];
		for (int i : params) {
			if (i<min) {
				min=i;
			}
		}
		return min;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//取宽高的最小值
		mWidth=Math.min(getMeasuredHeight(), getMeasuredWidth());
		
		if (!once) {
			//进行排序，以及排序
			initBitmap();
			//设置ImageView(Item)的宽高等属性
			initItem();
			//判断是否开启时间
			checkTimeEnable();
			
			once=true;
		}
		setMeasuredDimension(mWidth, mWidth);
	}
	private void checkTimeEnable() {
		if (isTimeEnabled) {
			//根据当前等级设置时间
			countTimeBaseLevel();
			mHandler.sendEmptyMessage(TIME_CHANGED);
		}
	}

	private void countTimeBaseLevel() {
		mTime=(int)Math.pow(2, mLevel)*20;
		
	}

	/**
	 * 进行排序，以及排序
	 */
	private void initBitmap() {
		if (mBitmap==null) {
			mBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.sc);
		}
		mItemBitmaps=ImageSplitterUtils.splitImage(mBitmap, mColumn);
		//用sort完成乱序
		Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {

			@Override
			public int compare(ImagePiece a, ImagePiece b) {
				return Math.random()>0.5?1:-1;
			}
		});
	}
	/**
	 * 设置ImageView(Item)的宽高等属性
	 */
	private void initItem() {
		mItemWidth = (mWidth-mPadding*2-mMargin*(mColumn-1))/mColumn;
		mGamePintuItems=new ImageView[mColumn*mColumn];
		for(int i=0;i<mGamePintuItems.length;i++){
			ImageView item = new ImageView(getContext());
			item.setOnClickListener(this);
			item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
			
			mGamePintuItems[i]=item;
			
			item.setId(i+1);
			
			//在item的tag中存放了index
			item.setTag(i+"_"+mItemBitmaps.get(i).getIndex());
			RelativeLayout.LayoutParams lp =new  RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
			//设置item间横向间距，通过右边距实现
			//不是最后一列
			if ((i+1)%mColumn!=0) {
				lp.rightMargin=mMargin;
			}
			
			//不是第一列
			if (i%mColumn!=0) {
				lp.addRule(RelativeLayout.RIGHT_OF, mGamePintuItems[i-1].getId());
			}
			//如果不是第一行,设置topMargin和rule
			if ((i+1)>mColumn) {
				lp.topMargin=mMargin;
				lp.addRule(RelativeLayout.BELOW, mGamePintuItems[i-mColumn].getId());
			}
			addView(item, lp);
		}
	}

	private boolean isAniming=false;
	
	private ImageView mFirst;
	private ImageView mSecond;
	@Override
	public void onClick(View v) {
		
		if (isAniming) {
			return;
		}
		
		//两次选中同一个
		if (mFirst==v) {
			mFirst.setColorFilter(null);
			mFirst=null;
			return;
		}
		
		if (mFirst==null) {
			mFirst=(ImageView) v;
			mFirst.setColorFilter(Color.parseColor("#55ff0000"));
		}else {
			mSecond=(ImageView) v;
			
//			if (mSecond.) {
//				
//			}
			
			exChangeView();//交换我们的Item
		}
	}

	/**
	 * 动画层
	 */
	private RelativeLayout mAnimLayout;
	
	/**
	 * 交换我们的Item
	 */
	private void exChangeView() {
		mFirst.setColorFilter(null);
		//构造动画层
		setUpAnimLayout();
		
		final String firstTag = (String) mFirst.getTag();
		final String secondTag = (String) mSecond.getTag();
		
		ImageView first = new ImageView(getContext());
		first.setImageBitmap(mItemBitmaps.get(getImageIdByTag(firstTag)).getBitmap());
		
		RelativeLayout.LayoutParams lp =new  RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
		lp.leftMargin=mFirst.getLeft()-mPadding;
		lp.topMargin=mFirst.getTop()-mPadding;
		first.setLayoutParams(lp);
		mAnimLayout.addView(first);
		
		
		ImageView second = new ImageView(getContext());
		second.setImageBitmap(mItemBitmaps.get(getImageIdByTag(secondTag)).getBitmap());
		
		RelativeLayout.LayoutParams lp2 =new  RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
		lp2.leftMargin=mSecond.getLeft()-mPadding;
		lp2.topMargin=mSecond.getTop()-mPadding;
		second.setLayoutParams(lp2);
		mAnimLayout.addView(second);
		
		//设置动画
		TranslateAnimation anim =new TranslateAnimation(0, mSecond.getLeft()-mFirst.getLeft(),
				0, mSecond.getTop()-mFirst.getTop());
		anim.setDuration(300);
		anim.setFillAfter(true);
		first.startAnimation(anim);
		
		TranslateAnimation anim2 =new TranslateAnimation(0, -mSecond.getLeft()+mFirst.getLeft(),
				0, -mSecond.getTop()+mFirst.getTop());
		anim2.setDuration(300);
		anim2.setFillAfter(true);
		second.startAnimation(anim2);
		
		//监听动画
		anim2.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				mFirst.setVisibility(View.INVISIBLE);
				mSecond.setVisibility(View.INVISIBLE);
				isAniming=true;
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mFirst.setImageBitmap(mItemBitmaps.get(getImageIdByTag(secondTag)).getBitmap());
				mSecond.setImageBitmap(mItemBitmaps.get(getImageIdByTag(firstTag)).getBitmap());
				
				mFirst.setTag(secondTag);
				mSecond.setTag(firstTag);
				
				mFirst.setVisibility(View.VISIBLE);
				mSecond.setVisibility(View.VISIBLE);
				
				mFirst=mSecond=null;
				
				mAnimLayout.removeAllViews();
				//判断是否胜利
				checkSuccess();
				
				isAniming=false;
			}
		});
		
	}

	/**
	 * 判断是否胜利
	 */
	private void checkSuccess() {
		boolean isSuccess=true;
		for (int i = 0; i < mGamePintuItems.length; i++) {
			ImageView imageView = mGamePintuItems[i];
			
			if (getImageIndexByTag((String) imageView.getTag())!=i) {
				isSuccess=false;
				break;
			}
			
		}
		if (isSuccess) {
			
			isGameSuccess=true;
			mHandler.removeMessages(TIME_CHANGED);
			
			Toast.makeText(getContext(), "胜利了！", Toast.LENGTH_LONG).show();
			mHandler.sendEmptyMessage(NEXT_LEVEL);
		}
	}

	/**
	 * 根据tag获取id
	 * @param tag
	 * @return
	 */
	public int getImageIdByTag(String tag) {
		String[] split = tag.split("_");
		return Integer.parseInt(split[0]);
	}
	/**
	 * 根据tag获取index
	 * @param tag
	 * @return
	 */
	public int getImageIndexByTag(String tag) {
		String[] split = tag.split("_");
		return Integer.parseInt(split[1]);
	}
	/**
	 * 构造动画层
	 */
	private void setUpAnimLayout() {
		if (mAnimLayout==null) {
			mAnimLayout =new RelativeLayout(getContext());
			addView(mAnimLayout);
		}
	}

	public void restart(){
		isGameOver=false;
		mColumn--;
		nextLevel();
	}
	
	private boolean isPause;
	
	public void pause() {
		isPause=true;
		mHandler.removeMessages(TIME_CHANGED);
	}
	
	public void resume() {
		if (isPause) {
			isPause=false;
			mHandler.sendEmptyMessage(TIME_CHANGED);
		}
	}
	public void nextLevel(){
		this.removeAllViews();
		mAnimLayout=null;
		mColumn++;
		isGameSuccess=false;
		checkTimeEnable();
		initBitmap();
		initItem();
	}
	
	
	
	
}
