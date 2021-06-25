package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    //롬복이 큰 도움. 추가할 때 생성자 세터 다 고쳐야 하는데 안 해도 됨.
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    /**
     *주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        //엔티티조회
        //Member member = memberRepository.findOne(memberId); //spring data jpa 적용 전
        Member member = memberRepository.findById(memberId).get();
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); //실제로는 배송지 정보 새로 입력해야겠죠? 여기선 걍 간단하게

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        ////누가
        //OrderItem orderItem1 = new OrderItem(); //이런식으로 생성할 수 있음, 이거 막아야함. //프로텍티드 해놓으면 컴파일 오류
        //쓰지 말라는 시그널 줌

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order); //하나만 저장을 해줘도 오더 아이템이랑 딜리버리가 자동으로 퍼시스트 된겁니다. 캐스캐이드덕분
        //캐스케이드 범위 그래서 고민하게 됨 어디까지 해야하나..
        //오더가 딜리버리 관리하고 오더가 오더아이템 관리. 요 그림정도만 써야한다?
        //참조하는 것이 프라이빗오너인 경우만.
        //라이프사이클 동일하게 관리할 때 의미.
        //다른 것이 참조할 수 없는 프라이빗 오너인 경우 이걸 쓰면 도움받을 수 있음, 그게 아니라면 예를 들어 딜리버리 다른 데서 막 갖다 쓰면
        //이렇게 캐스케이드 막 쓰면 안 됨. 오토 지울 때 다 지워지고 퍼시스트 다른 데 걸려있으면 복잡하게 돌어감
        //오더 아이템도 마찮가지. 다른 데서 막 갖다 쓸 경우 캐스케이드 쓰지 말고 따로 리포지토리 생성해서 퍼시스트 퍼시스트 별도로.
        //이번 케이스는 딱 오다가 딜리버리 오더가 오더아이템 만 사용. 퍼시스트 해야할 라이프사이클 모두 똑같기 때문에 이렇게 씀
        //개념 잘 안 들어오면 안 쓰다가 이런 경우에는 참조 따로 없고 같이 퍼시스트 하는구나 할 때 리펙토링 하면서 캐스캐이드 넣어주는 것이 나은 방법

        //코드를 제약하는 스타일로 짜시는 것이, 유지보수를 끌어갈 수 있음
        return order.getId();
    }


    /*
     * 주문취소
     * */
    @Transactional
    public void cancelOrder(Long orderId){
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문최소
        order.cancel(); //변경내역 감지해서 데이터베이스에 업데이트 쿼리 촥촥 날라감 -jpa 장점.

    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByString(orderSearch);
    } //단순 조회면 리포짓토리 그냥 호출 하는 편.. //여기서는 코드 얼마 없고 하니 서비스에 위임함
}
