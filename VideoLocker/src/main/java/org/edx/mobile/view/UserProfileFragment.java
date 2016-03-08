package org.edx.mobile.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.databinding.FragmentUserProfileBinding;
import org.edx.mobile.logger.Logger;
import org.edx.mobile.module.analytics.ISegment;
import org.edx.mobile.module.prefs.UserPrefs;
import org.edx.mobile.profiles.UserProfileActivity;
import org.edx.mobile.profiles.UserProfileInteractor;
import org.edx.mobile.profiles.UserProfilePresenter;
import org.edx.mobile.user.UserAPI;
import org.edx.mobile.util.images.ErrorUtils;

import de.greenrobot.event.EventBus;
import roboguice.RoboGuice;

public class UserProfileFragment extends PresenterFragment<UserProfilePresenter, UserProfilePresenter.ViewInterface> {

    public static UserProfileFragment newInstance(@NonNull String username) {
        final Bundle bundle = new Bundle();
        bundle.putString(UserProfileActivity.EXTRA_USERNAME, username);
        final UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Inject
    private Router router;

    protected final Logger logger = new Logger(getClass().getName());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false).getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile: {
                presenter.onEditProfile();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @NonNull
    @Override
    protected UserProfilePresenter createPresenter() {
        final Injector injector = RoboGuice.getInjector(getActivity());
        return new UserProfilePresenter(
                getArguments().getString(UserProfileActivity.EXTRA_USERNAME),
                injector.getInstance(UserPrefs.class),
                injector.getInstance(ISegment.class),
                injector.getInstance(EventBus.class),
                injector.getInstance(UserAPI.class));
    }

    @NonNull
    @Override
    protected UserProfilePresenter.ViewInterface createView() {
        final FragmentUserProfileBinding viewHolder = DataBindingUtil.getBinding(getView());
        {
            final View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onEditProfile();
                }
            };
            viewHolder.parentalConsentEditProfileButton.setOnClickListener(listener);
            viewHolder.incompleteEditProfileButton.setOnClickListener(listener);
        }

        return new UserProfilePresenter.ViewInterface() {
            @Override
            public void setEditProfileMenuButtonVisible(boolean visible) {
                setHasOptionsMenu(visible);
            }

            @Override
            public void setProfile(@NonNull UserProfileViewModel profile) {
                if (profile.limitedProfileMessage == LimitedProfileMessage.NONE) {
                    viewHolder.sharingLimited.setVisibility(View.GONE);
                } else {
                    viewHolder.sharingLimited.setVisibility(View.VISIBLE);
                    viewHolder.sharingLimited.setText(profile.limitedProfileMessage == LimitedProfileMessage.OWN_PROFILE
                            ? R.string.profile_sharing_limited_by_you
                            : R.string.profile_sharing_limited_by_other_user);
                }

                if (TextUtils.isEmpty(profile.language)) {
                    viewHolder.languageContainer.setVisibility(View.GONE);
                } else {
                    viewHolder.languageText.setText(profile.language);
                    viewHolder.languageContainer.setVisibility(View.VISIBLE);
                }

                if (TextUtils.isEmpty(profile.location)) {
                    viewHolder.locationContainer.setVisibility(View.GONE);
                } else {
                    viewHolder.locationText.setText(profile.location);
                    viewHolder.locationContainer.setVisibility(View.VISIBLE);
                }

                viewHolder.profileBodyContent.setBackgroundColor(getResources().getColor(profile.contentType == ContentType.ABOUT_ME ? R.color.white : R.color.edx_grayscale_neutral_xx_light));
                viewHolder.parentalConsentRequired.setVisibility(profile.contentType == ContentType.PARENTAL_CONSENT_REQUIRED ? View.VISIBLE : View.GONE);
                viewHolder.incompleteContainer.setVisibility(profile.contentType == ContentType.INCOMPLETE ? View.VISIBLE : View.GONE);
                viewHolder.noAboutMe.setVisibility(profile.contentType == ContentType.NO_ABOUT_ME ? View.VISIBLE : View.GONE);
                viewHolder.bioText.setVisibility(profile.contentType == ContentType.ABOUT_ME ? View.VISIBLE : View.GONE);
                viewHolder.loadingIndicator.setVisibility(profile.contentType == ContentType.LOADING ? View.VISIBLE : View.GONE);
                viewHolder.bioText.setText(profile.bio);
            }

            @Override
            public void showError(@NonNull Throwable error) {
                viewHolder.loadingIndicator.setVisibility(View.GONE);
                ((BaseFragmentActivity) getActivity()).showErrorMessage("", ErrorUtils.getErrorMessage(error, getContext()));
            }


            @Override
            public void setPhotoUri(@NonNull UserProfileInteractor.ImageResource model) {
                if (null == model.uri) {
                    Glide.with(UserProfileFragment.this)
                            .load(R.drawable.xsie)
                            .into(viewHolder.profileImage);

                } else if (model.shouldReadFromCache) {
                    Glide.with(UserProfileFragment.this)
                            .load(model.uri)
                            .into(viewHolder.profileImage);
                } else {
                    Glide.with(UserProfileFragment.this)
                            .load(model.uri)
                            .skipMemoryCache(true) // URI is re-used in subsequent events; disable caching
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(viewHolder.profileImage);
                }
            }

            @Override
            public void setName(@NonNull String name) {
                viewHolder.nameText.setText(name);
            }

            @Override
            public void navigateToProfileEditor(@NonNull String username) {
                router.showUserProfileEditor(getActivity(), username);
            }
        };
    }
}
