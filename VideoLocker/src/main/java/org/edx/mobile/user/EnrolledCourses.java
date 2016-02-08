package org.edx.mobile.user;


import org.edx.mobile.interfaces.SectionItemInterface;

@SuppressWarnings("serial")
public class EnrolledCourses implements SectionItemInterface {

    private String created;
    private String mode;
    private String user;
    private boolean is_active;
    private EnrolledCourseDetail course_details;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean is_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public EnrolledCourseDetail getCourse_details() {
        return course_details;
    }

    public void setCourse_details(EnrolledCourseDetail course_details) {
        this.course_details = course_details;
    }

    @Override
    public boolean isChapter() {
        return false;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    @Override
    public boolean isCourse() {
        return true;
    }

    @Override
    public boolean isVideo() {
        return false;
    }

    @Override
    public boolean isDownload() {
        return false;
    }

}
