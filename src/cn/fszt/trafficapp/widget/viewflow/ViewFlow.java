package cn.fszt.trafficapp.widget.viewflow;

import java.util.EnumSet;
import java.util.LinkedList;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;
import cn.fszt.trafficapp.R;

public class ViewFlow extends AdapterView<Adapter> {

	private static final int SNAP_VELOCITY = 1000;
	private static final int INVALID_SCREEN = -1;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;

	private LinkedList<View> mLoadedViews;
	private int mCurrentBufferIndex;
	private int mCurrentAdapterIndex;
	private int mSideBuffer = 2;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchState = TOUCH_STATE_REST;
	private float mLastMotionX;
	private float mLastMotionY;
	private int mTouchSlop;
	private int mMaximumVelocity;
	private int mCurrentScreen;
	private int mNextScreen = INVALID_SCREEN;
	private boolean mFirstLayout = true;
	private ViewSwitchListener mViewSwitchListener;
	private Adapter mAdapter;
	private int mLastScrollDirection;
	private AdapterDataSetObserver mDataSetObserver;
	private CircleFlowIndicator mIndicator;
	private int mLastOrientation = -1;
	private long timeSpan = 3000;
	private Handler handler;
	private ViewGroup viewGroup;
	private GestureDetector mGestureDetector;

	private PerformClick mPerformClick;

	private static final float MOVE_TOUCHSLOP = 1f; // 超过此的偏移都认为是移动而不是点击
	private boolean mIsClick;	//是否点击

	private LinkedList<View> mRecycledViews;
	private ViewLazyInitializeListener mViewInitializeListener;
	private EnumSet<LazyInit> mLazyInit = EnumSet.allOf(LazyInit.class);

	private boolean mLastObtainedViewWasRecycled = false;

