package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em; //주입을 해줌? 엔티티메니저 스프링부트가 다 해줌(생성), 설정파일 읽어서 생성

    public Long save(Member member){ //커맨드랑 쿼리를 분리해라. (원칙?) 아이디 정도만 조회하는 걸로 설계
        em.persist(member);
        return member.getId();
    }
    public Member find(Long id){
        return em.find(Member.class, id);
    }
}
