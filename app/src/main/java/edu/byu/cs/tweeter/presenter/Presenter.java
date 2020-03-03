package edu.byu.cs.tweeter.presenter;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.services.LoginService;
import edu.byu.cs.tweeter.model.services.UserService;

public abstract class Presenter {

    public User getCurrentUser() {
        return LoginService.getInstance().getCurrentUser();
    }

    public User getSearchedUser() {
        return UserService.getInstance().getSearchedUser();
    }
}
