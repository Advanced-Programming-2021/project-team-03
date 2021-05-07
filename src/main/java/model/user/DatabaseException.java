package model.user;

public class DatabaseException extends Exception {
    public final String errorMessage;

    DatabaseException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "DatabaseException{\n" +
                "errorMessage='" + errorMessage + "'}";
    }
}
