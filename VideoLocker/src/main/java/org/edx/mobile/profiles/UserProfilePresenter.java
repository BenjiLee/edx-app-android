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
        view.setContent(ViewInterface.ContentType.LOADING);
        observeOnView(userProfileInteractor.observeAccount()).subscribe(new Observer<Account>() {
            @Override
            public void onData(@NonNull Account account) {
                final ViewInterface.LimitedProfileMessage limitedProfileMessage;
                if (account.requiresParentalConsent() || account.getAccountPrivacy() == Account.Privacy.PRIVATE) {
                    limitedProfileMessage = isViewingOwnProfile ? ViewInterface.LimitedProfileMessage.OWN_PROFILE : ViewInterface.LimitedProfileMessage.OTHER_USERS_PROFILE;
                } else {
                    limitedProfileMessage = ViewInterface.LimitedProfileMessage.NONE;
                    String languageName = null;
                    if (!account.getLanguageProficiencies().isEmpty()) {
                        try {
                            languageName = LocaleUtils.getLanguageNameFromCode(account.getLanguageProficiencies().get(0).getCode());
                        } catch (InvalidLocaleException e) {
                            logger.error(e, true);
                        }
                    }
                    view.setLanguage(languageName);

                    String countryName = null;
                    if (!TextUtils.isEmpty(account.getCountry())) {
                        try {
                            countryName = LocaleUtils.getCountryNameFromCode(account.getCountry());
                        } catch (InvalidLocaleException e) {
                            logger.error(e, true);
                        }
                    }
                    view.setLocation(countryName);
                }
                view.setLimitedProfileMessage(limitedProfileMessage);

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
                        view.setAboutMeText(account.getBio());
                    }
                } else {
                    contentType = ViewInterface.ContentType.EMPTY;
                }
                view.setContent(contentType);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                view.setContent(ViewInterface.ContentType.EMPTY);
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

        void setLimitedProfileMessage(@NonNull LimitedProfileMessage message);

        void setLanguage(@Nullable String languageText);

        void setLocation(@Nullable String locationText);

        void setContent(@NonNull ContentType contentType);

        void setAboutMeText(@NonNull String bio);

        void showError(@NonNull Throwable error);

        void setPhotoUri(@NonNull UserProfileInteractor.ImageResource model);

        void setName(@NonNull String name);

        void navigateToProfileEditor(@NonNull String username);

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
