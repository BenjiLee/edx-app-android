package org.edx.mobile.view;

import android.databinding.DataBindingUtil;
import android.view.View;

import org.edx.mobile.R;
import org.edx.mobile.databinding.FragmentUserProfileBinding;
import org.edx.mobile.profiles.UserProfilePresenter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

public class UserProfileFragmentTest extends PresenterFragmentTest<UserProfileFragment, UserProfilePresenter, UserProfilePresenter.ViewInterface> {

    FragmentUserProfileBinding binding;

    @Before
    public void before() {
        startFragment(UserProfileFragment.newInstance("not used"));
        binding = DataBindingUtil.getBinding(fragment.getView());
        assertThat(binding, is(notNullValue()));
    }

    @Test
    public void setUsername_withNonEmptyString_updatesTextView() {
        final String text = "hello";
        view.setName(text);
        assertThat(binding.usernameText.getText().toString(), is(text));
    }

    @Test
    public void setEditProfileMenuButtonVisible_withTrue_showsEditProfileOption() {
        view.setEditProfileMenuButtonVisible(true);
        assertThat(fragment.getActivity().findViewById(R.id.edit_profile), is(notNullValue()));
    }

    @Test
    public void setEditProfileMenuButtonVisible_withFalse_hidesEditProfileOption() {
        view.setEditProfileMenuButtonVisible(false);
        assertThat(fragment.getActivity().findViewById(R.id.edit_profile), is(nullValue()));
    }

    @Test
    public void showLoadingIndicator_showsLoadingIndicatorAndHidesContent() {
        view.setContent(UserProfilePresenter.ViewInterface.ContentType.LOADING);
        assertThat(binding.loadingIndicator.getVisibility(), is(View.VISIBLE));
    }

    @Test
    public void click_onEditProfileOption_callsEditProfile() {
        view.setEditProfileMenuButtonVisible(true);
        shadowOf(fragment.getActivity()).clickMenuItem(R.id.edit_profile);
        verify(presenter).onEditProfile();
    }

    @Test
    public void click_onParentalConsentEditProfileButton_callsEditProfile() {
        view.setEditProfileMenuButtonVisible(true);
        binding.parentalConsentEditProfileButton.performClick();
        verify(presenter).onEditProfile();
    }

    @Test
    public void click_onIncompleteEditProfileButton_callsEditProfile() {
        view.setEditProfileMenuButtonVisible(true);
        binding.incompleteEditProfileButton.performClick();
        verify(presenter).onEditProfile();
    }
}
