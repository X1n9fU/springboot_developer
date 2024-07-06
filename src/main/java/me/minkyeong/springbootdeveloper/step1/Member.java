package me.minkyeong.springbootdeveloper.step1;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED) //접근 제어자가 protected인 기본 생성자 생성
@AllArgsConstructor
@Getter
@Entity(name="member_list") //엔티티 지정
public class Member {
    @Id // id 필드를 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //기본키 자동으로 1씩 증가
    @Column( name = "id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    //name 이라는 notnull 컬럼과 매핑
    private String name;

    public void changeName(String name){
        this.name = name;
    }
}
