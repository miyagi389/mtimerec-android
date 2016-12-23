package miyagi389.android.apps.tr.presentation.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ErrorLabelLayout extends LinearLayout implements ViewGroup.OnHierarchyChangeListener {

    public static final int DEFAULT_ERROR_LABEL_TEXT_SIZE = 12;
    public static final int DEFAULT_ERROR_LABEL_PADDING = 4;

    private int mErrorPaddingLeft;
    private int mErrorPaddingTop;
    private int mErrorPaddingRight;
    private int mErrorPaddingBottom;

    private TextView mErrorLabel;
    private Drawable mDrawable;
    private int mErrorColor;

    public ErrorLabelLayout(final Context context) {
        super(context);
        initView();
    }

    public ErrorLabelLayout(
        final Context context,
        final AttributeSet attrs
    ) {
        super(context, attrs);
        initView();
    }

    public ErrorLabelLayout(
        final Context context,
        final AttributeSet attrs,
        final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOnHierarchyChangeListener(this);
        setOrientation(VERTICAL);
        mErrorColor = Color.parseColor("#D32F2F");
        mErrorPaddingLeft = dipsToPix(DEFAULT_ERROR_LABEL_PADDING);
        mErrorPaddingTop = 0;
        mErrorPaddingRight = dipsToPix(DEFAULT_ERROR_LABEL_PADDING);
        mErrorPaddingBottom = 0;
        initErrorLabel();
    }

    private void initErrorLabel() {
        mErrorLabel = new TextView(getContext());
        mErrorLabel.setFocusable(true);
        mErrorLabel.setFocusableInTouchMode(true);
        mErrorLabel.setTextSize(DEFAULT_ERROR_LABEL_TEXT_SIZE);
        mErrorLabel.setTextColor(mErrorColor);
        mErrorLabel.setPadding(mErrorPaddingLeft, mErrorPaddingTop, mErrorPaddingRight, mErrorPaddingBottom);
    }

    public void setErrorPadding(
        final int left,
        final int top,
        final int right,
        final int bottom
    ) {
        mErrorPaddingLeft = dipsToPix(left);
        mErrorPaddingTop = dipsToPix(top);
        mErrorPaddingRight = dipsToPix(right);
        mErrorPaddingBottom = dipsToPix(bottom);
        mErrorLabel.setPadding(mErrorPaddingLeft, mErrorPaddingTop, mErrorPaddingRight, mErrorPaddingBottom);
    }

    @SuppressWarnings("unused")
    public void setErrorColor(final int color) {
        mErrorColor = color;
        mErrorLabel.setTextColor(mErrorColor);
    }

    public void clearError() {
        mErrorLabel.setVisibility(INVISIBLE);
        mDrawable.clearColorFilter();
    }

    public void setError(final String text) {
        mErrorLabel.setVisibility(VISIBLE);
        mErrorLabel.setText(text);
        // tint drawable
        mDrawable.setColorFilter(mErrorColor, PorterDuff.Mode.SRC_ATOP);
        // changing focus from EditText to error label, necessary for Android L only
        // EditText background Drawable is not tinted, until EditText remains focus
        mErrorLabel.requestFocus();
    }

    /**
     * {@link ViewGroup.OnHierarchyChangeListener#onChildViewAdded(View, View)}
     */
    @Override
    public void onChildViewAdded(
        final View parent,
        final View child
    ) {
        final int childCount = getChildCount();
        if (childCount == 1) {
            mDrawable = getChildAt(0).getBackground();
            addView(mErrorLabel);
        }
    }

    /**
     * {@link ViewGroup.OnHierarchyChangeListener#onChildViewRemoved(View, View)}
     */
    @Override
    public void onChildViewRemoved(
        final View parent,
        final View child
    ) {
    }

    private int dipsToPix(final float dps) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, getResources().getDisplayMetrics());
    }
}
