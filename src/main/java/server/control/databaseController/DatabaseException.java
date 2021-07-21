package server.control.databaseController;

public class DatabaseException extends Exception {
    public final String errorMessage;

    public DatabaseException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "DatabaseException{\n" +
                "errorMessage='" + errorMessage + "'}";
    }
}
