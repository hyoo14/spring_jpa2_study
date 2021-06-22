package jpabook.jpashop.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;



import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty //제약조건 주기 //java validation 검증.
    // ->문제가 있음. 화면에서 나오는(프레젠테이션) 검증로직이 엔티티에 들어감
    private String name; //만약에 username으로 바꾼다면, api스펙 자체가 username으로 바뀌어버림. 이것이 큰 문제
    //엔티티 바뀔 확률이 높은데 이거 바꾸었다고 api스펙이 바뀌는 것이 문제!!
    //결론적으로 api스펙을 위한 별도의 dto를 만들어야 합니다! 바로 바인딩해서 이렇게 쓰면 나중에 큰일납니다. 많은 서비스 장애 발생.
    //api 스펙에 맞게 별도의 dto를 받으시는 것이 좋습니다!
    //같은 가입이어도 여러 케이스에 대한 여러가지 api가 만들어질 가능성이 높음 ->엔티티 바뀔 일은 매우 많다!
    //api를 만들 때는 항상 엔티티를 파라미터로 받지 마시오. 엔티티를 웹에 노출해도 안돼!

    @Embedded
    private Address address;

    //@JsonIgnore //jpa2 주석: 안 보이게 할 수도 있음 //하지만 다른 api만들 때 멘붕이 옵니다..
    @OneToMany(mappedBy = "member") //주인이 아님. mappedBy
    private List<Order> orders = new ArrayList<>(); //이 컬렉션을 가급적이면 밖으러 꺼내지도 말고 변경하지 말아야.
    //있는 거 그대로 써야 안전합니다. 하이버네이트가 관리하기 때문에 바꾸면 동작 제대로 안 할 수 있습니다.
    //그러므로 , 컬랙션은 필드에 바로 초기화해야함
}