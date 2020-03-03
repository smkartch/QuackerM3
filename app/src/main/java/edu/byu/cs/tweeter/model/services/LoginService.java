package edu.byu.cs.tweeter.model.services;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.net.ServerFacade;
import edu.byu.cs.tweeter.net.request.LoginRequest;
import edu.byu.cs.tweeter.net.response.RegisterLoginResponse;

public class LoginService {

    private static LoginService instance;

    private User currentUser;

    private final ServerFacade serverFacade;

    public static LoginService getInstance() {
        if(instance == null) {
            instance = new LoginService();
        }

        return instance;
    }

    private LoginService() {
        serverFacade = new ServerFacade();
        // TODO: Remove when the actual login functionality exists.
        currentUser = new User("Test", "User",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        setCurrentUser(currentUser);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public RegisterLoginResponse postLogin(LoginRequest request) {
        return serverFacade.postLogin(request);
    }
}
