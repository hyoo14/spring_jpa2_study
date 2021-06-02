package jpabook.jpashop;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositioryTest {

    @Autowired MemberRepositiory memberRepositiory;

    @Test
    @Transactional
    public void testMember() throws Exception{
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long savedId = memberRepositiory.save(member);
        Member findMember = memberRepositiory.find(savedId);

        //then
        Assertions.assertThat( findMember.getId() ).isEqualTo(member.getId()); //->없어진건가?
        //Assertions.assertEquals(findMember.getId(), member.getId());
        //Assertions.assertEquals(findMember.getUsername(), member.getUsername());



    }
}