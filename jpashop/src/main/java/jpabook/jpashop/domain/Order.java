package jpabook.jpashop.domain;
import lombok.Getter;import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //주문 회원

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //원래는 오더 저장하고 쭉쭉 저장해야하는데
    private List<OrderItem> orderItems = new ArrayList<>();

    //오더 아이템A,B,C jpa persist 해줘야하는데, 그담 poersisit(orrder) 이런 식
    //persist cascade가 전파해줌, 딜리트할 때 다 지워줌


    @OneToOne(cascade = CascadeType.ALL, fetch = LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; //배송정보

    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //==연관관계 메서드==//(연관관계 편입 매서드)//양쪽 세팅 한방향으로 해결
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem) {orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==// //생성 변경시 이것만 바꾸면 되니까.. 이게 중요함. 이거저거 찾아다닐필요없이
    //실무에서는 훨씬 복잡하겠죠
    //오더 아이템도 이렇게 넘어오는게 아니라 파라미터나 디티오가 들어오면서 더 복잡하게 들어오고
    //안에서도 오더아이템 생성해서 넣을 수도 있음. 이게 상황따라 더 좋은 방법일 수도 있음
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for ( OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     *  주문취소
     * */
    public void cancel(){
        if (delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다."); //초기 컨디션 체크
        }
        this.setStatus(OrderStatus.CANCEL); //상태 바꿔주고
        for(OrderItem orderItem : orderItems){
            orderItem.cancel(); //아이템 제고 원복시켜줌
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     * */
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
        //람다 스트링 사용하면 더 깔끔하게 할 수도 있두아 ->아래처럼..
//        return orderItems.stream()
//                .mapToInt(OrderItem::getTotalPrice)
//                .sum();

    }
}