package com.sda.service;

import com.sda.entity.Comments;
import com.sda.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentsService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comments> getComments(int eventId){
        return commentRepository.findByEvent_Id(eventId);
    }

    public void comment(Comments comments){
        commentRepository.save(comments);
    }
}
