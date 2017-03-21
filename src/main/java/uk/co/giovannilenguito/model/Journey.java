package uk.co.giovannilenguito.model;

/**
 * Created by giovannilenguito on 28/02/2017.
 */
public class Journey {
    private String id;
    private String _id;
    private String _rev;

    private String type;
    private String city;
    private String longitude;
    private String latitude;
    private int hour;
    private String day;
    private int count;

    private String toName;
    private String fromName;
    private String toCRS;
    private String fromCRS;
    private String user;

    private Station to;
    private Station from;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
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

    public Station getTo() {
        return to;
    }

    public void setTo(Station to) {
        this.to = to;
    }

    public Station getFrom() {
        return from;
    }

    public void setFrom(Station from) {
        this.from = from;
    }
}
