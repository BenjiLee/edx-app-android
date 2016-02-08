package org.edx.mobile.user;

import org.edx.mobile.http.RetroHttpException;
import org.edx.mobile.model.api.EnrolledCoursesResponse;
import org.edx.mobile.model.api.LastAccessedSubsectionResponse;

import java.util.List;
import java.util.Map;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedOutput;


/*
 * API calls for data that is specific to the user.
 */
public interface UserService {
    @GET("/api/user/v1/accounts/{username}")
    Account getAccount(@Path("username") String username) throws RetroHttpException;

    @PATCH("/api/user/v1/accounts/{username}")
    Account updateAccount(@Path("username") String username, @Body Map<String, Object> fields) throws RetroHttpException;

    @POST("/api/user/v1/accounts/{username}/image")
    Response setProfileImage(@Path("username") String username, @Header("Content-Disposition") String contentDisposition, @Body TypedOutput file) throws RetroHttpException;

    @DELETE("/api/user/v1/accounts/{username}/image")
    Response deleteProfileImage(@Path("username") String username) throws RetroHttpException;

    @GET("/api/mobile/v0.5/users/{username}/course_enrollments")
    List<EnrolledCoursesResponse> getUserEnrolledCourses(@Path("username") String username) throws RetroHttpException;

    @GET("/api/mobile/v0.5/users/{username}/course_status_info/{courseId}")
    LastAccessedSubsectionResponse getLastAccessedSubsection(@Path("username") String username, @Path("courseId") String courseId) throws RetroHttpException;

    // TODO 415 UNSUPPORTED MEDIA TYPE, looks like we need the headers
    @PATCH("/api/mobile/v0.5/users/{username}/course_status_info/{courseId}")
    LastAccessedSubsectionResponse syncLastAccessedSubsection(
            @Path("username") String username,
            @Path("courseId") String courseId,
            @Body Map<String, String> body,
            @Header("Content-type") String contentType) throws RetroHttpException;
}
