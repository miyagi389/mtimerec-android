package miyagi389.android.apps.tr.presentation.ui;

import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.WindowManager;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import miyagi389.android.apps.tr.presentation.MainApplication;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.di.ActivityComponent;
import miyagi389.android.apps.tr.presentation.di.ActivityModule;

public abstract class BaseActivity extends RxAppCompatActivity {

    private final BaseActivity self = this;

    private ActivityComponent activityComponent;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        initializeFloatingWindow();
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public ActivityComponent getComponent() {
        if (activityComponent == null) {
            final MainApplication mainApplication = (MainApplication) getApplication();
            activityComponent = mainApplication.getApplicationComponent().plus(new ActivityModule(self));
        }
        return activityComponent;
    }

    private void initializeFloatingWindow() {
        if (shouldBeFloatingWindow()) {
            final Display display = getWindowManager().getDefaultDisplay();
            final Point screenSize = new Point();
            display.getSize(screenSize);
            final int screenWidth = screenSize.x;
            final int screenHeight = screenSize.y;

            setupFloatingWindow(
                (int) (screenWidth * getResources().getFraction(R.fraction.activity_floating_width, 1, 1)),
                (int) (screenHeight * getResources().getFraction(R.fraction.activity_floating_height, 1, 1)),
                1,
                0.4f
            );
        }
    }

    /**
     * Returns true if the theme sets the {@code R.attr.isFloatingWindow} flag to true.
     */
    private boolean shouldBeFloatingWindow() {
        final Resources.Theme theme = getTheme();
        final TypedValue floatingWindowFlag = new TypedValue();

        // Check isFloatingWindow flag is defined in theme.
        //noinspection SimplifiableIfStatement
        if (theme == null || !theme
            .resolveAttribute(R.attr.isFloatingWindow, floatingWindowFlag, true)) {
            return false;
        }

        return (floatingWindowFlag.data != 0);
    }

    /**
     * Configure this Activity as a floating window, with the given {@code width}, {@code height}
     * and {@code alpha}, and dimming the background with the given {@code dim} value.
     */
    private void setupFloatingWindow(
        final int width,
        final int height,
        final int alpha,
        final float dim
    ) {
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = height;
        params.alpha = alpha;
        params.dimAmount = dim;
        params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(params);
    }

    /**
     * Replace a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    protected void replaceFragment(
        final int containerViewId,
        final Fragment fragment
    ) {
        replaceFragment(containerViewId, fragment, fragment.getClass().getName());
    }

    /**
     * Replace a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    protected void replaceFragment(
        final int containerViewId,
        final Fragment fragment,
        final String tag
    ) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerViewId, fragment, tag);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public final void runOnUiThreadDelayed(
        @NonNull final Runnable action,
        final long delayMillis
    ) {
        self.handler.postDelayed(action, delayMillis);
    }
}
