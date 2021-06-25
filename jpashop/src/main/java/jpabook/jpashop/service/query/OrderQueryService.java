package jpabook.jpashop.service.query; //어플리케이션 커지면 쿼리용 분리해 놓는 것이 좋음. //쿼리용 서비스는 별도로 분리해야!

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class OrderQueryService {

    //osiv false일 경우 여기에서(트랜잭셔널) 다 처리해주면 됨!

}
