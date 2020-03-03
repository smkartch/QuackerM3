package edu.byu.cs.tweeter.model.domain;

import android.graphics.Bitmap;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class User implements Comparable<User> {

    private final String firstName;
    private final String lastName;
    private final String alias;
    private final String imageUrl;
    private final Feed feed;
    private final Story story;

    public User(@NotNull String firstName, @NotNull String lastName, String imageURL) {
        this(firstName, lastName, String.format("@%s%s", firstName, lastName), imageURL);
    }

    public User(@NotNull String firstName, @NotNull String lastName, @NotNull String alias, String imageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.alias = alias;
        this.imageUrl = imageURL;
        feed = new Feed();
        story = new Story();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getAlias() {
        return alias;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Feed getFeed() {
        return feed;
    }

    public void addToStory(Status status) {
        story.addStatus(status);
    }

    public Story getStory() {
        return story;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return alias.equals(user.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias);
    }

    @NotNull
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", alias='" + alias + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public int compareTo(User user) {
        return this.getAlias().compareTo(user.getAlias());
    }
}
