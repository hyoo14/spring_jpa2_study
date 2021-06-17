package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";

    }

    @PostMapping("members/new")
    public String create(@Valid MemberForm form, BindingResult result){

        if(result.hasErrors()){
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();  //단순하니까 이렇게 했지만.. 복잡해지면 dto로 화면에 출력하는 걸 권장.
        model.addAttribute("members", members);
        return "members/memberList";
    }
    //요구사항 단순할 때는 그냥 엔티티 쓰면 됨.
    //단순하지 않을 때. 1:1 매핑 없음.. 엔티티 추가하다보면 화면 종속적 엔티티 계속 생김.. 엔티티 지저분해짐.->유지보수 어렵
    //jpa 쓸 때. 엔티티는 핵심비지느시로직에만 디펜던시 있도록. 순수하게 만들어야 함. 유지보수 잘 하게.
    //엔티티는 화면을 위한 로직에 없어야 함.
    //화면에 맞는 객체는 폼 객체나 dto(데이터 트랜스퍼 오브젝트.)를 사용해야 함.
    //핵심 비즈니스 로직 건들였더니 화면 깨지고 화면 수정했더니 핵심 비즈니스 로직 안 맞으면 큰일 납니다.

    //api를 만들 때는 절대 엔티티를 웹으로 반환하면 안 됨. api라는 것은 스펙. 멤버 엔티티 반환하게 되면
    //1. 패스워드 노출 2. api스펙이 변해버림.
    //템플릿 엔진에선 괜찮지만 그래도 화면에 맞는 dto로 변환하는 것이 가장 깔끔.

}
