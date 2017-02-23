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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;

class OnSubscribeBroadcastRegister implements ObservableOnSubscribe<Intent> {

    private final Context context;
    private final IntentFilter intentFilter;
    private final String broadcastPermission;
    private final Handler schedulerHandler;

    OnSubscribeBroadcastRegister(
        @NonNull final Context context,
        @NonNull final IntentFilter intentFilter,
        @Nullable final String broadcastPermission,
        @Nullable final Handler schedulerHandler
    ) {
        this.context = context;
        this.intentFilter = intentFilter;
        this.broadcastPermission = broadcastPermission;
        this.schedulerHandler = schedulerHandler;
    }

    @Override
    public void subscribe(final ObservableEmitter<Intent> e) throws Exception {
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(
                final Context context,
                final Intent intent
            ) {
                e.onNext(intent);
            }
        };

        final Disposable disposable = Disposables.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                context.unregisterReceiver(broadcastReceiver);
            }
        });
        e.setDisposable(disposable);

        context.registerReceiver(broadcastReceiver, intentFilter, broadcastPermission, schedulerHandler);
    }
}
