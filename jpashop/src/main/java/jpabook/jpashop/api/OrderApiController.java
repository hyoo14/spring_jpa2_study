package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
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
                .collect(Collectors.toList());
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
        }




    }
}
