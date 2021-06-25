package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepositiory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepositiory orderQueryRepositiory;

    @GetMapping("/api/v1/orders") //entity 노출한 안 좋은 버전임
    public List<Order> orderV1(){
        List<Order> all = orderRepository.findAllByString((new OrderSearch()));
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems(); //강제초기화. 하이버네이트5의 기본 세팅 자체가 데이터 안 뿌림이어서
            orderItems.stream().forEach(o->o.getItem().getName()); //강제로 데이터 뿌리게 해줌.
        }
        return all;
    }

    @GetMapping("/api/v2/orders") //엔티티 감싸줌
    public List<OrderDto> orderV2(){

        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o)) //변환
                .collect(Collectors.toList());
        return result;

        //return orderRepository.findAllByString(new OrderSearch()).stream()
//        .map(OrderDto::new) //변환
//        .collect(toList()); ->이렇게 까지 줄일 수도 잇음
    }

    @GetMapping("/api/v3/orders") //패치조인으로 최적화
    public List<OrderDto> orderV3(){
        List<Order> orders = orderRepository.findAllWithItem();

        for (Order order : orders) {
            System.out.println("order ref=" + order+ "id=" + order.getId());
        }

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o)) //변환
                .collect(Collectors.toList()); //jpa2 osiv false일 경우, 이런 변환로직 자체를 OrderQueryService 같은 곳으로(트렌섹셔널) 옮겨서 다 처리해줌
        return result;

    }

    @GetMapping("/api/v3.1/orders") //패치조인으로 최적화
    public List<OrderDto> orderV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit )
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); //첫번째 패치조인

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;

    }

    @GetMapping("/api/v4/orders") //패치조인으로 최적화
    public List<OrderQueryDto> orderV4(){
        return orderQueryRepositiory.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders") //패치조인으로 최적화
    public List<OrderQueryDto> orderV5(){
        return orderQueryRepositiory.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders") //패치조인으로 최적화
//    public List<OrderFlatDto> orderV6(){ //기존 한방 쿼리 작성
//        return orderQueryRepositiory.findAllByDto_flat();
//    }
    public List<OrderQueryDto> orderV6(){ //노가다로 v5처럼 스펙 바꾼 경우(중복 없앤 경우) //개발자가 직접 분해 조립해서 넣은..맞출수는 있음을 보여준.............
        List<OrderFlatDto> flats = orderQueryRepositiory.findAllByDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }


    @Getter  //@Data를 써도 되고. no properties 에러는 보통 getter로 해결
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //벨류 오브젝트 같은 것은 엔티티서 노출해도 됨.
        //private List<OrderItem> orderItems; //이거 엔티티 수정 들어가면 api 다 망가져버림. 그래서 아래처럼 바꿈
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate  = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();

//            order.getOrderItems().stream().forEach(o->o.getItem().getName());//원래 이렇게 하면 안됨. orderItem도 다 dto로 바꾸어야함.
//
//            orderItems = order.getOrderItems();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }

    }

    @Getter
    static class OrderItemDto{

        private String itemName; //상품명
        private int orderPrice; //주문 가격
        private int count; //주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }//


        //osiv true(기본값) 되어 있으면 최종(화면이 렌더링되거나 api 값 반환)상태 까지 영속 상태가 유지됨
        //근데 계속 커넥션 들고 있어서 리소스 많이 잡아먹는 문제가 되기도 함.(단점)
        //장점은 엔티티 적극 활용해서 레이지로딩 기술 엔티티나 뷰에 적극 사용할 수 있음, 개발입장에서 중복 줄이고 투명하게 끝까지 나갈 수 있음

        //osiv 끄면 트랜잭션 범위에서만 영속상태가 됨..
        //이 경우 OrderQueryService 처럼 트랙잭셔널에 넣어서 처리

        //성능 생각하면 꺼야하지만 유지보수는 열어야. 고객서비스의 실시간 api는 osiv 끄고 admin처럼 커넥션 많이 사용하지 않는 곳에서는 osiv 키는 것이 좋음

    }
}
