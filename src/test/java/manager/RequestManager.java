package manager;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RequestManager {

    private final RequestSpecification baseRequestSpecification;

    public RequestManager(final RequestSpecification baseRequestSpecification) {
        this.baseRequestSpecification = baseRequestSpecification;
    }

    public Response getRequest(final String endpoint) {
        return this.baseRequestSpecification
                .when()
                .get(endpoint);
    }

    public Response postRequest(final String endpoint, final Object body) {
        return this.baseRequestSpecification
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(endpoint);
    }
}