package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by landon on 1/26/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Auth0UserProfile {

    String email;
    AppMetadata app_metadata;

    public Auth0UserProfile() {
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setApp_metadata(AppMetadata app_metadata) {
        this.app_metadata = app_metadata;
    }


    public static class AppMetadata {

        DatatoolsInfo datatools;

        public AppMetadata() {
        }

        public void setDatatools(DatatoolsInfo datatools) {
            this.datatools = datatools;
        }
    }

    public static class DatatoolsInfo {

        Project[] projects;

        public DatatoolsInfo() {
        }

        public DatatoolsInfo(Project[] projects) {
            this.projects = projects;
        }

        public void setProjects(Project[] projects) {
            this.projects = projects;
        }
    }


    public static class Project {

        String project_id;
        Permission[] permissions;

        public Project() {
        }

        public Project(String project_id, Permission[] permissions) {
            this.project_id = project_id;
            this.permissions = permissions;
        }

        public void setProject_id(String project_id) {
            this.project_id = project_id;
        }

        public void setPermissions(Permission[] permissions) {
            this.permissions = permissions;
        }
    }

    public static class Permission {

        String type;
        String[] feeds;

        public Permission() {
        }

        public Permission(String type, String[] feeds) {
            this.type = type;
            this.feeds = feeds;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setFeeds(String[] feeds) {
            this.feeds = feeds;
        }
    }

    public int getProjectCount() {
        return app_metadata.datatools.projects.length;
    }

    public boolean hasProject(String projectID) {
        for(Project project : app_metadata.datatools.projects) {
            if (project.project_id.equals(projectID)) return true;
        }
        return false;
    }
}

