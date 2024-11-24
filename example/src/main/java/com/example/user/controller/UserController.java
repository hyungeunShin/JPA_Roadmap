package com.example.user.controller;

import com.example.user.domain.CustomUser;
import com.example.user.domain.Gender;
import com.example.user.domain.User;
import com.example.user.dto.*;
import com.example.user.service.UserService;
import com.example.util.MailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserService userService;

    private final MailService mailService;

    private final MessageSource messageSource;

    @ModelAttribute("genderTypes")
    public Gender[] genderTypes() {
        return Gender.values();
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(name = "errorMessage", required = false) String errorMessage, Model model) {
        model.addAttribute("errorMessage", errorMessage);
        return "user/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new RegisterUserDTO());
        return "user/register";
    }

    @ResponseBody
    @PostMapping("/duplicatedId")
    public ResponseEntity<String> duplicatedId(@RequestParam("username") String username, HttpServletRequest request) {
        return ResponseEntity.ok(userService.duplicatedId(username) ? messageSource.getMessage("Duplicate.Id", null, request.getLocale()) : messageSource.getMessage("Not.Duplicate.Id", null, request.getLocale()));
    }

    @ResponseBody
    @PostMapping("/duplicatedEmail")
    public ResponseEntity<String> duplicatedEmail(@RequestParam(name = "id", required = false) Long id, @RequestParam("email") String email, HttpServletRequest request) {
        return ResponseEntity.ok(userService.duplicatedEmail(id, email) ? messageSource.getMessage("Duplicate.Email", null, request.getLocale()) : messageSource.getMessage("Not.Duplicate.Email", null, request.getLocale()));
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("user") RegisterUserDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra, HttpServletRequest request) {
        log.info("회원가입 정보 : {}", dto);

        if(bindingResult.hasErrors()) {
            return "user/register";
        }

        if(userService.duplicatedId(dto.getUsername())) {
            model.addAttribute("message", messageSource.getMessage("Duplicate.Id", null, request.getLocale()));
            return "user/register";
        }

        if(userService.duplicatedEmail(null, dto.getEmail())) {
            model.addAttribute("message", messageSource.getMessage("Duplicate.Email", null, request.getLocale()));
            return "user/register";
        }

        try {
            userService.register(dto);
            ra.addFlashAttribute("message", messageSource.getMessage("Success.Save.User", null, request.getLocale()));
            return "redirect:/login";
        } catch(IOException e) {
            model.addAttribute("message", messageSource.getMessage(e.getMessage(), null, request.getLocale()));
            return "user/register";
        }
    }

    @GetMapping("/findId")
    public String findIdForm(Model model) {
        model.addAttribute("user", new FindIdDTO());
        return "user/findId";
    }

    @PostMapping("/findId")
    public String findId(@Validated @ModelAttribute("user") FindIdDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra, HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            return "user/findId";
        }

        try {
            String username = userService.findId(dto);
            ra.addFlashAttribute("message", messageSource.getMessage("Success.Find.Id", new Object[] {username}, request.getLocale()));
            return "redirect:/login";
        } catch(NullPointerException e) {
            model.addAttribute("message", messageSource.getMessage("Not.Found.User", null, request.getLocale()));
            return "user/findId";
        }
    }

    @GetMapping("/findPassword")
    public String findPasswordForm(@ModelAttribute("user") FindPasswordDTO dto) {
        return "user/findPassword";
    }

    @ResponseBody
    @PostMapping("/authenticateEmail")
    public ResponseEntity<String> authenticateEmail(@RequestParam("email") String email, HttpServletRequest request) {
        log.info("{}로 이메일 발송 시도", email);

        try {
            mailService.sendEmail(email);
            log.info("이메일 발송 성공");
            return ResponseEntity.ok(messageSource.getMessage("Success.Send.Mail", new Object[] {email}, request.getLocale()));
        } catch(MessagingException e) {
            log.info("이메일 발송 실패");
            return ResponseEntity.badRequest().body(messageSource.getMessage("Fail.Send.Mail", null, request.getLocale()));
        }
    }

    @ResponseBody
    @PostMapping("/verifyCode")
    public ResponseEntity<String> verifyCode(@RequestParam("email") String email, @RequestParam("code") String code, HttpServletRequest request) {
        log.info("인증 번호 확인");

        if(mailService.verifyCode(email, code)) {
            log.info("인증 성공");
            return ResponseEntity.ok(messageSource.getMessage("Success.Verify.Code", null, request.getLocale()));
        } else {
            log.info("인증 실패");
            return ResponseEntity.badRequest().body(messageSource.getMessage("Fail.Verify.Code", null, request.getLocale()));
        }
    }

    @PostMapping("/findPassword")
    public String findPassword(@Validated @ModelAttribute("user") FindPasswordDTO dto, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.info("{}", fieldError);
                log.info("{}", fieldError.getDefaultMessage());
            }
            return "user/findPassword";
        }

        try {
            User user = userService.findPassword(dto);
            model.addAttribute("user", new ResetPasswordDTO(user.getId()));
            return "user/resetPassword";
        } catch(NullPointerException e) {
            model.addAttribute("message", messageSource.getMessage("Not.Found.User", null, request.getLocale()));
            return "user/findPassword";
        }
    }

    @PostMapping("/resetPassword")
    public String resetPw(@Validated @ModelAttribute("user") ResetPasswordDTO dto, BindingResult bindingResult, RedirectAttributes ra, HttpServletRequest request) {
        if(!StringUtils.equals(dto.getNewPassword(), dto.getCheckPassword())) {
            bindingResult.addError(new FieldError("user", "checkPassword", messageSource.getMessage("Not.Same.Password", null, request.getLocale())));
        }

        if(bindingResult.hasErrors()) {
            return "user/resetPassword";
        }

        try {
            userService.changePassword(dto);
            ra.addFlashAttribute("message", messageSource.getMessage("Success.Change.Password", null, request.getLocale()));
            return "redirect:/login";
        } catch(NullPointerException e) {
            ra.addFlashAttribute("message", messageSource.getMessage("Not.Found.User", null, request.getLocale()));
            return "redirect:/findPassword";
        }
    }

    @GetMapping("/profile")
    public String profileForm(Model model) {
        CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EditUserViewDTO dto = userService.findUser(user.getId());
        log.info("profile : {}", dto);
        model.addAttribute("user", dto);
        return "user/profile";
    }

    @PostMapping("/profile")
    public String editProfile(@ModelAttribute("user") @Validated EditUserDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra, HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            return "user/profile";
        }

        try {
            User user = userService.edit(dto);
            CustomUser customUser = new CustomUser(user);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities()));
            ra.addFlashAttribute("message", "프로필이 수정되었습니다.");
            return "redirect:/profile";
        } catch(IOException e) {
            model.addAttribute("message", messageSource.getMessage(e.getMessage(), null, request.getLocale()));
            return "user/profile";
        }
    }
}
