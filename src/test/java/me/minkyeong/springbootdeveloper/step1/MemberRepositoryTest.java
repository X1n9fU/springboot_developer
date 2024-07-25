//package me.minkyeong.springbootdeveloper.step1;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.jdbc.Sql;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//class MemberRepositoryTest {
//    @Autowired
//    MemberRepository memberRepository;
//
//    /* Member 가져오기 */
//
//    @Sql("/insert-members.sql")
//    //테스트 실행 전 sql 스크립트 실행
//    @Test
//    void getAllMembers(){
//        //SELECT * FROM member;
//        //when
//        List<Member> members = memberRepository.findAll();
//
//        //then
//        assertThat(members.size()).isEqualTo(3);
//    }
//
//    @Sql("/insert-members.sql")
//    @Test
//    void getMemberById(){
//        //SELECT * FROM member WHERE id = 2;
//        //when
//        Member member = memberRepository.findById(2L).get();
//
//        //then
//        assertThat(member.getName()).isEqualTo("B");
//    }
//
//    @Sql("/insert-members.sql")
//    @Test
//    void getMemberByName(){
//        //SELECT * FROM member WHERE name = 'C';
//        //when
//        Member member = memberRepository.findByName("C").get();
//
//        //then
//        assertThat(member.getId()).isEqualTo(3);
//    }
//
//    /* Member 저장하기 */
//
//    @Test
//    void saveMember(){
//        //INSERT INTO member (id, name) VALUES (1, 'A');
//        //given
//        Member member = new Member(1L, "A");
//
//        //when
//        memberRepository.save(member);
//
//        //then
//        assertThat(memberRepository.findById(1L).get().getName()).isEqualTo("A");
//    }
//
//    @Test
//    void saveMembers(){
//        //given
//        List<Member> members = List.of(new Member(2L, "B"),
//                new Member(3L, "C"));
//
//        //when
//        memberRepository.saveAll(members);
//
//        //then
//        assertThat(memberRepository.findAll().size()).isEqualTo(2);
//    }
//
//    /* Member 삭제하기 */
//    @Sql("/insert-members.sql")
//    @Test
//    void deleteMemberById(){
//        //DELETE FROM member WHERE id = 2;
//        //when
//        memberRepository.deleteById(2L);
//
//        //then
//        assertThat(memberRepository.findById(2L)).isEmpty();
//        assertThat(memberRepository.findById(2L).isEmpty()).isTrue();
//        //둘이 같은 표현
//    }
//
//    @Sql("/insert-members.sql")
//    @Test
//    void deleteMembers(){
//        //DELETE FROM member;
//        //when
//        memberRepository.deleteAll();
//
//        //then
//        assertThat(memberRepository.findAll()).isEmpty();
//        assertThat(memberRepository.findAll().size()).isZero();
//        //둘이 같은 표현
//    }
//    //모두 삭제하는 메소드는 테스트 간의 격리를 보장하기 위해 사용
//    //즉, 한 테스트의 실행으로 데이터베이스가 변경 된 것이 다른 테스트에 영향을 주지 않도록 하기 위함
//    //따라서 보통 아래처럼 @AfterEach로 사용
//
//    @AfterEach
//    public void cleanUp(){
//        memberRepository.deleteAll();
//    }
//
//    /* Member 업데이트 */
//    @Sql("/insert-members.sql")
//    @Test
//    void update(){
//        //UPDATE member SET name = 'BC' WHERE id = 2;
//        //given
//        Member member = memberRepository.findById(2L).get();
//
//        //when
//        member.changeName("BC");
//
//        //then
//        assertThat(memberRepository.findById(2L).get().getName()).isEqualTo("BC");
//    }
//
//    //@Transactional 을 사용 하지 않았는데 적용된 이유 ?
//    // -> 맨 위에 @DataJpaTest에 포함되어 있음
//
//
//}