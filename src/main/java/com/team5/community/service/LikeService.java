package com.team5.community.service;

import com.team5.community.entity.Likes;
import com.team5.community.entity.Post;
import com.team5.community.entity.User;
import com.team5.community.repository.LikesRepository;
import com.team5.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional; // 필수!

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikesRepository likesRepository;
    private final PostRepository postRepository;

    @Transactional
    public void toggleLike(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        Optional<Likes> existingLike = likesRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            likesRepository.delete(existingLike.get());
        } else {
            likesRepository.save(new Likes(user, post));
        }
    }

    // 컨트롤러에서 호출하는 메서드들 (이게 없어서 에러가 났던 겁니다)
    public boolean isLiked(User user, Post post) {
        return likesRepository.existsByUserAndPost(user, post);
    }
    
    public long countLikes(Post post) {
        return likesRepository.countByPost(post);
    }
}