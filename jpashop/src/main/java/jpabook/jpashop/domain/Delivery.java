package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;import javax.persistence.*;
@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) //꼭 스트링으로 써야, 순서가 안 밀림!
    private DeliveryStatus status; //ENUM [READY(준비), COMP(배송)]
}