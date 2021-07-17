package com.example.emergency;

public class DisasterModel {
    String dateofoccurance,disastercategory,disastercity,disastername,disasterpicture;

    public String getDateofoccurance() {
        return dateofoccurance;
    }

    public String getDisastercategory() {
        return disastercategory;
    }

    public String getDisastercity() {
        return disastercity;
    }

    public String getDisastername() {
        return disastername;
    }

    public String getDisasterpicture() {
        return disasterpicture;
    }

    public DisasterModel() {
    }

    public DisasterModel(String dateofoccurance, String disastercategory, String disastercity, String disastername, String disasterpicture) {
        this.dateofoccurance = dateofoccurance;
        this.disastercategory = disastercategory;
        this.disastercity = disastercity;
        this.disastername = disastername;
        this.disasterpicture = disasterpicture;
    }
}
