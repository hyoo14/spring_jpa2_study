package jpabook.jpashop.domain;
import lombok.Getter;
import lombok.Setter;



import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") //주인이 아님. mappedBy
    private List<Order> orders = new ArrayList<>(); //이 컬렉션을 가급적이면 밖으러 꺼내지도 말고 변경하지 말아야.
    //있는 거 그대로 써야 안전합니다. 하이버네이트가 관리하기 때문에 바꾸면 동작 제대로 안 할 수 있습니다.
    //그러므로 , 컬랙션은 필드에 바로 초기화해야함
}