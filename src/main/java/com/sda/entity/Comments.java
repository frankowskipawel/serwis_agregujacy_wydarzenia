package com.sda.entity;

import lombok.Data;

import javax.persistence.*;

import javax.validation.constraints.Size;
import java.sql.Date;


@Entity
@Data
@Table(name="comments")
public class Comments {
    @Id
    @GeneratedValue
    @Column(name="comm_id")
    private int id;
    @Size(max = 500)
    private String commentary;
    private Date date;
    @ManyToOne
    private Event event;
    @ManyToOne
    private User user;

}
