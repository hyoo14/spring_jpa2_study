package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

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
}
