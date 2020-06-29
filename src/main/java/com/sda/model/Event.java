package com.sda.model;


import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Data
@Table(name="event")
public class Event {

    @Id
    @GeneratedValue
    private int id;
    private String title;
    @Size(min = 20, message = "size must be min 20 letters")
    private String description;
    private String city;
    private Date startDate;
    private Date endDate;

    @ManyToOne
    private User user;

    @Transient
    String startDateString;
    @Transient
    String startTimeString;
    @Transient
    String endDateString;
    @Transient
    String endTimeString;

}