	private OnGlobalLayoutListener orientationChangeListener = new OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
			getViewTreeObserver().removeGlobalOnLayoutListener(orientationChangeListener);
			setSelection(mCurrentAdapterIndex);
		}
	};

	public static interface ViewSwitchListener {

		/**
		 * This method is called when a new View has been scrolled to.
		 * 
		 * @param view
		 *            the {@link View} currently in focus.
		 * @param position
		 *            The position in the adapter of the {@link View} currently
		 *            in focus.
		 */
		void onSwitched(View view, int position);

	}

	public static interface ViewLazyInitializeListener {
		void onViewLazyInitialize(View view, int position);
	}

	enum LazyInit {
		LEFT, RIGHT
	}

	public ViewFlow(Context context) {
		super(context);
		mSideBuffer = 3;
		init();
	}

	public ViewFlow(Context context, int sideBuffer) {
		super(context);
		mSideBuffer = sideBuffer;
		init();
	}

	public ViewFlow(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.ViewFlow);
		mSideBuffer = styledAttrs.getInt(R.styleable.ViewFlow_sidebuffer, 3);
		init();
	}

	private void init() {
		mLoadedViews = new LinkedList<View>();
		mRecycledViews = new LinkedList<View>();
		mScroller = new Scroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		mGestureDetector = new GestureDetector(getContext(), new YScrollDetector());
	}

	public void setViewGroup(ViewGroup viewGroup) {
		this.viewGroup = viewGroup;
	}

	public void startAutoFlowTimer() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (getChildCount() > 0 && mSideBuffer > 1)
					snapToScreen((mCurrentScreen + 1) % getChildCount());
				Message message = handler.obtainMessage(0);
				sendMessageDelayed(message, timeSpan);
			}
		};

		Message message = handler.obtainMessage(0);
		handler.sendMessageDelayed(message, timeSpan);
	}

	public void stopAutoFlowTimer() {
		if (handler != null)
			handler.removeMessages(0);
		handler = null;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.orientation != mLastOrientation) {
			mLastOrientation = newConfig.orientation;
			getViewTreeObserver().addOnGlobalLayoutListener(orientationChangeListener);
		}
	}

	public int getViewsCount() {
		return mSideBuffer;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int childWidth = 0;
		int childHeight = 0;
		int childState = 0;

		final int widthPadding = getWidthPadding();
		final int heightPadding = getHeightPadding();

		int count = mAdapter == null ? 0 : mAdapter.getCount();
		if (count > 0) {
			final View child = obtainView(0);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			childWidth = child.getMeasuredWidth();
			childHeight = child.getMeasuredHeight();
			childState = child.getMeasuredState();
			mRecycledViews.add(child);
		}

		switch (widthMode) {
		case MeasureSpec.UNSPECIFIED:
			widthSize = childWidth + widthPadding;
			break;
		case MeasureSpec.AT_MOST:
			widthSize = (childWidth + widthPadding) | childState;
			break;
		case MeasureSpec.EXACTLY:
			if (widthSize < childWidth + widthPadding)
				widthSize |= MEASURED_STATE_TOO_SMALL;
			break;
		}
		switch (heightMode) {
		case MeasureSpec.UNSPECIFIED:
			heightSize = childHeight + heightPadding;
			break;
		case MeasureSpec.AT_MOST:
			heightSize = (childHeight + heightPadding) | (childState >> MEASURED_HEIGHT_STATE_SHIFT);
			break;
		case MeasureSpec.EXACTLY:
			if (heightSize < childHeight + heightPadding)
				heightSize |= MEASURED_STATE_TOO_SMALL;
			break;
		}

		if (heightMode == MeasureSpec.UNSPECIFIED) {
			heightSize = heightPadding + childHeight;
		} else {
			heightSize |= (childState & MEASURED_STATE_MASK);
		}

		setMeasuredDimension(widthSize, heightSize);
	}

	private int getWidthPadding() {
		return getPaddingLeft() + getPaddingRight() + getHorizontalFadingEdgeLength() * 2;
	}

	public int getChildWidth() {
		return getWidth() - getWidthPadding();
	}

	private int getHeightPadding() {
		return getPaddingTop() + getPaddingBottom();
	}

	public int getChildHeight() {
		return getHeight() - getHeightPadding();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		final int count = getChildCount();
		for (int i = 0; i < count; ++i) {
			final View child = getChildAt(i);
			child.measure(MeasureSpec.makeMeasureSpec(getChildWidth(), MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(getChildHeight(), MeasureSpec.EXACTLY));
		}

		if (mFirstLayout) {
			mScroller.startScroll(0, 0, mCurrentScreen * getChildWidth(), 0, 0);
			mFirstLayout = false;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (getChildCount() == 0)
			return false;

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			// 处理事件，左后滑动时，传递给子项处理，上下滑动时，交由ViewGroup处理
			if (viewGroup != null) {
				viewGroup.requestDisallowInterceptTouchEvent(!mGestureDetector.onTouchEvent(ev));
			}

			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			// Remember where the motion event started
			mLastMotionX = x;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			if (handler != null)
				handler.removeMessages(0);
			break;

		case MotionEvent.ACTION_MOVE:
			// 处理事件，左后滑动时，传递给子项处理，上下滑动时，交由ViewGroup处理
			if (viewGroup != null) {
				viewGroup.requestDisallowInterceptTouchEvent(!mGestureDetector.onTouchEvent(ev));
			}

			final int xDiff = (int) Math.abs(x - mLastMotionX);
			boolean xMoved = xDiff > mTouchSlop;

			if (xMoved) {
				mTouchState = TOUCH_STATE_SCROLLING;
				if (mViewInitializeListener != null)
					initializeView(xDiff);
			}

			if (mTouchState == TOUCH_STATE_SCROLLING) {
				// Scroll to follow the motion event
				final int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;

				final int scrollX = getScrollX();
				if (deltaX < 0) {
					if (scrollX > 0) {
						scrollBy(Math.max(-scrollX, deltaX), 0);
					}
				} else if (deltaX > 0) {
					final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - scrollX - getWidth();
					if (availableToScroll > 0) {
						scrollBy(Math.min(availableToScroll, deltaX), 0);
					}
				}
				return true;
			}
			break;

		case MotionEvent.ACTION_UP:
			if (viewGroup != null) {
				viewGroup.requestDisallowInterceptTouchEvent(false);
			}

			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();

				if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
					// Fling hard enough to move left
					snapToScreen(mCurrentScreen - 1);
				} else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - 1) {
					// Fling hard enough to move right
					snapToScreen(mCurrentScreen + 1);
				} else {
					snapToDestination();
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}

			if (mIsClick) {
				if (mPerformClick == null) {
					mPerformClick = new PerformClick();
				}
				final ViewFlow.PerformClick performClick = mPerformClick;
				performClick.mClickMotionPosition = mCurrentAdapterIndex;
				performClick.rememberWindowAttachCount(); // 记录点击时的连接窗口次数
				performClick.run();
			}

			mTouchState = TOUCH_STATE_REST;
			if (handler != null) {
				Message message = handler.obtainMessage(0);
				handler.sendMessageDelayed(message, timeSpan);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (viewGroup != null) {
				viewGroup.requestDisallowInterceptTouchEvent(false);
			}

			mTouchState = TOUCH_STATE_REST;
		}
		return false;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mPerformClick != null) {
			removeCallbacks(mPerformClick);
		}
	}

	private class WindowRunnnable {
		private int mOriginalAttachCount;

		public void rememberWindowAttachCount() {
			mOriginalAttachCount = getWindowAttachCount(); // getWindowAttachCount
															// 获取控件绑在窗口上的次数
		}

		public boolean sameWindow() { // 判断是否是同一个窗口，异常情况时界面attachWindowCount会是+1，那么此时就不是同一个窗口了。
			return hasWindowFocus() && getWindowAttachCount() == mOriginalAttachCount;
		}
	}

	private class PerformClick extends WindowRunnnable implements Runnable {
		int mClickMotionPosition;

		public void run() {
			// The data has changed since we posted this action in the event
			// queue,
			// bail out before bad things happen
			// if (mDataChanged) return;

			final Adapter adapter = mAdapter;
			final int motionPosition = mClickMotionPosition;
			if (adapter != null && mAdapter.getCount() > 0 && motionPosition != INVALID_POSITION
					&& motionPosition < adapter.getCount() && sameWindow()) {
				// final View view = getChildAt(motionPosition -
				// mFirstPosition); // mFirstPosition不关注
				final View view = mLoadedViews.get(mCurrentBufferIndex);
				if (view != null) {
					performItemClick(view, motionPosition, adapter.getItemId(motionPosition));
				}
			}
		}
	}

	class YScrollDetector extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			/**
			 * if we're scrolling more closer to x direction, return false, let
			 * subview to process it
			 */
			return (Math.abs(distanceY) > Math.abs(distanceX));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (getChildCount() == 0)
			return false;

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:

			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			// Remember where the motion event started
			mLastMotionX = x;
			mLastMotionY = y;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			mIsClick = true;
			if (handler != null)
				handler.removeMessages(0);
			break;

		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(x - mLastMotionX);

			boolean xMoved = xDiff > mTouchSlop;

			// 计算y移动偏移量 判断y轴是否有移动过及本次down下，是否是一次click事件
			float tempDeltaX = mLastMotionX - ev.getX();
			float tempdeltaY = mLastMotionY - ev.getY();
			boolean isXMoved = Math.abs(tempDeltaX) > MOVE_TOUCHSLOP;
			boolean isYMoved = Math.abs(tempdeltaY) > MOVE_TOUCHSLOP;
			boolean tempIsMoved = isXMoved || isYMoved; // xy有点偏移都认为不是点击事件
			mIsClick = !tempIsMoved; // 若x与y偏移量都过小，则认为是一次click事件

			if (xMoved) {
				// Scroll if the user moved far enough along the X axis
				mTouchState = TOUCH_STATE_SCROLLING;
				if (mViewInitializeListener != null)
					initializeView(xDiff);
			}

			if (mTouchState == TOUCH_STATE_SCROLLING) {
				// Scroll to follow the motion event
				final int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;

				final int scrollX = getScrollX();
				if (deltaX < 0) {
					if (scrollX > 0) {
						scrollBy(Math.max(-scrollX, deltaX), 0);
					}
				} else if (deltaX > 0) {
					final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - scrollX - getWidth();
					if (availableToScroll > 0) {
						scrollBy(Math.min(availableToScroll, deltaX), 0);
					}
				}
				return true;
			}
			break;

		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();

				if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
					// Fling hard enough to move left
					snapToScreen(mCurrentScreen - 1);
				} else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - 1) {
					// Fling hard enough to move right
					snapToScreen(mCurrentScreen + 1);
				}
				// else if (velocityX < -SNAP_VELOCITY
				// && mCurrentScreen == getChildCount() - 1) {
				// snapToScreen(0);
				// }
				// else if (velocityX > SNAP_VELOCITY
				// && mCurrentScreen == 0) {
				// snapToScreen(getChildCount() - 1);
				// }
				else {
					snapToDestination();
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}

			mTouchState = TOUCH_STATE_REST;

			if (handler != null) {
				Message message = handler.obtainMessage(0);
				handler.sendMessageDelayed(message, timeSpan);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			snapToDestination();
			mTouchState = TOUCH_STATE_REST;
		}
		return true;
	}
	
	private void initializeView(final float direction) {
		if (direction > 0) {
			if (mLazyInit.contains(LazyInit.RIGHT)) {
				mLazyInit.remove(LazyInit.RIGHT);
				if (mCurrentBufferIndex+1 < mLoadedViews.size())
					mViewInitializeListener.onViewLazyInitialize(mLoadedViews.get(mCurrentBufferIndex + 1), mCurrentAdapterIndex + 1);
			}
		} else {
			if (mLazyInit.contains(LazyInit.LEFT)) {
				mLazyInit.remove(LazyInit.LEFT);
				if (mCurrentBufferIndex > 0)
					mViewInitializeListener.onViewLazyInitialize(mLoadedViews.get(mCurrentBufferIndex - 1), mCurrentAdapterIndex - 1);
			}
		}
	}

	@Override
	protected void onScrollChanged(int h, int v, int oldh, int oldv) {
		super.onScrollChanged(h, v, oldh, oldv);
		if (mIndicator != null) {
			/*
			 * The actual horizontal scroll origin does typically not match the
			 * perceived one. Therefore, we need to calculate the perceived
			 * horizontal scroll origin here, since we use a view buffer.
			 */
			int hPerceived = h + (mCurrentAdapterIndex - mCurrentBufferIndex) * getWidth();
			mIndicator.onScrolled(hPerceived, v, oldh, oldv);
		}
	}

	private void snapToDestination() {
		final int screenWidth = getWidth();
		final int whichScreen = (getScrollX() + (screenWidth / 2)) / screenWidth;

		snapToScreen(whichScreen);
	}

	private void snapToScreen(int whichScreen) {
		mLastScrollDirection = whichScreen - mCurrentScreen;
		if (!mScroller.isFinished())
			return;

		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

		mNextScreen = whichScreen;

		final int newX = whichScreen * getWidth();
		final int delta = newX - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
			mNextScreen = INVALID_SCREEN;
			postViewSwitched(mLastScrollDirection);
		}
	}

	/**
	 * Scroll to the {@link View} in the view buffer specified by the index.
	 * 
	 * @param indexInBuffer
	 *            Index of the view in the view buffer.
	 */
	private void setVisibleView(int indexInBuffer, boolean uiThread) {
		mCurrentScreen = Math.max(0, Math.min(indexInBuffer, getChildCount() - 1));
		int dx = (mCurrentScreen * getWidth()) - mScroller.getCurrX();
		mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(), dx, 0, 0);
		if (dx == 0)
			onScrollChanged(mScroller.getCurrX() + dx, mScroller.getCurrY(), mScroller.getCurrX() + dx,
					mScroller.getCurrY());
		if (uiThread)
			invalidate();
		else
			postInvalidate();
	}

	/**
	 * Set the listener that will receive notifications every time the {code
	 * ViewFlow} scrolls.
	 * 
	 * @param l
	 *            the scroll listener
	 */
	public void setOnViewSwitchListener(ViewSwitchListener l) {
		mViewSwitchListener = l;
	}

	public void setOnViewLazyInitializeListener(ViewLazyInitializeListener l) {
		mViewInitializeListener = l;
	}
	
	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		setAdapter(adapter, 0);
	}

	public void setAdapter(Adapter adapter, int initialPosition) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}

		mAdapter = adapter;

		if (mAdapter != null) {
			mDataSetObserver = new AdapterDataSetObserver();
			mAdapter.registerDataSetObserver(mDataSetObserver);

		}
		if (mAdapter == null || mAdapter.getCount() == 0)
			return;

		setSelection(initialPosition);
	}

	@Override
	public View getSelectedView() {
		return (mCurrentBufferIndex < mLoadedViews.size() ? mLoadedViews.get(mCurrentBufferIndex) : null);
	}

	@Override
	public int getSelectedItemPosition() {
		return mCurrentAdapterIndex;
	}

	/**
	 * Set the FlowIndicator
	 * 
	 * @param flowIndicator
	 */
	public void setFlowIndicator(CircleFlowIndicator flowIndicator) {
		mIndicator = flowIndicator;
		mIndicator.setViewFlow(this);
	}

	protected void recycleViews() {
		while (!mLoadedViews.isEmpty())
			recycleView(mLoadedViews.remove());
	}

	protected void recycleView(View v) {
		if (v == null)
			return;
		mRecycledViews.addFirst(v);
		detachViewFromParent(v);
	}

	protected View getRecycledView() {
		return (mRecycledViews.isEmpty() ? null : mRecycledViews.remove());
	}
	
	@Override
	public void setSelection(int position) {
		mNextScreen = INVALID_SCREEN;
		mScroller.forceFinished(true);
		if (mAdapter == null)
			return;
		
		position = Math.max(position, 0);
		position = Math.min(position, mAdapter.getCount()-1);

		recycleViews();

		View currentView = makeAndAddView(position, true);
		mLoadedViews.addLast(currentView);

		if (mViewInitializeListener != null)
			mViewInitializeListener.onViewLazyInitialize(currentView, position);

		for(int offset = 1; mSideBuffer - offset >= 0; offset++) {
			int leftIndex = position - offset;
			int rightIndex = position + offset;
			if(leftIndex >= 0)
				mLoadedViews.addFirst(makeAndAddView(leftIndex, false));
			if(rightIndex < mAdapter.getCount())
				mLoadedViews.addLast(makeAndAddView(rightIndex, true));
		}

		mCurrentBufferIndex = mLoadedViews.indexOf(currentView);
		mCurrentAdapterIndex = position;

		requestLayout();
		setVisibleView(mCurrentBufferIndex, false);
		if (mIndicator != null) {
			mIndicator.onSwitched(currentView, mCurrentAdapterIndex);
		}
		if (mViewSwitchListener != null) {
			mViewSwitchListener.onSwitched(currentView, mCurrentAdapterIndex);
		}
	}

	private void resetFocus() {
		recycleViews();
		removeAllViewsInLayout();
		mLazyInit.addAll(EnumSet.allOf(LazyInit.class));

		for (int i = Math.max(0, mCurrentAdapterIndex - mSideBuffer); i < Math
				.min(mAdapter.getCount(), mCurrentAdapterIndex + mSideBuffer
						+ 1); i++) {
			mLoadedViews.addLast(makeAndAddView(i, true));
			if (i == mCurrentAdapterIndex) {
				mCurrentBufferIndex = mLoadedViews.size() - 1;
				if (mViewInitializeListener != null)
					mViewInitializeListener.onViewLazyInitialize(mLoadedViews.getLast(), mCurrentAdapterIndex);
			}
		}
		requestLayout();
	}

	private void postViewSwitched(int direction) {
		if (direction == 0)
			return;

		if (direction > 0) { // to the right
			mCurrentAdapterIndex++;
			mCurrentBufferIndex++;
			mLazyInit.remove(LazyInit.LEFT);
			mLazyInit.add(LazyInit.RIGHT);

			// Recycle view outside buffer range
			if (mCurrentAdapterIndex > mSideBuffer) {
				recycleView(mLoadedViews.removeFirst());
				mCurrentBufferIndex--;
			}

			// Add new view to buffer
			int newBufferIndex = mCurrentAdapterIndex + mSideBuffer;
			if (newBufferIndex < mAdapter.getCount())
				mLoadedViews.addLast(makeAndAddView(newBufferIndex, true));

		} else { // to the left
			mCurrentAdapterIndex--;
			mCurrentBufferIndex--;
			mLazyInit.add(LazyInit.LEFT);
			mLazyInit.remove(LazyInit.RIGHT);

			// Recycle view outside buffer range
			if (mAdapter.getCount() - 1 - mCurrentAdapterIndex > mSideBuffer) {
				recycleView(mLoadedViews.removeLast());
			}

			// Add new view to buffer
			int newBufferIndex = mCurrentAdapterIndex - mSideBuffer;
			if (newBufferIndex > -1) {
				mLoadedViews.addFirst(makeAndAddView(newBufferIndex, false));
				mCurrentBufferIndex++;
			}

		}

		requestLayout();
		setVisibleView(mCurrentBufferIndex, true);
		if (mIndicator != null) {
			mIndicator.onSwitched(mLoadedViews.get(mCurrentBufferIndex),
					mCurrentAdapterIndex);
		}
		if (mViewSwitchListener != null) {
			mViewSwitchListener
					.onSwitched(mLoadedViews.get(mCurrentBufferIndex),
							mCurrentAdapterIndex);
		}
	}

	private View setupChild(View child, boolean addToEnd, boolean recycle) {
		ViewGroup.LayoutParams p = (ViewGroup.LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
					0);
		}
		if (recycle)
			attachViewToParent(child, (addToEnd ? -1 : 0), p);
		else
			addViewInLayout(child, (addToEnd ? -1 : 0), p, true);
		return child;
	}

	private View makeAndAddView(int position, boolean addToEnd, View convertView) {
		View view = mAdapter.getView(position, convertView, this);
		return setupChild(view, addToEnd, convertView != null);
	}

	private View makeAndAddView(int position, boolean addToEnd) {
		View view = obtainView(position);
		return setupChild(view, addToEnd, mLastObtainedViewWasRecycled);
	}
	
	private View obtainView(int position) {
		View convertView = getRecycledView();
		View view = mAdapter.getView(position, convertView, this);
		if(view != convertView && convertView != null)
			mRecycledViews.add(convertView);
		mLastObtainedViewWasRecycled = (view == convertView);
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			view.setLayoutParams(p);
		}
		return view;
	}
	
	class AdapterDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			View v = getChildAt(mCurrentBufferIndex);
			if (v != null) {
				for (int index = 0; index < mAdapter.getCount(); index++) {
					if (v.equals(mAdapter.getItem(index))) {
						mCurrentAdapterIndex = index;
						break;
					}
				}
			}
			resetFocus();
		}

		@Override
		public void onInvalidated() {
			// Not yet implemented!
		}

	}

	public void setTimeSpan(long timeSpan) {
		this.timeSpan = timeSpan;
	}

	public void setmSideBuffer(int sideBuffer) {
		this.mSideBuffer = sideBuffer;
	}
}