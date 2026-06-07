package com.team5.community.repository;

import com.team5.community.entity.Likes;
import com.team5.community.entity.Post;
import com.team5.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    
    Optional<Likes> findByUserAndPost(User user, Post post);
    
    boolean existsByUserAndPost(User user, Post post); // 이 메서드가 꼭 있어야 합니다
    
    long countByPost(Post post); // 좋아요 개수 세기
    
    void deleteByUserAndPost(User user, Post post);
    
    // [추가] 게시글 삭제 시 해당 게시글과 연관된 모든 좋아요를 삭제하는 메서드
    void deleteByPost(Post post);
}