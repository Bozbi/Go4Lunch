package com.sbizzera.go4lunch.model;

import java.util.Arrays;
import java.util.List;

public class FakeRestaurants {
    private String name;
    private String address;
    private String openingHours;
    private String distance;
    private String workmateFrequentation;
    private String stars;
    private String photoUrl;

    public FakeRestaurants(String name, String address, String openingHours, String distance, String workmateFrequentation, String photoUrl) {
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.distance = distance;
        this.workmateFrequentation = workmateFrequentation;
        this.stars = stars;
        this.photoUrl = photoUrl;
    }

    public static List<FakeRestaurants> fakeRestaurantsList = Arrays.asList(
            new FakeRestaurants("Le Zinc", "6 rue de la monnaie", "Open until 7pm", "140m", "3", "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("La Boussole", "25 rue de la tuilerie", "Open until 10pm", "1260m", "1", "https://images.unsplash.com/photo-1525648199074-cee30ba79a4a?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("Le Chiblibli", "14 rue d'Autreuil", "Open until 9pm", "1560m", "2", "https://images.unsplash.com/photo-1515669097368-22e68427d265?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("Le Zinc", "6 rue de la monnaie", "Open until 7pm", "140m", "3", "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("La Boussole", "25 rue de la tuilerie", "Open until 10pm", "1260m", "1", "https://images.unsplash.com/photo-1525648199074-cee30ba79a4a?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("Le Chiblibli", "14 rue d'Autreuil", "Open until 9pm", "1560m", "2", "https://images.unsplash.com/photo-1515669097368-22e68427d265?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("Le Zinc", "6 rue de la monnaie", "Open until 7pm", "140m", "3", "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("La Boussole", "25 rue de la tuilerie", "Open until 10pm", "1260m", "1", "https://images.unsplash.com/photo-1525648199074-cee30ba79a4a?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("Le Chiblibli", "14 rue d'Autreuil", "Open until 9pm", "1560m", "2", "https://images.unsplash.com/photo-1515669097368-22e68427d265?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("Le Zinc", "6 rue de la monnaie", "Open until 7pm", "140m", "3", "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("La Boussole", "25 rue de la tuilerie", "Open until 10pm", "1260m", "1", "https://images.unsplash.com/photo-1525648199074-cee30ba79a4a?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80"),
            new FakeRestaurants("Le Chiblibli", "14 rue d'Autreuil", "Open until 9pm", "1560m", "2", "https://images.unsplash.com/photo-1515669097368-22e68427d265?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80")
    );

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getWorkmateFrequentation() {
        return workmateFrequentation;
    }

    public void setWorkmateFrequentation(String workmateFrequentation) {
        this.workmateFrequentation = workmateFrequentation;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
