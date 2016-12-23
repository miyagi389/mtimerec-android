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

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import rx.Observable;
import rx.Subscriber;

/**
 * Emits a {@link Cursor} for every available position.
 */
final class OnSubscribeContentProvider implements Observable.OnSubscribe<Cursor> {

    private final ContentResolver cr;
    private final Uri uri;
    private final String[] projection;
    private final String selection;
    private final String[] selectionArgs;
    private final String sortOrder;

    OnSubscribeContentProvider(
        final ContentResolver cr,
        final Uri uri,
        final String[] projection,
        final String selection,
        final String[] selectionArgs,
        final String sortOrder
    ) {
        this.cr = cr;
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    @Override
    public void call(final Subscriber<? super Cursor> subscriber) {
        Cursor cursor = null;
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            if (cursor != null) {
                while (!subscriber.isUnsubscribed() && cursor.moveToNext()) {
                    subscriber.onNext(cursor);
                }
            }
            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        } catch (Throwable e) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onError(e);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}
