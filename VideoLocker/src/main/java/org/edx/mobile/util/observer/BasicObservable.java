package org.edx.mobile.util.observer;

import android.support.annotation.NonNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class BasicObservable<T> implements Observable<T> {
    private final Set<Observer<T>> observers = new LinkedHashSet<>();

    @NonNull
    public Subscription subscribe(@NonNull final Observer<T> observer) {
        observers.add(observer);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                observers.remove(observer);
            }
        };
    }

    public void sendData(@NonNull T data) {
        for (Observer<T> observer : observers) {
            observer.onData(data);
        }
    }

    public void sendError(@NonNull Throwable error) {
        for (Observer<T> observer : observers) {
            observer.onError(error);
        }
    }
}
