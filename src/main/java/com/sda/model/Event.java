package com.sda.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Data
@Table(name="`event`")
public class Event {

    @Id
    @GeneratedValue
    @Column(name="EV_ID")
    private int id;
    @Column(name="EV_TITLE")
    private String title;
    @Column(name="EV_DESCRIPTION")
    private String description;
    @Column(name="EV_CITY")
    private String city;
    @Column(name="EV_DATE")
    private Date date;
    @Column(name="EV_TIME")
    private Time time;
    @Column(name="EV_USER")
    private User user;


}
