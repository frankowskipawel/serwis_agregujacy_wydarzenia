package com.sda.modelAPI;

import lombok.Data;


@Data
public class EventAPI {


    private int id;
    private String title;
    private String description;
    private String city;
    private String startDate;
    private String endDate;
    private String user;
    private String picture;


}
