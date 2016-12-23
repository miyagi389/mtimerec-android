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

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

class OnSubscribeContentObserver implements Observable.OnSubscribe<Uri> {

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
    public void call(final Subscriber<? super Uri> subscriber) {
        final ContentObserver contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(
                final boolean selfChange,
                final Uri uri
            ) {
                subscriber.onNext(uri);
            }
        };

        final Subscription subscription = Subscriptions.create(new Action0() {
            @Override
            public void call() {
                context.getContentResolver().unregisterContentObserver(contentObserver);
            }
        });

        subscriber.add(subscription);
        context.getContentResolver().registerContentObserver(uri, notifyForDescendents, contentObserver);
    }
}
