package miyagi389.android.apps.tr.presentation.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle2.components.support.RxFragment;

import miyagi389.android.apps.tr.presentation.BuildConfig;
import miyagi389.android.apps.tr.presentation.MainApplication;
import miyagi389.android.apps.tr.presentation.di.FragmentComponent;
import miyagi389.android.apps.tr.presentation.di.FragmentModule;

/**
 * Base {@link android.support.v4.app.Fragment} class for every fragment in this application.
 */
public abstract class BaseFragment extends RxFragment {

    private FragmentComponent fragmentComponent;

    @NonNull
    public FragmentComponent getComponent() {
        if (fragmentComponent != null) {
            return fragmentComponent;
        }
        final Activity activity = getActivity();
        if (!(activity instanceof BaseActivity)) {
            throw new IllegalStateException(
                "The activity of this fragment is not an instance of BaseFragment");
        }
        fragmentComponent = ((BaseActivity) activity).getComponent()
            .plus(new FragmentModule(this));
        return fragmentComponent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (BuildConfig.DEBUG) {
            final RefWatcher refWatcher = MainApplication.getRefWatcher(getContext());
            refWatcher.watch(this);
        }
    }

    public void hideKeyboard(@NonNull final View view) {
        final InputMethodManager imm =
            (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void showError(@Nullable final String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
