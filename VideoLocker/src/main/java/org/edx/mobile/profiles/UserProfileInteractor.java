package org.edx.mobile.profiles;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.edx.mobile.event.AccountUpdatedEvent;
import org.edx.mobile.event.ProfilePhotoUpdatedEvent;
import org.edx.mobile.user.Account;
import org.edx.mobile.user.UserAPI;
import org.edx.mobile.util.observer.AsyncObservable;
import org.edx.mobile.util.observer.Observable;
import org.edx.mobile.util.observer.Observer;
import org.edx.mobile.util.observer.RepeatingObservable;

import java.util.concurrent.Callable;

import de.greenrobot.event.EventBus;

public class UserProfileInteractor {
    @NonNull
    private final String username;

    @NonNull
    private final EventBus eventBus;

    @NonNull
    private final RepeatingObservable<Account> account = new RepeatingObservable<>();

    @NonNull
    private final RepeatingObservable<ImageResource> photo = new RepeatingObservable<>();

    public UserProfileInteractor(@NonNull final String username, @NonNull final UserAPI userAPI, @NonNull EventBus eventBus) {
        this.username = username;
        this.eventBus = eventBus;
        eventBus.register(this);

        AsyncObservable.fromCallable(new Callable<Account>() {
            @Override
            public Account call() throws Exception {
                return userAPI.getAccount(username);
            }
        }).subscribe(new Observer<Account>() {
            @Override
            public void onData(@NonNull Account data) {
                handleNewAccount(data);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                account.onError(error);
            }
        });
    }

    @NonNull
    public Observable<Account> observeAccount() {
        return account;
    }

    @NonNull
    public Observable<ImageResource> observeProfilePhoto() {
        return photo;
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull AccountUpdatedEvent event) {
        if (!event.getAccount().getUsername().equalsIgnoreCase(username)) {
            return;
        }
        handleNewAccount(event.getAccount());
    }

    private void handleNewAccount(@NonNull Account data) {
        account.onData(data);
        photo.onData(new ImageResource(data.getProfileImage().hasImage() ? Uri.parse(data.getProfileImage().getImageUrlFull()) : null, true));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull ProfilePhotoUpdatedEvent event) {
        if (!event.getUsername().equalsIgnoreCase(username)) {
            return;
        }
        photo.onData(new ImageResource(event.getUri(), false));
    }

    public void destroy() {
        eventBus.unregister(this);
    }

    public static class ImageResource {
        @Nullable
        public final Uri uri;

        public final boolean shouldReadFromCache;

        public ImageResource(@Nullable Uri uri, boolean shouldReadFromCache) {
            this.uri = uri;
            this.shouldReadFromCache = shouldReadFromCache;
        }
    }
}
