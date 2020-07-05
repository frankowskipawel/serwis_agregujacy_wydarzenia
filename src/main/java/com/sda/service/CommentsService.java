package com.sda.service;

import com.sda.entity.Comment;
import com.sda.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentsService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getComments(int eventId){
        return commentRepository.findByEvent_Id(eventId);
    }

    public Page<Comment> findAllOngoingEvent(Pageable pageable, String query, Date date) {
        return commentRepository.findAllByDateAndTimeOrderByDateDesc(pageable,query,date);
    }

    public void save(Comment comment){
        commentRepository.save(comment);
    }
}
