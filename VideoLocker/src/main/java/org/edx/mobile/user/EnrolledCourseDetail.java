package org.edx.mobile.user;

public class EnrolledCourseDetail {

    private String course_id;
    private String enrollment_start;
    private String enrollment_end;
    private String course_start;
    private String course_end;
    private Boolean invite_only;
    private EnrolledCoursesCourseMode course_modes;

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getEnrollment_start() {
        return enrollment_start;
    }

    public void setEnrollment_start(String enrollment_start) {
        this.enrollment_start = enrollment_start;
    }

    public String getEnrollment_end() {
        return enrollment_end;
    }

    public void setEnrollment_end(String enrollment_end) {
        this.enrollment_end = enrollment_end;
    }

    public String getCourse_start() {
        return course_start;
    }

    public void setCourse_start(String course_start) {
        this.course_start = course_start;
    }

    public String getCourse_end() {
        return course_end;
    }

    public void setCourse_end(String course_end) {
        this.course_end = course_end;
    }

    public Boolean getInvite_only() {
        return invite_only;
    }

    public void setInvite_only(Boolean invite_only) {
        this.invite_only = invite_only;
    }

    public EnrolledCoursesCourseMode getCourse_modes() {
        return course_modes;
    }

    public void setCourse_modes(EnrolledCoursesCourseMode course_modes) {
        this.course_modes = course_modes;
    }
}
