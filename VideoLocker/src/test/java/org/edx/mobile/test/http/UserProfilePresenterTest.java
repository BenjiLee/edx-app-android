package org.edx.mobile.test.http;

import android.net.Uri;
import android.support.annotation.NonNull;

import org.edx.mobile.http.RetroHttpException;
import org.edx.mobile.model.api.ProfileModel;
import org.edx.mobile.module.analytics.ISegment;
import org.edx.mobile.module.prefs.UserPrefs;
import org.edx.mobile.profiles.UserProfileInteractor;
import org.edx.mobile.profiles.UserProfilePresenter;
import org.edx.mobile.test.PresenterTest;
import org.edx.mobile.user.Account;
import org.edx.mobile.user.ProfileImage;
import org.edx.mobile.user.UserAPI;
import org.junit.Test;
import org.mockito.Mock;

import de.greenrobot.event.EventBus;

import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserProfilePresenterTest extends PresenterTest<UserProfilePresenter, UserProfilePresenter.ViewInterface> {

    static final String PROFILE_USERNAME = "john_doe";
    static final String FULL_IMAGE_URL = "http://example.com/image.jpg";

    @Mock
    UserAPI userAPI;
    @Mock
    UserPrefs userPrefs;
    @Mock
    ISegment segment;
    @Mock
    EventBus eventBus;

    private void createPresenter() {
        startPresenter(new UserProfilePresenter(
                PROFILE_USERNAME,
                userPrefs,
                segment,
                eventBus,
                userAPI
        ));
    }

    @Test
    public void tracksProfileView() {
        createPresenter();
        verify(segment).trackProfileViewed(PROFILE_USERNAME);
    }

    @Test
    public void whenNoAboutMeReturned_withAnonymousUser_showsNoAboutMeContent() {
        configureBareMockAccount();
        createPresenter();
        verify(view).setContent(UserProfilePresenter.ViewInterface.ContentType.NO_ABOUT_ME);
    }

    @Test
    public void whenParentalConsentIsRequired_withAuthenticatedUserEqualToUserBeingViewed_showsParentalConsentRequiredContent() {
        setAuthenticatedUsername(PROFILE_USERNAME);
        final Account account = configureBareMockAccount();
        when(account.requiresParentalConsent()).thenReturn(true);
        createPresenter();
        verify(view).setContent(UserProfilePresenter.ViewInterface.ContentType.PARENTAL_CONSENT_REQUIRED);
    }

    @Test
    public void whenParentalConsentIsRequired_withAnonymousUser_showsNoAboutMe() {
        final Account account = configureBareMockAccount();
        when(account.requiresParentalConsent()).thenReturn(true);
        createPresenter();
        verify(view).setContent(UserProfilePresenter.ViewInterface.ContentType.NO_ABOUT_ME);
    }

    @Test
    public void withProfileImage_setsFullImageUrlAsPhotoUri() {
        final Account account = configureBareMockAccount();
        final ProfileImage profileImage = account.getProfileImage();
        when(profileImage.hasImage()).thenReturn(true);
        when(profileImage.getImageUrlFull()).thenReturn(FULL_IMAGE_URL);
        createPresenter();
        verify(view).setPhotoUri(refEq(new UserProfileInteractor.ImageResource(Uri.parse(FULL_IMAGE_URL), true)));
    }

    @Test
    public void withNoProfileImage_setsNullPhotoUri() {
        configureBareMockAccount();
        createPresenter();
        verify(view).setPhotoUri(refEq(new UserProfileInteractor.ImageResource(null, true)));
    }

    @Test
    public void withGetAccountException_setsAccountAndProfileImage() throws RetroHttpException {
        final RuntimeException throwable = mock(RuntimeException.class);
        when(userAPI.getAccount(PROFILE_USERNAME)).thenThrow(throwable);
        createPresenter();
        verify(view).showError(throwable);
    }

    private void setAuthenticatedUsername(@NonNull String username) {
        final ProfileModel profileModel = new ProfileModel();
        profileModel.username = username;
        when(userPrefs.getProfile()).thenReturn(profileModel);
    }

    private Account configureBareMockAccount() {
        final Account account = mock(Account.class);
        when(account.getUsername()).thenReturn(PROFILE_USERNAME);
        when(account.getProfileImage()).thenReturn(mock(ProfileImage.class));
        try {
            when(userAPI.getAccount(PROFILE_USERNAME)).thenReturn(account);
        } catch (RetroHttpException e) {
            throw new RuntimeException(e);
        }
        return account;
    }
}
