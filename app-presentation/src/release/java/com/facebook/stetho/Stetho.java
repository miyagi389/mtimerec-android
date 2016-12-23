package com.facebook.stetho;

import android.content.Context;

/**
 * リリース用のダミー実装
 * see: <a href="http://qiita.com/kubode/items/a5afd116a432331a4fc6">Stethoをrelease時に使用しないようにする方法</a>
 */
public class Stetho {

    public static void initializeWithDefaults(@SuppressWarnings("UnusedParameters") final Context context) {
        // empty
    }
}
