/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.android.content;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;

class OnSubscribeContentObserver implements ObservableOnSubscribe<Uri> {

    private final Context context;
    private final Uri uri;
    private final boolean notifyForDescendents;

    public OnSubscribeContentObserver(
        @NonNull final Context context,
        @NonNull final Uri uri,
        final boolean notifyForDescendents
    ) {
        this.context = context;
        this.uri = uri;
        this.notifyForDescendents = notifyForDescendents;
    }

    @Override
    public void subscribe(final ObservableEmitter<Uri> e) throws Exception {
        final ContentObserver contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(
                final boolean selfChange,
                final Uri uri
            ) {
                e.onNext(uri);
            }
        };

        final Disposable disposable = Disposables.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                context.getContentResolver().unregisterContentObserver(contentObserver);
            }
        });
        e.setDisposable(disposable);

        context.getContentResolver().registerContentObserver(uri, notifyForDescendents, contentObserver);
    }
}
