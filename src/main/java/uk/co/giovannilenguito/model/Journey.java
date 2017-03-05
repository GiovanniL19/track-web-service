package uk.co.giovannilenguito.model;

/**
 * Created by giovannilenguito on 28/02/2017.
 */
public class Journey {
    private String _id;
    private String _rev;

    private String type;
    private String city;
    private String longitude;
    private String latitude;
    private int hour;
    private int count;

    private String toCRS;
    private String fromCRS;
    private String user;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getToCRS() {
        return toCRS;
    }

    public void setToCRS(String toCRS) {
        this.toCRS = toCRS;
    }

    public String getFromCRS() {
        return fromCRS;
    }

    public void setFromCRS(String fromCRS) {
        this.fromCRS = fromCRS;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
