package edu.byu.cs.tweeter.presenter;

import edu.byu.cs.tweeter.model.services.StatusService;
import edu.byu.cs.tweeter.net.request.StatusRequest;
import edu.byu.cs.tweeter.net.response.MessageResponse;

public class MainPresenter extends Presenter {

    private final View view;

    /**
     * The interface by which this presenter communicates with it's view.
     */
    public interface View {
        // If needed, Specify methods here that will be called on the view in response to model updates
    }

    public MainPresenter(View view) {
        this.view = view;
    }

    public MessageResponse postStatus(StatusRequest request) {
        return StatusService.getInstance().postStatus(request);
    }
}
