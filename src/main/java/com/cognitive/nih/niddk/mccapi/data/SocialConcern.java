package com.cognitive.nih.niddk.mccapi.data;

public class SocialConcern {
    private String name;
    private String data;
    private String description;
    private String date;

    public SocialConcern()
    {

    }

    public SocialConcern(String name)
    {
        this.name = name;
        date = "None";
        data = "No data";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
