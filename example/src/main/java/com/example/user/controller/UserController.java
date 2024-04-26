package com.example.user.controller;

import com.example.user.domain.Gender;
import com.example.user.domain.User;
import com.example.user.dto.FindIdDTO;
import com.example.user.dto.FindPwDTO;
import com.example.user.dto.RegisterUserDTO;
import com.example.user.dto.ResetPwDTO;
import com.example.user.service.UserService;
import com.example.util.ServiceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class UserController {
    private final UserService service;

    @ModelAttribute("genderTypes")
    public Gender[] genderTypes() {
        return Gender.values();
    }

    @ModelAttribute("bodyText")
    public String bodyText() {
        return "login-page";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new RegisterUserDTO());
        return "login/register";
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("user") RegisterUserDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra) {
        log.info("회원가입 정보 = {}", dto);

        if(!dto.getAgree()) {
            bindingResult.addError(new FieldError("user", "agree", "개인정보 수집을 동의해주세요."));
        }

        if(bindingResult.hasErrors()) {
            return "login/register";
        }

        ServiceResult serviceResult = null;
        try {
            serviceResult = service.register(dto);
        } catch (IOException e) {
            model.addAttribute("message", "다시 시도 해주세요");
            return "login/register";
        } catch(NullPointerException e) {
            model.addAttribute("message", e.getMessage());
            return "login/register";
        }

        if(serviceResult.equals(ServiceResult.CREATED)) {
            ra.addFlashAttribute("message", "회원가입이 완료되었습니다.");
            return "redirect:/login";
        } else {
            model.addAttribute("message", "다시 시도 해주세요");
            return "login/register";
        }
    }

    @ResponseBody
    @PostMapping("/idCheck")
    public ResponseEntity<ServiceResult> idCheck(@RequestParam("username") String username) {
        log.info("idCheck.id : {}", username);
        ServiceResult result = service.idCheck(username);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/idForget")
    public String idForgetForm(Model model) {
        return "login/forgetId";
    }

    @ResponseBody
    @PostMapping("/idForget")
    public ResponseEntity<String> idForget(@RequestBody FindIdDTO dto) {
        try {
            String username = service.findId(dto);
            return new ResponseEntity<>(username, HttpStatus.OK);
        } catch(NullPointerException e) {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
    }

    @GetMapping("/pwForget")
    public String pwForgetForm(@ModelAttribute("user") FindPwDTO dto) {
        return "login/forgetPw";
    }

    @PostMapping("/pwForget")
    public String pwForget(@Validated @ModelAttribute("user") FindPwDTO dto, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            return "login/forgetPw";
        }

        try {
            User user = service.findPw(dto);
            model.addAttribute("user", new ResetPwDTO(user.getId()));
            return "login/resetPw";
        } catch(NullPointerException e) {
            model.addAttribute("message", "존재하지 않는 회원입니다.");
            return "login/forgetPw";
        }
    }

    @PostMapping("/resetPw")
    public String resetPw(@Validated @ModelAttribute("user") ResetPwDTO dto, BindingResult bindingResult, RedirectAttributes ra) {
        if(!StringUtils.equals(dto.getNewPw(), dto.getNewPw2())) {
            bindingResult.addError(new FieldError("user", "newPw2", "비밀번호를 확인해주세요."));
        }

        if(bindingResult.hasErrors()) {
            return "login/resetPw";
        }

        return "";
    }

//    @GetMapping("/profile")
//    public String profileForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, Model model) {
//        ProfileFormDTO dto = service.findMember(member.getId());
//        log.info("profile : {}", dto);
//        model.addAttribute("member", dto);
//        return "board/profile";
//    }
//
//    @PostMapping("/profile")
//    public String editProfile(@ModelAttribute("member") @Validated ProfileEditDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra
//            , HttpServletRequest request) {
//        if(bindingResult.hasErrors()) {
//            ProfileFormDTO formDTO = service.findMember(dto.getMemNo());
//            dto.setUploadFileName(formDTO.getUploadFileName());
//            return "board/profile";
//        }
//
//        try {
//            Member member = service.editProfile(dto);
//            HttpSession session = request.getSession(false);
//            session.setAttribute(SessionConst.LOGIN_MEMBER, member);
//            ra.addFlashAttribute("message", "프로필이 수정되었습니다");
//            return "redirect:/profile";
//        } catch(NullPointerException e) {
//            model.addAttribute("message", e.getMessage());
//            return "board/profile";
//        } catch(IOException e) {
//            model.addAttribute("message", "다시 시도해주세요");
//            return "board/profile";
//        }
//    }
}
