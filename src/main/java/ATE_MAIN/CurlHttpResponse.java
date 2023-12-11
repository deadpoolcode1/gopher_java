package ATE_MAIN;

public class CurlHttpResponse {
    private final int responseCode;
    private final String responseBody;

    public CurlHttpResponse(int responseCode, String responseBody) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}

