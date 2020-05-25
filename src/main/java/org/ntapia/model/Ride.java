package org.ntapia.model;

public class Ride {

    private String id;
    private Unicorn unicorn;
    private String eta;
    private String rider;

    public Ride(String id, Unicorn unicorn, String eta, String rider) {
        this.id = id;
        this.unicorn = unicorn;
        this.eta = eta;
        this.rider = rider;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Unicorn getUnicorn() {
        return unicorn;
    }

    public void setUnicorn(Unicorn unicorn) {
        this.unicorn = unicorn;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getRider() {
        return rider;
    }

    public void setRider(String rider) {
        this.rider = rider;
    }
}
