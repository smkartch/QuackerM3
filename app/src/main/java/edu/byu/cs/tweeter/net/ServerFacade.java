package edu.byu.cs.tweeter.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.net.request.FeedRequest;
import edu.byu.cs.tweeter.net.request.FollowRequest;
import edu.byu.cs.tweeter.net.request.FollowersRequest;
import edu.byu.cs.tweeter.net.request.FollowingRequest;
import edu.byu.cs.tweeter.net.request.IsFollowingRequest;
import edu.byu.cs.tweeter.net.request.LoginRequest;
import edu.byu.cs.tweeter.net.request.LogoutRequest;
import edu.byu.cs.tweeter.net.request.RegisterRequest;
import edu.byu.cs.tweeter.net.request.StatusRequest;
import edu.byu.cs.tweeter.net.request.StoryRequest;
import edu.byu.cs.tweeter.net.request.UserRequest;
import edu.byu.cs.tweeter.net.response.FeedResponse;
import edu.byu.cs.tweeter.net.response.FollowersResponse;
import edu.byu.cs.tweeter.net.response.FollowingResponse;
import edu.byu.cs.tweeter.net.response.IsFollowingResponse;
import edu.byu.cs.tweeter.net.response.RegisterLoginResponse;
import edu.byu.cs.tweeter.net.response.MessageResponse;
import edu.byu.cs.tweeter.net.response.StoryResponse;
import edu.byu.cs.tweeter.net.response.UserResponse;

public class ServerFacade {

    private static Map<User, List<User>> followeesByFollower;
    private static Map<User, List<User>> followersByFollowee;

    public FollowingResponse getFollowees(FollowingRequest request) {

        assert request.getLimit() >= 0;
        assert request.getFollower() != null;

        if(followeesByFollower == null) {
            followeesByFollower = initializeFollowees();
        }

        List<User> allFollowees = followeesByFollower.get(request.getFollower());
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFolloweesStartingIndex(request.getLastFollowee(), allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
            }
        }

