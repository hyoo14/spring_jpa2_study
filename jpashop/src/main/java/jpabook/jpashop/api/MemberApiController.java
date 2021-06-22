package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;


@RestController //@Controller @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members") //클래스를 안 만들어도 된다는 장점 뿐.
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    //v2: 요청 값으로 Member 엔티티 대신에 별도의 DTO 를 받음!

    @PostMapping("/api/v2/members") //값을 변환하는 시점에,, 변환 되었을 경우 에러가 나는 것을 볼 수 있음 //에러 사전 체크느낌
    public  CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest{
        @NotEmpty //여기서 체크해주는 것이 좋음. 엔티티외부 노출 없이 api스펙에 맞는 별도의 dto만드는 것. 정석!
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
