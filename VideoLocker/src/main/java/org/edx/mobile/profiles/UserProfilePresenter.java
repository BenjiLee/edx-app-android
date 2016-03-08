package org.edx.mobile.profiles;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.edx.mobile.logger.Logger;
import org.edx.mobile.model.api.ProfileModel;
import org.edx.mobile.module.analytics.ISegment;
import org.edx.mobile.module.prefs.UserPrefs;
import org.edx.mobile.user.Account;
import org.edx.mobile.user.UserAPI;
import org.edx.mobile.util.InvalidLocaleException;
import org.edx.mobile.util.LocaleUtils;
import org.edx.mobile.util.observer.Observer;
import org.edx.mobile.view.ViewHoldingPresenter;

import de.greenrobot.event.EventBus;

public class UserProfilePresenter extends ViewHoldingPresenter<UserProfilePresenter.ViewInterface> {

    @NonNull
    private final String username;

    private final boolean isViewingOwnProfile;

    @NonNull
    private final UserProfileInteractor userProfileInteractor;

    protected final Logger logger = new Logger(getClass().getName());

    public UserProfilePresenter(@NonNull final String username, @NonNull UserPrefs userPrefs, @NonNull ISegment segment, @NonNull EventBus eventBus, @NonNull UserAPI userAPI) {
        this.username = username;
        this.userProfileInteractor = new UserProfileInteractor(username, userAPI, eventBus);

        final ProfileModel model = userPrefs.getProfile();
        isViewingOwnProfile = null != model && model.username.equalsIgnoreCase(username);

        segment.trackProfileViewed(username);
    }

    @Override
    public void attachView(@NonNull final ViewInterface view) {
        super.attachView(view);
        view.setName(username);
        view.setEditProfileMenuButtonVisible(isViewingOwnProfile);
        view.setProfile(new ViewInterface.UserProfileViewModel(ViewInterface.LimitedProfileMessage.NONE, null, null, ViewInterface.ContentType.LOADING, null));
        observeOnView(userProfileInteractor.observeAccount()).subscribe(new Observer<Account>() {
            @Override
            public void onData(@NonNull Account account) {
                final ViewInterface.LimitedProfileMessage limitedProfileMessage;
                String languageName = null;
                String countryName = null;
                if (account.requiresParentalConsent() || account.getAccountPrivacy() == Account.Privacy.PRIVATE) {
                    limitedProfileMessage = isViewingOwnProfile ? ViewInterface.LimitedProfileMessage.OWN_PROFILE : ViewInterface.LimitedProfileMessage.OTHER_USERS_PROFILE;
                } else {
                    limitedProfileMessage = ViewInterface.LimitedProfileMessage.NONE;
                    if (!account.getLanguageProficiencies().isEmpty()) {
                        try {
                            languageName = LocaleUtils.getLanguageNameFromCode(account.getLanguageProficiencies().get(0).getCode());
                        } catch (InvalidLocaleException e) {
                            logger.error(e, true);
                        }
                    }

                    if (!TextUtils.isEmpty(account.getCountry())) {
                        try {
                            countryName = LocaleUtils.getCountryNameFromCode(account.getCountry());
                        } catch (InvalidLocaleException e) {
                            logger.error(e, true);
                        }
                    }
                }

                final ViewInterface.ContentType contentType;
                if (isViewingOwnProfile && account.requiresParentalConsent()) {
                    contentType = ViewInterface.ContentType.PARENTAL_CONSENT_REQUIRED;

                } else if (isViewingOwnProfile && TextUtils.isEmpty(account.getBio()) && account.getAccountPrivacy() != Account.Privacy.ALL_USERS) {
                    contentType = ViewInterface.ContentType.INCOMPLETE;

                } else if (account.getAccountPrivacy() != Account.Privacy.PRIVATE) {
                    if (TextUtils.isEmpty(account.getBio())) {
                        contentType = ViewInterface.ContentType.NO_ABOUT_ME;
                    } else {
                        contentType = ViewInterface.ContentType.ABOUT_ME;
                    }
                } else {
                    contentType = ViewInterface.ContentType.EMPTY;
                }
                view.setProfile(new ViewInterface.UserProfileViewModel(limitedProfileMessage, languageName, countryName, contentType, account.getBio()));
            }

            @Override
            public void onError(@NonNull Throwable error) {
                view.setProfile(new ViewInterface.UserProfileViewModel(ViewInterface.LimitedProfileMessage.NONE, null, null, ViewInterface.ContentType.EMPTY, null));
                view.showError(error);
            }
        });
        observeOnView(userProfileInteractor.observeProfilePhoto()).subscribe(new Observer<UserProfileInteractor.ImageResource>() {
            @Override
            public void onData(@NonNull UserProfileInteractor.ImageResource data) {
                view.setPhotoUri(data);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                // Do nothing; leave whatever image/placeholder is already displayed
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        userProfileInteractor.destroy();
    }

    public void onEditProfile() {
        assert getView() != null;
        getView().navigateToProfileEditor(username);
    }

    public interface ViewInterface {
        void setEditProfileMenuButtonVisible(boolean visible);

        void setProfile(@NonNull UserProfileViewModel profile);

        void showError(@NonNull Throwable error);

        void setPhotoUri(@NonNull UserProfileInteractor.ImageResource model);

        void setName(@NonNull String name);

        void navigateToProfileEditor(@NonNull String username);

        class UserProfileViewModel {
            @NonNull
            public final LimitedProfileMessage limitedProfileMessage;

            @Nullable
            public final String language;

            @Nullable
            public final String location;

            @NonNull
            public final ContentType contentType;

            @Nullable
            public final String bio;

            public UserProfileViewModel(@NonNull LimitedProfileMessage message, @Nullable String language, @Nullable String location, @NonNull ContentType contentType, @Nullable String bio) {
                limitedProfileMessage = message;
                this.language = language;
                this.location = location;
                this.contentType = contentType;
                this.bio = bio;
            }

            @Override
            public String toString() {
                return "UserProfileViewModel{" +
                        "limitedProfileMessage=" + limitedProfileMessage +
                        ", language='" + language + '\'' +
                        ", location='" + location + '\'' +
                        ", contentType=" + contentType +
                        ", bio='" + bio + '\'' +
                        '}';
            }
        }

        enum LimitedProfileMessage {
            NONE,
            OWN_PROFILE,
            OTHER_USERS_PROFILE
        }

        enum ContentType {
            EMPTY,
            LOADING,
            NO_ABOUT_ME,
            INCOMPLETE,
            PARENTAL_CONSENT_REQUIRED,
            ABOUT_ME
        }
    }
}
