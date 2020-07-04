package com.sda.model;

import lombok.Data;

import javax.persistence.*;

import javax.validation.constraints.Size;
import java.util.Date;


@Entity
@Data
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue
    @Column(name="comm_id")
    private int id;
    @Column(columnDefinition="TEXT")
    @Size(max = 500, message = "Comments can have up to 500 signs only")
    private String commentary;
    private Date date;
    private Time time;
    @ManyToOne
    private Event event;
    @ManyToOne
    private User user;

}
