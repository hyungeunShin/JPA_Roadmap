package com.example.user.controller;

import com.example.user.domain.CustomUser;
import com.example.user.domain.Gender;
import com.example.user.domain.User;
import com.example.user.dto.*;
import com.example.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    private final MessageSource messageSource;

    @ModelAttribute("genderTypes")
    public Gender[] genderTypes() {
        return Gender.values();
    }

    @GetMapping("/login")
    public String loginForm() {
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
        return ResponseEntity.ok(service.duplicatedId(username) ? messageSource.getMessage("Duplicate.Id", null, request.getLocale()) : messageSource.getMessage("Not.Duplicate.Id", null, request.getLocale()));
    }

    @ResponseBody
    @PostMapping("/duplicatedEmail")
    public ResponseEntity<String> duplicatedEmail(@RequestParam(name = "id", required = false) Long id, @RequestParam("email") String email, HttpServletRequest request) {
        return ResponseEntity.ok(service.duplicatedEmail(id, email) ? messageSource.getMessage("Duplicate.Email", null, request.getLocale()) : messageSource.getMessage("Not.Duplicate.Email", null, request.getLocale()));
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("user") RegisterUserDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra, HttpServletRequest request) {
        log.info("회원가입 정보 : {}", dto);

        if(bindingResult.hasErrors()) {
            return "user/register";
        }

        if(service.duplicatedId(dto.getUsername())) {
            model.addAttribute("message", messageSource.getMessage("Duplicate.Id", null, request.getLocale()));
            return "user/register";
        }

        if(service.duplicatedEmail(null, dto.getEmail())) {
            model.addAttribute("message", messageSource.getMessage("Duplicate.Email", null, request.getLocale()));
            return "user/register";
        }

        try {
            service.register(dto);
            ra.addFlashAttribute("message", messageSource.getMessage("Success.Save.User", null, request.getLocale()));
            return "redirect:/login";
        } catch(IOException e) {
            model.addAttribute("message", messageSource.getMessage(e.getMessage(), null, request.getLocale()));
            return "user/register";
        }
    }

    @GetMapping("/findId")
    public String idForgetForm(Model model) {
        model.addAttribute("user", new FindIdDTO());
        return "user/findId";
    }

    @PostMapping("/findId")
    public String idForget(@Validated @ModelAttribute("user") FindIdDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra, HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            return "user/findId";
        }

        try {
            String username = service.findId(dto);
            ra.addFlashAttribute("message", messageSource.getMessage("Success.Find.Id", new Object[] {username}, request.getLocale()));
            return "redirect:/login";
        } catch(NullPointerException e) {
            model.addAttribute("message", messageSource.getMessage("Not.Found.User", null, request.getLocale()));
            return "user/findId";
        }
    }

    @GetMapping("/pwForget")
    public String pwForgetForm(@ModelAttribute("user") FindPwDTO dto) {
        return "user/forgetPw";
    }

    @PostMapping("/pwForget")
    public String pwForget(@Validated @ModelAttribute("user") FindPwDTO dto, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            return "user/forgetPw";
        }

        try {
            User user = service.findPassword(dto);
            model.addAttribute("user", new ResetPwDTO(user.getId()));
            return "user/resetPw";
        } catch(NullPointerException e) {
            model.addAttribute("message", ResponseEntity.badRequest().body(messageSource.getMessage("Not.Found.User", null, request.getLocale())));
            return "user/forgetPw";
        }
    }

    @PostMapping("/resetPw")
    public String resetPw(@Validated @ModelAttribute("user") ResetPwDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra, HttpServletRequest request) {
        if(!StringUtils.equals(dto.getNewPassword(), dto.getCheckPassword())) {
            bindingResult.addError(new FieldError("user", "checkPassword", messageSource.getMessage("Not.Same.Password", null, request.getLocale())));
        }

        if(bindingResult.hasErrors()) {
            return "user/resetPw";
        }

        try {
            service.changePassword(dto);
            ra.addFlashAttribute("message", messageSource.getMessage("Success.Change.Password", null, request.getLocale()));
            return "redirect:/login";
        } catch(NullPointerException e) {
            model.addAttribute("message", messageSource.getMessage("Not.Found.User", null, request.getLocale()));
            return "user/resetPw";
        }
    }

    @GetMapping("/profile")
    public String profileForm(Model model) {
        CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EditUserViewDTO dto = service.findUser(user.getId());
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
            User user = service.edit(dto);
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
