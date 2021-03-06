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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;

import io.reactivex.Observable;

public final class ContentObservable {

    private ContentObservable() {
        throw new AssertionError("No instances");
    }

    /**
     * Create Observable that wraps BroadcastReceiver and emits received intents.
     *
     * @param filter Selects the Intent broadcasts to be received.
     */
    public static Observable<Intent> fromBroadcast(
        final Context context,
        final IntentFilter filter
    ) {
        return Observable.create(new OnSubscribeBroadcastRegister(context, filter, null, null));
    }

    /**
     * Create Observable that wraps BroadcastReceiver and emits received intents.
     *
     * @param filter              Selects the Intent broadcasts to be received.
     * @param broadcastPermission String naming a permissions that a
     *                            broadcaster must hold in order to send an Intent to you.  If null,
     *                            no permission is required.
     * @param schedulerHandler    Handler identifying the thread that will receive
     *                            the Intent.  If null, the main thread of the process will be used.
     */
    public static Observable<Intent> fromBroadcast(
        final Context context,
        final IntentFilter filter,
        final String broadcastPermission,
        final Handler schedulerHandler
    ) {
        return Observable.create(new OnSubscribeBroadcastRegister(context, filter, broadcastPermission, schedulerHandler));
    }

    /**
     * Create Observable that wraps BroadcastReceiver and connects to LocalBroadcastManager
     * to emit received intents.
     *
     * @param filter Selects the Intent broadcasts to be received.
     */
    public static Observable<Intent> fromLocalBroadcast(
        final Context context,
        final IntentFilter filter
    ) {
        return Observable.create(new OnSubscribeLocalBroadcastRegister(context, filter));
    }

    /**
     * Create Observable that emits String keys whenever it changes in provided SharedPreferences
     * <p>
     * Items will be observed on the main Android UI thread
     */
    public static Observable<String> fromSharedPreferencesChanges(final SharedPreferences sharedPreferences) {
        return Observable.create(new OnSubscribeSharedPreferenceChange(sharedPreferences));
    }

    /**
     * Create Observable that emits the specified {@link Cursor} for each available position
     * of the cursor moving to the next position before each call and closing the cursor whether the
     * Observable completes or an error occurs.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Cursor> Observable<E> fromCursor(final E cursor) {
        return Observable.create(new OnSubscribeCursor(cursor));
    }

    public static Observable<Cursor> fromContentProvider(
        final ContentResolver cr,
        final Uri uri,
        final String[] projection,
        final String selection,
        final String[] selectionArgs,
        final String sortOrder
    ) {
        return Observable.create(new OnSubscribeContentProvider(cr, uri, projection, selection, selectionArgs, sortOrder));
    }

    public static Observable<Uri> fromContentObserver(
        @NonNull final Context context,
        @NonNull final Uri uri,
        final boolean notifyForDescendents
    ) {
        return Observable.create(new OnSubscribeContentObserver(context, uri, notifyForDescendents));
    }
}
