package org.edx.mobile.test;

import android.support.annotation.NonNull;

import org.edx.mobile.CustomRobolectricTestRunner;
import org.edx.mobile.util.observer.AsyncObservable;
import org.edx.mobile.util.observer.MainLooperObservable;
import org.edx.mobile.view.Presenter;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.mock;

@RunWith(CustomRobolectricTestRunner.class)
public abstract class PresenterTest<P extends Presenter<V>, V> {
    protected P presenter;
    protected V view;

    @Before
    public final void setup() {
        MockitoAnnotations.initMocks(this);
        MainLooperObservable.EXECUTOR = new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                command.run();
            }
        };
        AsyncObservable.EXECUTOR = new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                command.run();
            }
        };
    }

    protected final void startPresenter(@NonNull P p) {
        presenter = p;
        view = mock(getViewType());
        presenter.attachView(view);
    }

    @After
    public final void after() {
        if (null != view && null != presenter) {
            presenter.detachView();
            presenter.destroy();
        }
    }

    @SuppressWarnings("unchecked")
    private Class<V> getViewType() {
        return (Class<V>) GenericSuperclassUtils.getTypeArguments(getClass(), PresenterTest.class)[1];
    }
}
