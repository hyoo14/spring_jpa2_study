package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;


@RestController //@Controller @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> memberV1(){

        return memberService.findMembers(); //entity 정보 다 노출됨.

    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
        //엔티티 디티오로 변환해주는 수고로움이 있지만 스펙이 변하지 않는 장점이 있다.
        //감싸서 변환하기 때문에 유연성도 생긴다.
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;
        //t type data
        //generic이니까 T?? ->object type으로 변환해주는.. 감싸주는것. 유연성을 높여줌.
        //배열만으로 나오면 유연성이 떨어지니깐..
    }
    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

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

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2
            (@PathVariable("id") Long id,
             @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id); //스타일이긴 한데 단순하게 pk찍어서 쿼리 날림
        //커맨드와 쿼리 분리해서 유지보수하는 방법

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());

    }
    @Data
    static class UpdateMemberRequest {
        private String name;
    }
    @Data
    @AllArgsConstructor //dto는 대충 data 왔다갔다니까 롬복 어노테이션 많이 씀. 실용적 관점에서
    static class UpdateMemberResponse{
        private Long id;
        private String name;
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
