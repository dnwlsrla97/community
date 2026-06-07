package com.team5.community.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "studentId")
// 1. ⚠️ MySQL에 만들어두신 회원 테이블 이름이 'users'가 아니라면 
//    아래 'users'를 진짜 테이블 이름(예: 'member', 'student' 등)으로 고쳐주세요!
@Table(name = "student") 
public class User {

    @Id
    // 2. ⚠️ MySQL 회원 테이블에서 '학번'이 저장된 실제 컬럼명을 name=" " 안에 정확히 적어주세요!
    //    (예: student_id, studentId, id, user_id 등 언더바나 대소문자 확인 필수)
    @Column(name = "student_id") 
    private String studentId; 

    // 3. ⚠️ '비밀번호'가 저장된 실제 컬럼명을 name=" " 안에 정확히 적어주세요!
    //    (예: password, pass, pwd 등)
    @Column(name = "password")
    private String password;  
    
    // 4. ⚠️ '이름'이 저장된 실제 컬럼명을 name=" " 안에 정확히 적어주세요!
    //    (예: name, user_name, username 등)
    @Column(name = "name")
    private String name;      
    public String getId() {
        return this.studentId;
    }
}