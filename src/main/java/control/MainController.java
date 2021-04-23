package control;

import org.json.JSONObject;


public class MainController { // this class is responsible for view request and send the feedback to thee view via a Json string

    private static MainController mainControllerInstance;

    private MainController(){
    }

    public static MainController getInstance() {
        if (mainControllerInstance == null)
            mainControllerInstance = new MainController();
        return mainControllerInstance;
    }

    public String getRequest(String input){ //this method receives a input string and return a string as an answer
        /* note that this strings are in Json format */
        // TODO parsing analysing and answering the request of view menu
        JSONObject inputObject = new JSONObject(input);
        String requestType = inputObject.getString("requestType");

        return null;
    }
}
