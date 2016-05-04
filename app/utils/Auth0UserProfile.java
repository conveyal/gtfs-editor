package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.security.Permissions;
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
        Subscription[] subscriptions;

        public DatatoolsInfo() {
        }

        public DatatoolsInfo(Project[] projects, Permission[] permissions) {
            this.projects = projects;
            this.permissions = permissions;
            this.subscriptions = subscriptions;
        }

        public void setProjects(Project[] projects) {
            this.projects = projects;
        }

        public void setPermissions(Permission[] permissions) {
            this.permissions = permissions;
        }

        public void setSubscriptions(Subscription[] subscriptions) {
            this.subscriptions = subscriptions;
        }

    }

    public static class Project {

        String project_id;
        Permission[] permissions;
        String[] defaultFeeds;

        public Project() {
        }

        public Project(String project_id, Permission[] permissions, String[] defaultFeeds) {
            this.project_id = project_id;
            this.permissions = permissions;
            this.defaultFeeds = defaultFeeds;
        }

        public void setProject_id(String project_id) {
            this.project_id = project_id;
        }

        public void setPermissions(Permission[] permissions) { this.permissions = permissions; }

        public void setDefaultFeeds(String[] defaultFeeds) {
            this.defaultFeeds = defaultFeeds;
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

    public static class Subscription {

        String type;
        String[] target;

        public Subscription() {
        }

        public Subscription(String type, String[] target) {
            this.type = type;
            this.target = target;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setTarget(String[] target) {
            this.target = target;
        }
    }

    private String[] getDefaultFeeds(String projectID) {
        if(app_metadata.datatools.projects == null) return null;
        for(Project project : app_metadata.datatools.projects) {
            if (project.project_id.equals(projectID)) {
                if(project.defaultFeeds != null) return project.defaultFeeds;
            }
        }
        return new String[0];
    }

    public List<String> getManagedFeeds(String projectID){
        List<String> feeds = new ArrayList<String>();
        for(Project project : app_metadata.datatools.projects) {
            if (project.project_id.equals(projectID)) {
                for(Permission permission : project.permissions) {
                    if(permission.type.equals("manage-feed")) {
                        String[] feedArr = new String[0];
                        if(permission.feeds != null) feedArr = permission.feeds;
                        else feedArr = getDefaultFeeds(projectID);

                        for(String thisFeedID : feedArr) {
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
        if(app_metadata.datatools.projects == null) return false;
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
        if(canAdministerApplication()) return true;
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

    public String[] getFeedsForPermission(String projectID, String permissionType) {
        if(canAdministerProject(projectID)) {
            String[] all = { "*" };
            return all;
        }
        for(Project project : app_metadata.datatools.projects) {
            for(Permission permission : project.permissions) {
                if(permission.type.equals(permissionType)) {
                    return permission.feeds;
                }
            }
        }
        return new String[0];
    }

    public String[] getEditableFeeds(String projectID) {
        return getFeedsForPermission(projectID, "edit-gtfs");
    }

    public String[] getManageableFeeds(String projectID) {
        return getFeedsForPermission(projectID, "manage-feed");
    }

}
