package org.ntapia;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.ntapia.dao.UnicornDAO;
import org.ntapia.dao.UnicornDAOImpl;
import org.ntapia.mapper.RideMapper;
import org.ntapia.model.Location;
import org.ntapia.model.Ride;
import org.ntapia.model.Unicorn;

import java.util.*;

public final class RequestUnicorn implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public static final Map<String, String> CROSS_ORIGIN_HEADER = Collections.singletonMap("Access-Control-Allow-Origin", "*");
    public static final int STATUS_CODE_500 = 500;
    public static final String FIELD_ERROR = "Error";
    public static final String FIELD_REFERENCE = "Reference";
    public static final String CLAIM_USERNAME = "cognito:username";
    public static final String AUTHORIZER_CLAIMS = "claims";
    public static final String NODE_PICKUP_LOCATION = "PickupLocation";
    public static final String NODE_LATITUDE = "Latitude";
    public static final String NODE_LONGITUDE = "Longitude";

    private final UnicornDAO unicornDAO = new UnicornDAOImpl();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        final LambdaLogger logger = context.getLogger();
        logger.log("Event: " + event);

        if (Objects.isNull(event.getRequestContext().getAuthorizer())) {
            return buildErrorResponse("Authorization not configured", context.getAwsRequestId());
        }

        final String id = UUID.randomUUID().toString();
        final String username = extractUsername(event);
        final Location location;

        try {
            location = extractLocation(event);
        } catch (ParseException e) {
            logger.log(String.format("ERROR: Parse request: %s to JSON, : %s", event.getBody(), e.getMessage()));
            return buildErrorResponse("Error to parse request", context.getAwsRequestId());
        }

        Ride ride = process(id, username, location);

        return buildSuccessResponse(ride);
    }

    private APIGatewayProxyResponseEvent buildSuccessResponse(Ride ride) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setStatusCode(201);
        responseEvent.setHeaders(CROSS_ORIGIN_HEADER);
        responseEvent.setBody(RideMapper.toJson(ride));

        return responseEvent;
    }

    private Ride process(String id, String username, Location location) {
        final Unicorn unicorn = unicornDAO.getRandom();
        final Ride ride = new Ride(id, unicorn, "60", username);
        this.unicornDAO.save(ride);
        return ride;
    }

    private Location extractLocation(APIGatewayProxyRequestEvent event) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(event.getBody());
        JSONObject pickupLocation = (JSONObject) json.get(NODE_PICKUP_LOCATION);
        return new Location((Double) pickupLocation.get(NODE_LATITUDE), (Double) pickupLocation.get(NODE_LONGITUDE));
    }

    private String extractUsername(APIGatewayProxyRequestEvent event) {
        Map<String, String> claims = (Map<String, String>) event.getRequestContext().getAuthorizer().get(AUTHORIZER_CLAIMS);
        return claims.get(CLAIM_USERNAME);
    }

    private APIGatewayProxyResponseEvent buildErrorResponse(String message, String reference) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(STATUS_CODE_500);
        response.setHeaders(CROSS_ORIGIN_HEADER);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put(FIELD_ERROR, message);
        responseBody.put(FIELD_REFERENCE, reference);
        response.setBody(new JSONObject(responseBody).toJSONString());

        return response;
    }


}
