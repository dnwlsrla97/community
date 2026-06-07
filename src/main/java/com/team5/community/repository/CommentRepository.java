package com.team5.community.repository;

import com.team5.community.entity.Comment;
import com.team5.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 특정 게시글의 모든 댓글을 작성시간 역순(최신순)으로 가져오기
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    // [추가] 게시글 삭제 시 연관된 모든 댓글을 삭제하기 위한 메서드
    void deleteByPost(Post post);
}