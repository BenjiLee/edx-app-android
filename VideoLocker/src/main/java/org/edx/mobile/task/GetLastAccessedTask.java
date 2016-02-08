package org.edx.mobile.task;

import android.content.Context;

import com.google.inject.Inject;

import org.edx.mobile.http.RetroHttpException;
import org.edx.mobile.model.api.LastAccessedSubsectionResponse;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.services.ServiceManager;
import org.edx.mobile.user.UserAPI;

public abstract class GetLastAccessedTask extends Task<LastAccessedSubsectionResponse> {

    String courseId;

    @Inject
    private UserAPI userAPI;

    public GetLastAccessedTask(Context context,  String courseId) {
        super(context);
        this.courseId = courseId;
    }

    @Override
    public LastAccessedSubsectionResponse call() throws RetroHttpException {
        PrefManager pref = new PrefManager(context, PrefManager.Pref.LOGIN);
        String username = pref.getCurrentUserProfile().username;

        try {
            if(courseId!=null){
                LastAccessedSubsectionResponse res = userAPI.getLastAccessedSubsection(username, courseId);
                return res;
            }
        } catch (RetroHttpException ex) {
            handle(ex);
            logger.error(ex, true);
            if (ex.getStatusCode() == 401) {
                environment.getRouter().forceLogout(
                        getContext(),
                        environment.getSegment(),
                        environment.getNotificationDelegate());
            }        }
        return null;
    }
}
