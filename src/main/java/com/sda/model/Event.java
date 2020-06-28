package com.sda.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private LocalDate date;
    private LocalTime time;
    @ManyToOne
    private User user;

    @Transient
    String dateString;
    @Transient
    String timeString;


}
