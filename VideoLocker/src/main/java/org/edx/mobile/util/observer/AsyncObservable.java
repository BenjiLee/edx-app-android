package org.edx.mobile.util.observer;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public enum AsyncObservable {
    ;

    // Using AsyncTask.THREAD_POOL_EXECUTOR to automatically support Espresso idling resources
    @VisibleForTesting
    @NonNull
    public static Executor EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR;

    @NonNull
    public static <T> Observable<T> fromCallable(@NonNull final Callable<T> callable) {
        // TODO: Use some kind of dedicated & cancelable thread pool / interactor?
        final RepeatingObservable<T> observable = new RepeatingObservable<>();
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                final T data;
                try {
                    data = callable.call();
                } catch (Throwable e) {
                    observable.onError(e);
                    return;
                }
                observable.onData(data);
            }
        });
        return observable;
    }
}
