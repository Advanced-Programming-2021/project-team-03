package model.user;

public class UserException extends Exception {
    public final String errorMessage;

    UserException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() { // This is an standard format automatically generated by IntelliJ
        return "UserException{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
