package org.edx.mobile.task;

import android.content.Context;

import com.google.inject.Inject;

import org.edx.mobile.model.api.LastAccessedSubsectionResponse;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.services.ServiceManager;
import org.edx.mobile.user.UserAPI;

public abstract class SyncLastAccessedTask extends Task<LastAccessedSubsectionResponse> {

    String courseId;
    String lastVisitedModuleId;

    @Inject
    private UserAPI userAPI;

    public SyncLastAccessedTask(Context context, String courseId, String lastVisitedModuleId) {
        super(context);
        this.courseId = courseId;
        this.lastVisitedModuleId = lastVisitedModuleId;
    }

    @Override
    public LastAccessedSubsectionResponse call() throws Exception {
        PrefManager pref = new PrefManager(context, PrefManager.Pref.LOGIN);
        String username = pref.getCurrentUserProfile().username;

        try {
            if(courseId!=null && lastVisitedModuleId !=null) {

            // TODO When UserAPI.syncLastAccessedSubsection is fixed, replace with this snippet
//            LastAccessedSubsectionResponse res = userAPI.syncLastAccessedSubsection(
//                    username,
//                    courseId,
//                    lastVisitedModuleId);

                ServiceManager api = environment.getServiceManager();
                LastAccessedSubsectionResponse res = api.syncLastAccessedSubsection(
                        courseId, lastVisitedModuleId);
                return res;
            }
        } catch (Exception ex) {
            handle(ex);
            logger.error(ex, true);
        }
        return null;
    }
}
