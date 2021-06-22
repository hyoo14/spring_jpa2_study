package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service //jpa의 모든 데이터 변경,로직들은 트랜젝션 안에서 실행되어야함
@Transactional(readOnly = true) //그래야 레이지로딩 다 됨 //여기는 리드온리 많으니까 리드온리 트루!
@RequiredArgsConstructor //파이널 있는 필드만 가지고 생성자 만들어 줌
public class MemberService {

    //@Autowired //필드 인젝션! //이렇게 많으 쓰는데 단점이 많다. 예를 못 바꿔주는 단점. 엑세스할 수 있는 방법이 없어서..
    private final MemberRepository memberRepository; //바뀔 일 없으니까 파이널로 해주는 것 권장
    //세터 인젝션을 사용하기도 함.. ->근데 안 좋음.. 중간에 막 바뀔 수 있어서

    //@Autowired //그래서 생성자 인젝션을 씀 //오토와이어 어노테이션 없어도 자동으로 주입해줌
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }
    //결국 requiredArgsConstructor 권장. 이 스타일로 갈 것임


    /**
     * 회원가입
     */
    @Transactional//읽기 아닐 때는 리드온리 투루 해주면 안된다 //이렇게 써놓으면 리드온리 폴스됨
    public Long join(Member member){
        validateDuplicateMember(member);//중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member){
        //Exception
        List<Member> findMembers  = memberRepository.findByName(member.getName());//데이터베이스에 네임 유니크 제약이 안전
        if( !findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    //회원 전체 조회
    //@Transactional(readOnly = true) //읽기전용이니까 리소스 너무 쓰지 말고 읽기용 모드로 해서 읽어라 이러면 최적화 됨..
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    //@Transactional(readOnly = true) //읽기에는 가급적이면 리드온리에 트루 넣어줘야.
    public Member findOne(Long memberId){//단건조회
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
