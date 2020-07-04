package com.sda.entity;

import lombok.Data;

import javax.persistence.*;

import javax.validation.constraints.Size;

import java.sql.Time;
import java.util.Date;

@Entity
@Data
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue
    @Column(name="comm_id")
    private int id;
    @Size(max = 500)
    private String commentary;
    private Date date;
    @ManyToOne
    private Event event;

}
