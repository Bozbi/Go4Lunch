package com.sbizzera.go4lunch.model;

import java.util.Arrays;
import java.util.List;

public class FakeWorkmates {
    private String choice;
    private String photoUrl;

    public FakeWorkmates(String choice, String photoUrl) {
        this.choice = choice;
        this.photoUrl = photoUrl;
    }

    public static List<FakeWorkmates> workMatesList = Arrays.asList(
            new FakeWorkmates("Boris is eating at La Boussole","https://randomuser.me/api/portraits/men/32.jpg"),
            new FakeWorkmates("Céline hasn't decided yet","https://randomuser.me/api/portraits/women/95.jpg"),
            new FakeWorkmates("Gaspard is eating at Epinard Burgers","https://randomuser.me/api/portraits/men/22.jpg"),
            new FakeWorkmates("Jojo hasn't decided yet","https://pbs.twimg.com/profile_images/1056600329662459904/ukegoVAy.jpg"),
            new FakeWorkmates("Boris is eating at La Boussole","https://randomuser.me/api/portraits/men/32.jpg"),
            new FakeWorkmates("Céline hasn't decided yet","https://randomuser.me/api/portraits/women/95.jpg"),
            new FakeWorkmates("Gaspard is eating at Epinard Burgers","https://randomuser.me/api/portraits/men/22.jpg"),
            new FakeWorkmates("Jojo hasn't decided yet","https://pbs.twimg.com/profile_images/1056600329662459904/ukegoVAy.jpg"),
            new FakeWorkmates("Boris is eating at La Boussole","https://randomuser.me/api/portraits/men/32.jpg"),
            new FakeWorkmates("Céline hasn't decided yet","https://randomuser.me/api/portraits/women/95.jpg"),
            new FakeWorkmates("Gaspard is eating at Epinard Burgers","https://randomuser.me/api/portraits/men/22.jpg"),
            new FakeWorkmates("Jojo hasn't decided yet","https://pbs.twimg.com/profile_images/1056600329662459904/ukegoVAy.jpg"),
            new FakeWorkmates("Boris is eating at La Boussole","https://randomuser.me/api/portraits/men/32.jpg"),
            new FakeWorkmates("Céline hasn't decided yet","https://randomuser.me/api/portraits/women/95.jpg"),
            new FakeWorkmates("Gaspard is eating at Epinard Burgers","https://randomuser.me/api/portraits/men/22.jpg"),
            new FakeWorkmates("Jojo hasn't decided yet","https://pbs.twimg.com/profile_images/1056600329662459904/ukegoVAy.jpg")
    );

    public static List<FakeWorkmates> getWorkMatesList(){

        return workMatesList;
    }

    public String getChoice() {
        return choice;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
