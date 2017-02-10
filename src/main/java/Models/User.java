package Models;

import java.util.List;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
public class User {
    //Object information
    private String _id;
    private String _rev;
    private String type;

    //User information
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;

    //Account activity
    private int dateCreated;
    private int lastLogin;

    //List string because these are collections of id
    private List<String> toStations;
    private List<String> fromStations;
    private List<String> likedRoutes;
    private List<String> routesHistory;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(int dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(int lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<String> getToStations() {
        return toStations;
    }

    public void setToStations(List<String> toStations) {
        this.toStations = toStations;
    }

    public List<String> getFromStations() {
        return fromStations;
    }

    public void setFromStations(List<String> fromStations) {
        this.fromStations = fromStations;
    }

    public List<String> getLikedRoutes() {
        return likedRoutes;
    }

    public void setLikedRoutes(List<String> likedRoutes) {
        this.likedRoutes = likedRoutes;
    }

    public List<String> getRoutesHistory() {
        return routesHistory;
    }

    public void setRoutesHistory(List<String> routesHistory) {
        this.routesHistory = routesHistory;
    }
}
