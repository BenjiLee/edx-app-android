package org.edx.mobile.view;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.edx.mobile.base.BaseFragmentActivity;

public abstract class PresenterActivity<P extends Presenter<V>, V> extends BaseFragmentActivity {

    protected P presenter;

    @NonNull
    abstract protected P createPresenter();

    @NonNull
    abstract protected V createView();

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == presenter) {
            presenter = getLastCustomNonConfigurationInstance();
            if (null == presenter) {
                presenter = createPresenter();
            }
        }
    }

    @Override
    @CallSuper
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        presenter.attachView(createView());
    }

    @Override
    public final P getLastCustomNonConfigurationInstance() {
        return (P) super.getLastCustomNonConfigurationInstance();
    }

    @Override
    public final P onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        if (!isChangingConfigurations()) {
            presenter.destroy();
        }
    }
}
