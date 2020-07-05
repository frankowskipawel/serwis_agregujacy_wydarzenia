package com.sda.repository;

import com.sda.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByEvent_Id(int eventId);

    Page<Comment> findAllByDateAndTimeOrderByDateDesc(Date date, Time time, Pageable pageable);

    default Page<Comment> findAllByDateAndTimeOrderByDateDesc(Pageable pageable, String query, Date date) {
        return findAllByDateAndTimeOrderByDateDesc(pageable, query, date);
    }
}

