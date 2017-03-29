package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.annimon.stream.Optional;

import timber.log.Timber;

public class WebViewFragment extends Fragment {

    private final WebViewFragment self = this;

    public static final String EXTRA_URI = "EXTRA_URI";

    // すぐ終わる処理に対して、Progress... の表示を出しても意味が無いので
    // ある程度時間がたったら Progress... を表示するための delay
    private static final int PROGRESS_DELAY_MILLIS = 1500;
    private static final int START_LOADING_DELAY_MILLIS = 1000;

    private WebView webView;

    public interface Listener {

        void onTitleChanged(CharSequence title);

        void onStartLoading(int delayMillis);

        void onProgressChanged(
            int newProgress,
            int delayMillis
        );

        void onStopLoading();
    }

    // Required empty public constructor
    public WebViewFragment() {
    }

    @NonNull
    /*package*/ static WebViewFragment newInstance(
        @NonNull final Uri uri,
        @Nullable final String title
    ) {
        final WebViewFragment f = new WebViewFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_URI, uri);
        args.putString(Intent.EXTRA_TITLE, title);
        f.setArguments(args);
        return f;
    }

    @NonNull
    /*package*/ static WebViewFragment newInstance(
        @NonNull final String bodyText,
        @Nullable final String title
    ) {
        final WebViewFragment f = new WebViewFragment();
        final Bundle args = new Bundle();
        args.putString(Intent.EXTRA_TEXT, bodyText);
        args.putString(Intent.EXTRA_TITLE, title);
        f.setArguments(args);
        return f;
    }

    @Nullable
    private Uri getArgumentsUri() {
        final Bundle args = getArguments();
        return args == null ? null : args.getParcelable(EXTRA_URI);
    }

    @Nullable
    private String getArgumentsText() {
        final Bundle args = getArguments();
        return args == null ? null : args.getString(Intent.EXTRA_TEXT);
    }

    @Nullable
    private String getArgumentsTitle() {
        final Bundle args = getArguments();
        return args == null ? null : args.getString(Intent.EXTRA_TITLE);
    }

    private Listener listener;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (!(context instanceof Listener)) {
            throw new ClassCastException(
                context.toString() + " must implement " + Listener.class.getSimpleName()
            );
        }
        self.listener = (Listener) context;
    }

    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        final ViewGroup container,
        final Bundle savedInstanceState
    ) {
        final WebView v = new WebView(getActivity());
        v.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        v.setWebChromeClient(
            new WebChromeClient() {
                @Override
                public void onProgressChanged(
                    final WebView view,
                    final int newProgress
                ) {
                    if (newProgress <= 30) {    // SUPPRESS CHECKSTYLE MagicNumber
                        listener.onProgressChanged(newProgress, PROGRESS_DELAY_MILLIS);
                    } else {
                        listener.onProgressChanged(newProgress, 0);
                    }
                }
            }
        );
        v.setWebViewClient(
            new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(
                    final WebView view,
                    final WebResourceRequest request
                ) {
                    listener.onStartLoading(0);
                    return super.shouldOverrideUrlLoading(view, request);
                }

                @Override
                public void onPageStarted(
                    final WebView view,
                    final String url,
                    final Bitmap favicon
                ) {
                    listener.onStartLoading(START_LOADING_DELAY_MILLIS);
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(
                    final WebView view,
                    final String url
                ) {
                    Timber.d("onPageFinished() url=%s", url);

                    super.onPageFinished(view, url);

                    listener.onStopLoading();
                }
            }
        );

        final WebSettings webSettings = v.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView = v;

        return v;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        clearWebViewData();

        final CharSequence title = getArgumentsTitle();
        if (!TextUtils.isEmpty(title)) {
            listener.onTitleChanged(title);
        }

        final Uri uri = getArgumentsUri();
        final String text = getArgumentsText();
        if (uri != null) {
            webView.loadUrl(uri.toString());
        } else if (!TextUtils.isEmpty(text)) {
            // FIXME #loadDataWithBaseURL で読み込むと、WebView.goBack()で戻らない。
            webView.loadDataWithBaseURL(null, text, "text/html", "UTF-8", null);
        } else {
            Optional.of(getActivity()).ifPresent(activity -> {
                activity.onBackPressed();
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearWebViewData();
    }

    public boolean onBackPressed() {
        if (!webView.canGoBack()) {
            return false;
        }

        webView.goBack();

        return true;
    }

    private void clearWebViewData() {
        final WebView w = webView;
        w.clearCache(true);
        w.clearHistory();
        w.clearSslPreferences();
    }
}
