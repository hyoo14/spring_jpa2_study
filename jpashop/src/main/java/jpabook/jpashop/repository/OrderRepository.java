package jpabook.jpashop.repository;

//import jpabook.jpashop.api.OrderSimpleApiController; //리포지토리에서 컨트롤러에 의존관계 생기면 큰일납니다. //망하자는 거(강조하심)
//의존관계는 안으로 들어오거나 헥사고널 아키텍처처럼 다 인터페이스로 발라내거나 이런 거 아닌 이상 한방향으로 흘러가야함(컨트롤러에서 리포지토리 가는 거 정도)
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void  save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch){ //실무에서 안 써요


        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색 //실무에선 이렇게 안 함.
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();

    }
    /*
    * JPA Criteria //이것도 실무에선 안 씀
    * */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() { //한방 쿼리로 멤버 오더 딜리버리 조인한 다음 아예 셀렉 절에 다 넣고 한방에 다 땡겨옴
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" + //패치는 jpa만 있는 문법. sql에는 없음 //jpa 기본편 참고
                        " join fetch o.delivery d", Order.class //패치 조인은 실무에서 정말 자주 사용. 책이나 강좌로 100퍼 이해해야함!
        ).getResultList();
    }

//    public List<OrderSimpleQueryDto> findOrderDtos() {
//        return em.createQuery( "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
//                                    " join o.member m" +
//                                    " join o.delivery d", OrderSimpleQueryDto.class) //o가 dto에 매핑될 수 없음
//                        .getResultList();
//        //jpa는 엔티티나 벨류 오브젝트가 기본적으로 반환할 수 있음. dto같은 거 안됨
//        //new 오퍼레이션 꼭 서야함. 좀 지져분해짐
//
//    } ->order.simplequery에 OrderSimpleQueryRepository 만들어서 옮김.

    //리포지토리는 순수한 엔티티를 조회하는 데에 써야함!


}
