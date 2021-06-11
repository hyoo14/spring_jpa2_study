package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    //@PersistenceContext //엔티티매니저용 인젝션.. 근데 스프링부트가 @Autowired도 지원해줌
    private final EntityManager em; //스프링이 엔티티매니저 다 꺼내고 해줌.
    //생성자로 인젝션 한 것!

    public void save(Member member) {
        em.persist(member); //멤버객체 넣음, 커밋시점에 디비에 반영됨
    }

    public Member findOne(Long id){
        return em.find(Member.class, id); //jpa의 find매서드사용, 단건조회. 첫번째 타입, 그담 pk
    }

    public List<Member> findAll(){

        return em.createQuery( "select m from Member m", Member.class)
                .getResultList(); //jpql 부분으로 기본편 참고하십시오. 책봐도됨 jpa책 //from의 대상이 entity인 것만 다름. sql과 유사
    }

    public List<Member> findByName(String name){
        return em.createQuery( "select m from Member m where m.name = :name", Member.class)
                .setParameter( "name", name)
                .getResultList(); //파라미터 바인딩하여 특정회원에 대해서만 찾는 거 만들어봄
    }
}
