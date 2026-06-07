package com.team5.community.repository;

import com.team5.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // 기본 제공되는 findById(학번) 기능을 사용하면 DB에서 바로 회원을 찾을 수 있습니다!
}