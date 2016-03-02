package org.edx.mobile.util.observer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This observable keeps track of the last data or error it observes,
 * and repeats it to new subscribers.
 */
public class RepeatingObservable<T> implements Observer<T>, Observable<T> {
    @NonNull
    private final BasicObservable<T> observable = new BasicObservable<>();

    @Nullable
    private T lastData;

    @Nullable
    private Throwable lastError;

    @Override
    public void onData(@NonNull T data) {
        lastError = null;
        lastData = data;
        observable.sendData(data);
    }

    @Override
    public void onError(@NonNull Throwable error) {
        lastData = null;
        lastError = error;
        observable.sendError(error);
    }

    @Override
    @NonNull
    public Subscription subscribe(@NonNull Observer<T> observer) {
        if (null != lastData) {
            observer.onData(lastData);
        } else if (null != lastError) {
            observer.onError(lastError);
        }
        return observable.subscribe(observer);
    }
}
