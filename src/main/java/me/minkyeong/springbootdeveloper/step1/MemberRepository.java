package me.minkyeong.springbootdeveloper.step1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);
    //쿼리 메소드

//    @Query("select m from Member m where m.name = ?1")
//    Optional<Member> findByNameQuery(String name);
}
