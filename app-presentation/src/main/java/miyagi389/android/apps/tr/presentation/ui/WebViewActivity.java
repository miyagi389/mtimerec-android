package miyagi389.android.apps.tr.presentation.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;

import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.databinding.WebViewActivityBinding;

public class WebViewActivity
    extends BaseActivity
    implements WebViewFragment.Listener {

    /**
     * Handler Message: タイトルバーのプログレスアイコン
     */
    private static final int MESSAGE_LOADING = 1;

    private final WebViewActivity self = this;

    private WebViewActivityBinding binding;

    private WebViewFragment webViewFragment;

    private Handler progressHandler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingContentView();
        initializeActivity();
        progressHandler = new ProgressHandler(self);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressHandler.removeMessages(MESSAGE_LOADING);
    }

    private void bindingContentView() {
        self.binding = DataBindingUtil.setContentView(self, R.layout.web_view_activity);
        self.binding.toolbar.setTitle(getTitle());
        setSupportActionBar(self.binding.toolbar);
    }

    private void initializeActivity() {
        final FragmentManager fm = getSupportFragmentManager();
        self.webViewFragment = (WebViewFragment) fm.findFragmentById(R.id.content_wrapper);
        if (self.webViewFragment == null) {
            final Uri uri = getIntentUri();
            final String text = getIntentText();
            final String title = getIntentTitle();
            if (uri != null) {
                self.webViewFragment = WebViewFragment.newInstance(uri, title);
            } else if (!TextUtils.isEmpty(text)) {
                self.webViewFragment = WebViewFragment.newInstance(text, title);
            }
            replaceFragment(R.id.content_wrapper, self.webViewFragment);
        }
    }

    @Nullable
    private Uri getIntentUri() {
        final Intent intent = getIntent();
        return intent == null ? null : intent.getData();
    }

    @Nullable
    private String getIntentText() {
        final Intent intent = getIntent();
        return intent == null ? null : intent.getStringExtra(Intent.EXTRA_TEXT);
    }

    @Nullable
    private String getIntentTitle() {
        final Intent intent = getIntent();
        return intent == null ? null : intent.getStringExtra(Intent.EXTRA_TITLE);
    }

    /**
     * {@link WebViewFragment.Listener#onTitleChanged(CharSequence)}
     */
    @Override
    public void onTitleChanged(final CharSequence title) {
        self.binding.toolbar.setTitle(title);
    }

    /**
     * {@link WebViewFragment.Listener#onStartLoading(int)}
     */
    @Override
    public void onStartLoading(final int delayMillis) {
        showDelayProgress(delayMillis);
    }

    /**
     * {@link WebViewFragment.Listener#onProgressChanged(int, int)}
     */
    @Override
    public void onProgressChanged(
        final int newProgress,
        final int delayMillis
    ) {
        // TODO 将来的に進捗表示が必要になったら実装する。
    }

    /**
     * {@link WebViewFragment.Listener#onStopLoading()}
     */
    @Override
    public void onStopLoading() {
        hideDelayProgress();
    }

    private void showDelayProgress(final int delayMillis) {
        progressHandler.removeMessages(MESSAGE_LOADING);
        if (delayMillis > 0) {
            final Message msg = progressHandler.obtainMessage(MESSAGE_LOADING);
            progressHandler.sendMessageDelayed(msg, delayMillis);
        } else {
            setProgressIndeterminateVisibility(true);
        }
    }

    private void hideDelayProgress() {
        progressHandler.removeMessages(MESSAGE_LOADING);
        setProgressIndeterminateVisibility(false);
    }

    public void setProgressIndeterminateVisibility(final boolean visible) {
        self.binding.progress.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private static class ProgressHandler extends Handler {

        private final WeakReference<WebViewActivity> activity;

        ProgressHandler(@Nonnull final WebViewActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final WebViewActivity activity = this.activity.get();
            if (activity == null) {
                return;
            }

            switch (msg.what) {
                case MESSAGE_LOADING:
                    activity.setProgressIndeterminateVisibility(true);
                    break;
                default:
                    break;
            }
        }
    }
}
