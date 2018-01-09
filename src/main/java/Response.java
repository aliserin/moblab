public class Response {
    private boolean error;
    private boolean found;
    private String message;
    private Opinion[] results;

    public boolean isError() {
        return error;
    }

    public boolean isFound() {
        return found;
    }

    public String getMessage() {
        return message;
    }

    public Opinion[] getResults() {
        return results;
    }
}