package com.sda.repository;

import com.sda.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Integer> {
    List<Comments> findByEvent_Id(int eventId);
    }

