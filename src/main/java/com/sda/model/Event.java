package com.sda.model;



import lombok.Data;
import org.hibernate.annotations.Columns;

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
    @Column(columnDefinition="TEXT")
    private String description;
    private String city;
    private Date startDate;
    private Date endDate;
    @ManyToOne
    private User user;
    @ManyToOne
    private Picture picture;

    @Transient
    String startDateString;
    @Transient
    String startTimeString;
    @Transient
    String endDateString;
    @Transient
    String endTimeString;

}
