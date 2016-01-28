package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.*;

/**
 * Created by demory on 1/18/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Auth0UserProfile {

    String email;
    String user_id;
    AppMetadata app_metadata;

    public Auth0UserProfile() {
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
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
        Permission[] permissions;

        public DatatoolsInfo() {
        }

        public DatatoolsInfo(Project[] projects, Permission[] permissions) {
            this.projects = projects;
            this.permissions = permissions;
        }

        public void setProjects(Project[] projects) {
            this.projects = projects;
        }

        public void setPermissions(Permission[] permissions) {
            this.permissions = permissions;
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

    public List<String> getManagedFeeds(String projectID){
        List<String> feeds = new ArrayList<String>();
        for(Project project : app_metadata.datatools.projects) {
            if (project.project_id.equals(projectID)) {
                for(Permission permission : project.permissions) {
                    if(permission.type.equals("manage-feed")) {
                        for(String thisFeedID : permission.feeds) {
//                            if(thisFeedID.equals(feedID) || thisFeedID.equals("*"))
                                feeds.add(thisFeedID);
                        }
                    }
                }
            }
        }
        return feeds;
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

    public boolean canAdministerApplication() {
        if(app_metadata.datatools.permissions != null) {
            for(Permission permission : app_metadata.datatools.permissions) {
                if(permission.type.equals("administer-application")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canAdministerProject(String projectID) {
        for(Project project : app_metadata.datatools.projects) {
            if (project.project_id.equals(projectID)) {
                for(Permission permission : project.permissions) {
                    if(permission.type.equals("administer-project")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canViewFeed(String projectID, String feedID) {
        for(Project project : app_metadata.datatools.projects) {
            if (project.project_id.equals(projectID)) {
                for(Permission permission : project.permissions) {
                    if(permission.type.equals("view-feed")) {
                        for(String thisFeedID : permission.feeds) {
                            if(thisFeedID.equals(feedID) || thisFeedID.equals("*")) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean canManageFeed(String projectID, String feedID) {
        for(Project project : app_metadata.datatools.projects) {
            if (project.project_id.equals(projectID)) {
                for(Permission permission : project.permissions) {
                    if(permission.type.equals("manage-feed")) {
                        for(String thisFeedID : permission.feeds) {
                            if(thisFeedID.equals(feedID) || thisFeedID.equals("*")) return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
