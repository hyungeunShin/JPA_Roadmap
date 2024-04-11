package com.example.member.controller;

import com.example.member.dto.*;
import com.example.util.ServiceResult;
import com.example.member.domain.Gender;
import com.example.member.domain.Member;
import com.example.member.service.MemberService;
import com.example.util.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService service;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("bodyText", "login-page");
        model.addAttribute("member", new LoginDTO());
        return "login/login";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("member") LoginDTO dto, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("bodyText", "login-page");
            return "login/login";
        }

        try {
            Member findMember = service.login(dto);
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_MEMBER, findMember);
            return "redirect:/board/list";
        }  catch(NullPointerException e) {
            model.addAttribute("bodyText", "login-page");
            model.addAttribute("message", "아이디 및 비밀번호를 확인해주세요.");
            return "login/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("genderTypes", Gender.values());
        model.addAttribute("member", new RegisterMemberDTO());
        model.addAttribute("bodyText", "register-page");
        return "login/register";
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("member") RegisterMemberDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra) {
        log.info("회원가입 정보 = {}", dto);

        if(!dto.getMemAgree()) {
            log.info("개인정보 동의 안함");
            bindingResult.addError(new FieldError("member", "memAgree", "개인정보 수집을 동의해주세요."));
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("bodyText", "register-page");
            model.addAttribute("genderTypes", Gender.values());
            return "login/register";
        }

        ServiceResult serviceResult = null;
        try {
            serviceResult = service.register(dto);
        } catch (IOException e) {
            model.addAttribute("genderTypes", Gender.values());
            model.addAttribute("bodyText", "register-page");
            model.addAttribute("message", "다시 시도 해주세요");
            return "login/register";
        } catch(NullPointerException e) {
            model.addAttribute("genderTypes", Gender.values());
            model.addAttribute("bodyText", "register-page");
            model.addAttribute("message", e.getMessage());
            return "login/register";
        }

        if(serviceResult.equals(ServiceResult.CREATED)) {
            ra.addFlashAttribute("message", "회원가입이 완료되었습니다.");
            return "redirect:/login";
        } else {
            model.addAttribute("genderTypes", Gender.values());
            model.addAttribute("bodyText", "register-page");
            model.addAttribute("message", "다시 시도 해주세요");
            return "login/register";
        }
    }

    @ResponseBody
    @PostMapping("/idCheck")
    public ResponseEntity<ServiceResult> idCheck(@RequestParam("memId") String memId) {
        log.info("idCheck.id : {}", memId);
        ServiceResult result = service.idCheck(memId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/forget")
    public String forgetForm(Model model) {
        model.addAttribute("bodyText", "login-page");
        return "login/forget";
    }

    @ResponseBody
    @PostMapping("/idForget")
    public ResponseEntity<String> idForget(@RequestBody FindIdDTO dto) {
        try {
            String memId = service.findId(dto);
            return new ResponseEntity<>(memId, HttpStatus.OK);
        } catch(NullPointerException e) {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
    }

    @ResponseBody
    @PostMapping("/pwForget")
    public ResponseEntity<String> pwForget(@RequestBody FindPwDTO dto) {
        try {
            String memPw = service.findPw(dto);
            return new ResponseEntity<>(memPw, HttpStatus.OK);
        } catch(NullPointerException e) {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
    }

    @GetMapping("/profile")
    public String profileForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, Model model) {
        ProfileFormDTO dto = service.findMember(member.getId());
        log.info("profile : {}", dto);
        model.addAttribute("member", dto);
        model.addAttribute("genderTypes", Gender.values());
        return "board/profile";
    }

    @PostMapping("/profile")
    public String editProfile(@ModelAttribute("member") @Validated ProfileEditDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra
            , HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            ProfileFormDTO formDTO = service.findMember(dto.getMemNo());
            dto.setUploadFileName(formDTO.getUploadFileName());
            model.addAttribute("genderTypes", Gender.values());
            return "board/profile";
        }

        try {
            Member member = service.editProfile(dto);
            HttpSession session = request.getSession(false);
            session.setAttribute(SessionConst.LOGIN_MEMBER, member);
            ra.addFlashAttribute("message", "프로필이 수정되었습니다");
            return "redirect:/profile";
        } catch(NullPointerException e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("genderTypes", Gender.values());
            return "board/profile";
        } catch(IOException e) {
            model.addAttribute("message", "다시 시도해주세요");
            model.addAttribute("genderTypes", Gender.values());
            return "board/profile";
        }
    }
}
