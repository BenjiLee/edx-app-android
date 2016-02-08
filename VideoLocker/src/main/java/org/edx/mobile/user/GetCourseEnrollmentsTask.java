package org.edx.mobile.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import org.edx.mobile.model.api.EnrolledCoursesResponse;
import org.edx.mobile.task.Task;

import java.util.List;

public abstract class GetCourseEnrollmentsTask extends
        Task<List<EnrolledCoursesResponse>> {

    @Inject
    private UserAPI userAPI;

    public GetCourseEnrollmentsTask(@NonNull Context context) {
        super(context);
    }

    public List<EnrolledCoursesResponse> call() throws Exception {
        return userAPI.getUserEnrolledCourses();
    }
}
