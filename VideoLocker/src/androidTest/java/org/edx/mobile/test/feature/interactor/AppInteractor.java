package org.edx.mobile.test.feature.interactor;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;

import org.edx.mobile.base.MainApplication;
import org.edx.mobile.module.prefs.PrefManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

public class AppInteractor {
    public LandingScreenInteractor launchApp() {
        // Start launch activity
        final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        final Intent launchIntent = instrumentation.getTargetContext().getPackageManager()
                .getLaunchIntentForPackage(instrumentation.getTargetContext().getPackageName())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final MainApplication application = MainApplication.instance();
        PrefManager pref = new PrefManager(application, PrefManager.Pref.LOGIN);

        instrumentation.startActivitySync(launchIntent);
        instrumentation.waitForIdleSync();
        onView(isRoot()).check(matches(isDisplayed()));

        return new LandingScreenInteractor();
    }
}
