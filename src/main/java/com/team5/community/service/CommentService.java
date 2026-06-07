package com.team5.community.service;

import com.team5.community.entity.Comment;
import com.team5.community.entity.Post;
import com.team5.community.entity.User;
import java.util.List;
import com.team5.community.repository.CommentRepository;
import com.team5.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public void writeComment(Long postId, String content, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        Comment comment = new Comment(content, user, post);
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 본인 확인 (댓글 작성자와 로그인한 유저가 같은지)
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 댓글만 삭제할 수 있습니다.");
        }
        
        commentRepository.delete(comment);
    }
    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPostOrderByCreatedAtDesc(post);
    }
}