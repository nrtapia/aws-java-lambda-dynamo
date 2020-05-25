package org.ntapia.mapper;

import org.json.simple.JSONObject;
import org.ntapia.model.Ride;
import org.ntapia.model.Unicorn;

public final class RideMapper {

    private RideMapper() {
    }

    public static String toJson(Ride ride) {
        Unicorn unicorn = ride.getUnicorn();

        JSONObject unicornObject = new JSONObject();
        unicornObject.put("Color", unicorn.getColor());
        unicornObject.put("Gender", unicorn.getGender().toString());
        unicornObject.put("Name", unicorn.getName());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RideId", ride.getId());
        jsonObject.put("Unicorn", unicornObject);
        jsonObject.put("UnicornName", unicorn.getName());
        jsonObject.put("Eta", ride.getEta());
        jsonObject.put("Rider", ride.getRider());

        return jsonObject.toJSONString();
    }
}