        return new FollowingResponse(responseFollowees, hasMorePages);
    }

    private int getFolloweesStartingIndex(User lastFollowee, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFollowee != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFollowee.equals(allFollowees.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                }
            }
        }

        return followeesIndex;
    }

    public IsFollowingResponse isFollowing(IsFollowingRequest request) {
        assert request.getCurrentUser() != null;
        assert request.getSearchedUser() != null;

        if(followeesByFollower == null) {
            followeesByFollower = initializeFollowees();
        }

        IsFollowingResponse response;
        if (followeesByFollower.get(request.getCurrentUser()) != null) {
            if (followeesByFollower.get(request.getCurrentUser()).contains(request.getSearchedUser())) {
                response = new IsFollowingResponse(true);
            } else {
                response = new IsFollowingResponse(false);
            }
        } else {
            response = new IsFollowingResponse(false);
        }

        return response;
    }

    /**
     * Generates the followee data.
     */
    private Map<User, List<User>> initializeFollowees() {

        Map<User, List<User>> followeesByFollower = new HashMap<>();

        List<Follow> follows = getFollowGenerator().generateUsersAndFollows(100,
                0, 50, FollowGenerator.Sort.FOLLOWER_FOLLOWEE);

        // Populate a map of followees, keyed by follower so we can easily handle followee requests
        for(Follow follow : follows) {
            List<User> followees = followeesByFollower.get(follow.getFollower());

            if(followees == null) {
                followees = new ArrayList<>();
                followeesByFollower.put(follow.getFollower(), followees);
            }

            followees.add(follow.getFollowee());
        }

        return followeesByFollower;
    }

    /**
     * Returns an instance of FollowGenerator that can be used to generate Follow data. This is
     * written as a separate method to allow mocking of the generator.
     *
     * @return the generator.
     */
    FollowGenerator getFollowGenerator() {
        return FollowGenerator.getInstance();
    }

    public FollowersResponse getFollowers(FollowersRequest request) {

        assert request.getLimit() >= 0;
        assert request.getFollowee() != null;

        if(followersByFollowee == null) {
            followersByFollowee = initializeFollowers();
        }

        List<User> allFollowers = followersByFollowee.get(request.getFollowee());
        List<User> responseFollowers = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowers != null) {
                int followersIndex = getFollowersStartingIndex(request.getLastFollower(), allFollowers);

                for(int limitCounter = 0; followersIndex < allFollowers.size() && limitCounter < request.getLimit(); followersIndex++, limitCounter++) {
                    responseFollowers.add(allFollowers.get(followersIndex));
                }

                hasMorePages = followersIndex < allFollowers.size();
            }
        }

        return new FollowersResponse(responseFollowers, hasMorePages);
    }

    private int getFollowersStartingIndex(User lastFollower, List<User> allFollowers) {

        int followersIndex = 0;

        if(lastFollower != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollower.equals(allFollowers.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                }
            }
        }

        return followersIndex;
    }

    /**
     * Generates the follower data.
     */
    private Map<User, List<User>> initializeFollowers() {

        Map<User, List<User>> followersByFollowee = new HashMap<>();

        List<Follow> follows = getFollowGenerator().generateUsersAndFollows(100,
                0, 50, FollowGenerator.Sort.FOLLOWEE_FOLLOWER);

        // Populate a map of followers, keyed by followee so we can easily handle follower requests
        for(Follow follow : follows) {
            List<User> followers = followersByFollowee.get(follow.getFollowee());

            if(followers == null) {
                followers = new ArrayList<>();
                followersByFollowee.put(follow.getFollowee(), followers);
            }

            followers.add(follow.getFollower());
        }

        return followersByFollowee;
    }

    public StoryResponse getStory(StoryRequest request) {

        assert request.getLimit() >= 0;
        assert request.getUser() != null;

        User user = request.getUser();

        if(user.getStory().getStatusList().size() == 0) {
            user.getStory().addStatuses(initializeStatuses(user));
        }

        List<Status> allStatuses = user.getStory().getStatusList();
        List<Status> responseStatusList = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                int statusIndex = getStatusesStartingIndex(request.getLastStatus(), allStatuses);

                for(int limitCounter = 0; statusIndex < allStatuses.size() && limitCounter < request.getLimit(); statusIndex++, limitCounter++) {
                    responseStatusList.add(allStatuses.get(statusIndex));
                }

                hasMorePages = statusIndex < allStatuses.size();
            }
        }

        return new StoryResponse(responseStatusList, hasMorePages);
    }

    public FeedResponse getFeed(FeedRequest request) {
        assert request.getLimit() >= 0;
        assert request.getUser() != null;

        User user = request.getUser();

        if(user.getFeed().getStatusList().size() == 0) {
            user.getFeed().addStatuses(initializeStatuses(user));
        }

        List<Status> allStatuses = user.getFeed().getStatusList();
        List<Status> responseStatusList = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                int statusIndex = getStatusesStartingIndex(request.getLastStatus(), allStatuses);

                for(int limitCounter = 0; statusIndex < allStatuses.size() && limitCounter < request.getLimit(); statusIndex++, limitCounter++) {
                    responseStatusList.add(allStatuses.get(statusIndex));
                }

                hasMorePages = statusIndex < allStatuses.size();
            }
        }

        return new FeedResponse(responseStatusList, hasMorePages);
    }

    private int getStatusesStartingIndex(Status lastStatus, List<Status> allStatuses) {

        int statusIndex = 0;

        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatus.equals(allStatuses.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusIndex = i + 1;
                }
            }
        }

        return statusIndex;
    }

    public List<Status> initializeStatuses(User user) {
        StatusGenerator statusGenerator = StatusGenerator.getInstance();
        return statusGenerator.generate20statuses(user);
    }

    public MessageResponse postStatus(StatusRequest request) {
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    byte[] b = baos.toByteArray();
//                    String encImage = Base64.encodeToString(b, Base64.URL_SAFE);
        //Encode bitmap and send to server
        Status status;
        if (request.getImageBitmap() != null) {
            status = new Status(request.getStatus(), request.getDate(),
                    request.getUser(), request.getImageBitmap());
        } else {
            status = new Status(request.getStatus(), request.getDate(),
                    request.getUser(), "");
        }
        request.getUser().addToStory(status);
        return new MessageResponse(true);
    }

    public RegisterLoginResponse postRegister(RegisterRequest request) {
//        assert request.getImageBitmap() != null;
//        assert request.getImageBitmap() != null;
        return new RegisterLoginResponse(true);
    }

    public RegisterLoginResponse postLogin(LoginRequest request) {
        return new RegisterLoginResponse(true);
    }

    public UserResponse getUserByHandle(UserRequest request) {
        User user = new User("different", "user", request.getHandle(),
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
        user.getStory().addStatuses(initializeStatuses(user));
        ArrayList<User> followers = new ArrayList<>();
        followers.add(new User("HardCoded", "User", "@User",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png"));

        if (followersByFollowee == null) {
            followersByFollowee = initializeFollowers();
        }
        if (followeesByFollower == null) {
            followeesByFollower = initializeFollowees();
        }
        followersByFollowee.put(user,followers);
        followeesByFollower.put(user, followers);

        return new UserResponse(user);
    }

    public MessageResponse postLogout(LogoutRequest request) {
        return new MessageResponse(true);
    }

    public MessageResponse addFollow(FollowRequest request) {
        assert request.getFollowee() != null;
        assert request.getFollower() != null;

        if (followersByFollowee == null) {
            followersByFollowee = initializeFollowers();
        }
        if (followeesByFollower == null) {
            followeesByFollower = initializeFollowees();
        }

        if (followersByFollowee.get(request.getFollowee()) == null && followeesByFollower.get(request.getFollower()) == null) {
            ArrayList<User> list = new ArrayList<>();
            list.add(request.getFollower());
            followersByFollowee.put(request.getFollowee(), list);

            ArrayList<User> list1 = new ArrayList<>();
            list1.add(request.getFollowee());
            followeesByFollower.put(request.getFollower(), list1);

            return new MessageResponse(true);
        } else if (followersByFollowee.get(request.getFollowee()) == null) {
            ArrayList<User> list = new ArrayList<>();
            list.add(request.getFollower());
            followersByFollowee.put(request.getFollowee(), list);

            followeesByFollower.get(request.getFollower()).add(request.getFollowee());

            return new MessageResponse(true);
        } else if (followeesByFollower.get(request.getFollower()) == null) {
            ArrayList<User> list1 = new ArrayList<>();
            list1.add(request.getFollowee());
            followeesByFollower.put(request.getFollower(), list1);

            followersByFollowee.get(request.getFollowee()).add(request.getFollower());

            return new MessageResponse(true);
        }
        followersByFollowee.get(request.getFollowee()).add(request.getFollower());
        followeesByFollower.get(request.getFollower()).add(request.getFollowee());
        return new MessageResponse(true);
    }

    public MessageResponse removeFollow(FollowRequest request) {
        assert request.getFollowee() != null;
        assert request.getFollower() != null;

        if (followersByFollowee == null || followeesByFollower == null) {
            return new MessageResponse(false);
        }

        if (followersByFollowee.get(request.getFollowee()) == null) {
            return new MessageResponse(false);
        }
        if (followeesByFollower.get(request.getFollower()) == null) {
            return new MessageResponse(false);
        }
        if (followersByFollowee.get(request.getFollowee()).contains(request.getFollower())) {
            followersByFollowee.get(request.getFollowee()).remove(request.getFollower());
            return new MessageResponse(true);
        }
        if (followeesByFollower.get(request.getFollower()).contains(request.getFollowee())){
            followeesByFollower.get(request.getFollower()).remove(request.getFollowee());
            return new MessageResponse(true);
        }
         return new MessageResponse(false);
    }
}
