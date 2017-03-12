package miyagi389.android.apps.tr.presentation.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ErrorLabelLayout extends LinearLayout implements ViewGroup.OnHierarchyChangeListener {

    public static final int DEFAULT_ERROR_LABEL_TEXT_SIZE = 12;
    public static final int DEFAULT_ERROR_LABEL_PADDING = 4;

    private int errorPaddingLeft;
    private int errorPaddingTop;
    private int errorPaddingRight;
    private int errorPaddingBottom;

    private TextView errorLabel;
    private Drawable drawable;
    private int errorColor;

    public ErrorLabelLayout(@NonNull final Context context) {
        super(context);
        initView();
    }

    public ErrorLabelLayout(
        @NonNull final Context context,
        @Nullable final AttributeSet attrs
    ) {
        super(context, attrs);
        initView();
    }

    public ErrorLabelLayout(
        @NonNull final Context context,
        @Nullable final AttributeSet attrs,
        final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOnHierarchyChangeListener(this);
        setOrientation(VERTICAL);
        errorColor = Color.parseColor("#D32F2F");
        errorPaddingLeft = dipsToPix(DEFAULT_ERROR_LABEL_PADDING);
        errorPaddingTop = 0;
        errorPaddingRight = dipsToPix(DEFAULT_ERROR_LABEL_PADDING);
        errorPaddingBottom = 0;
        initErrorLabel();
    }

    private void initErrorLabel() {
        errorLabel = new TextView(getContext());
        errorLabel.setFocusable(true);
        errorLabel.setFocusableInTouchMode(true);
        errorLabel.setTextSize(DEFAULT_ERROR_LABEL_TEXT_SIZE);
        errorLabel.setTextColor(errorColor);
        errorLabel.setPadding(errorPaddingLeft, errorPaddingTop, errorPaddingRight, errorPaddingBottom);
    }

    public void setErrorPadding(
        final int left,
        final int top,
        final int right,
        final int bottom
    ) {
        errorPaddingLeft = dipsToPix(left);
        errorPaddingTop = dipsToPix(top);
        errorPaddingRight = dipsToPix(right);
        errorPaddingBottom = dipsToPix(bottom);
        errorLabel.setPadding(errorPaddingLeft, errorPaddingTop, errorPaddingRight, errorPaddingBottom);
    }

    @SuppressWarnings("unused")
    public void setErrorColor(final int color) {
        errorColor = color;
        errorLabel.setTextColor(errorColor);
    }

    public void clearError() {
        errorLabel.setVisibility(INVISIBLE);
        drawable.clearColorFilter();
    }

    public void setError(@Nullable final String text) {
        errorLabel.setVisibility(VISIBLE);
        errorLabel.setText(text);
        // tint drawable
        drawable.setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
        // changing focus from EditText to error label, necessary for Android L only
        // EditText background Drawable is not tinted, until EditText remains focus
        errorLabel.requestFocus();
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
            drawable = getChildAt(0).getBackground();
            addView(errorLabel);
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
