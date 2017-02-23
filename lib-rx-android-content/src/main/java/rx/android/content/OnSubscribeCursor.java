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

import android.database.Cursor;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Emits a {@link Cursor} for every available position.
 */
final class OnSubscribeCursor<E extends Cursor> implements ObservableOnSubscribe<E> {

    private final E cursor;

    OnSubscribeCursor(final E cursor) {
        this.cursor = cursor;
    }

    @Override
    public void subscribe(final ObservableEmitter<E> e) throws Exception {
        try {
            while (!e.isDisposed() && cursor.moveToNext()) {
                e.onNext(cursor);
            }
            if (!e.isDisposed()) {
                e.onComplete();
            }
        } catch (final Throwable throwable) {
            if (!e.isDisposed()) {
                e.onError(throwable);
            }
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}
