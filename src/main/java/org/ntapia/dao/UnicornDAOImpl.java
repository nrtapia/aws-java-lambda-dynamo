package org.ntapia.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import org.ntapia.model.Gender;
import org.ntapia.model.Ride;
import org.ntapia.model.Unicorn;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UnicornDAOImpl implements UnicornDAO {

    private static final AmazonDynamoDB DYNAMO_DB;
    private static final List<Unicorn> FLEET;

    static {
        DYNAMO_DB = AmazonDynamoDBClientBuilder.defaultClient();

        FLEET = List.of(
                new Unicorn("Bucephalus", "Golden", Gender.MALE),
                new Unicorn("Shadowfax", "White", Gender.MALE),
                new Unicorn("Rocinante", "Yellow", Gender.FEMALE)
        );
    }

    public UnicornDAOImpl() {
    }

    public Unicorn getRandom() {
        Random rand = new Random();
        return FLEET.get(rand.nextInt(FLEET.size()));
    }

    public void save(Ride ride) {
        PutItemRequest putItemRequest = new PutItemRequest();
        putItemRequest.setTableName("Rides");

        final Unicorn unicorn = ride.getUnicorn();
        Map<String, AttributeValue> mapUnicorn = Map.of(
                "Color", new AttributeValue().withS(unicorn.getColor()),
                "Gender", new AttributeValue().withS(unicorn.getGender().toString()),
                "Name", new AttributeValue().withS(unicorn.getName())
        );

        Map<String, AttributeValue> mapValues = Map.of(
                "RideId", new AttributeValue().withS(ride.getId()),
                "User", new AttributeValue().withS(ride.getRider()),
                "Unicorn", new AttributeValue().withM(mapUnicorn),
                "UnicornName", new AttributeValue().withS(unicorn.getName()),
                "RequestTime", new AttributeValue().withS(getDateISO())
        );
        putItemRequest.setItem(mapValues);

        DYNAMO_DB.putItem(putItemRequest);
    }

    private String getDateISO() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }
}
