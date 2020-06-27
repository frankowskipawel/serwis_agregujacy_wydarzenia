package com.sda.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="`event`")
public class Event {

    @Id
    @GeneratedValue
    private int id;
    private String title;
    private String description;
    private String city;
    private Date date;
    private Time time;
    @ManyToOne
    private User user;


}
