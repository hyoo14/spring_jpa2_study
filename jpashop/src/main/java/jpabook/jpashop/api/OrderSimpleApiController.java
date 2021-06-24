package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne
 * Order
 * Order -> Member
 * Order -> Delivery
 * */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for( Order order : all){
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){ //원리 리스트가 아니라 리절트로 감싸야하는 것이 정석! 지금은 테스트니까 그냥함
        //만약 order 2개라면
        //N + 1  -> 1(첫 쿼리 오더스) + N(N번만큼 추가 쿼리 실행됨)
        // == 1 + 회원 N + 배송 N
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());


        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v3/simple-orders") //패치조인 사용하여 쿼리 한번만 날리는 최적화!
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v4/simple-orders") //엔티티 거치지 않고 바로dto로만 하는 최적화!
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos(); //디비에서 데이터를 덜 퍼울림. 그것이 장점
        //하지만 v3, v4는 우열 가리기 힘듬. tradeoff있음.
        //v3는 내부에 원하는 것만 패치조인으로 가져와서 성능 튜닝
        //v4는 sql 짜듯이 jpql짜서 가져와버림. 재사용성이 떨어짐. 특정 dto 쓸 때만 쓸 수 있음.
        //v3는 많은 api에서 활용 가능.
        //v4는 성능이 좀 더 나음. 단 dto로 조회한 거는 변경을 할 수가 없음. 엔티티가 아니니깐 jpa로 할 수 있는 것이 없움. 코드도 좀 더 지저분.
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) { //dto가 entity 파라미터 받는 것은 크게 문제되지 않음
            //왜냐하면 별로 중요하지 않는 곳에서 중요한 엔티티에 의존하는 것이기 때문.
            orderId = order.getId();
            name = order.getMember().getName(); //lazy 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //lazy 초기화

        }
    }


}
