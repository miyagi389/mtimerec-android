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

import android.content.SharedPreferences;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;

class OnSubscribeSharedPreferenceChange implements ObservableOnSubscribe<String> {

    private final SharedPreferences sharedPreferences;

    OnSubscribeSharedPreferenceChange(final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void subscribe(final ObservableEmitter<String> e) throws Exception {
        final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(
                final SharedPreferences sharedPreferences,
                final String key
            ) {
                e.onNext(key);
            }
        };

        final Disposable disposable = Disposables.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
            }
        });
        e.setDisposable(disposable);

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }
}
