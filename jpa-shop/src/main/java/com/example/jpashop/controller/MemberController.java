package com.example.jpashop.controller;

import com.example.jpashop.controller.dto.MemberForm;
import com.example.jpashop.domain.Address;
import com.example.jpashop.domain.Member;
import com.example.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService service;

    @GetMapping("/members/new")
    public String joinForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String join(@Validated @ModelAttribute("memberForm") MemberForm form, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        try {
            service.join(member);
        } catch(IllegalStateException e) {
            model.addAttribute("message", "이미 존재하는 이름입니다.");
            return "members/createMemberForm";
        }

        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = service.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
