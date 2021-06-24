package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery( "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class) //o가 dto에 매핑될 수 없음
                .getResultList();
        //jpa는 엔티티나 벨류 오브젝트가 기본적으로 반환할 수 있음. dto같은 거 안됨
        //new 오퍼레이션 꼭 서야함. 좀 지져분해짐


    }

    //이렇게 분리해놓으면 유지보수가 좋음.
    //기존 리포지토리는 엔티티 접근용으로 놔둠.
}
