package lets.study.member.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lets.study.member.controller.dto.LoginRequestDto;
import lets.study.member.controller.dto.SignupRequestDto;
import lets.study.member.service.MemberService;
import lets.study.util.JwtUtil;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    public MemberController(MemberService memberService, JwtUtil jwtUtil) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto request) {
        memberService.register(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
    	System.out.println("들어옴");
    	try {
            String username = memberService.login(request); // 아이디, 비밀번호 확인
            String token = jwtUtil.generateToken(username); // JWT 발급
            System.out.println("token ==> " +token);
            
            if (token != null && jwtUtil.validateToken(token)) {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER")); // 필요시 권한 설정
                Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/"); // 모든 경로에 대해 유효
            cookie.setMaxAge(60 * 60); // 1시간 유효
            response.addCookie(cookie);
            
            return ResponseEntity.ok("로그인 성공");
        } catch (IllegalArgumentException e) {
        	System.out.println(e);
            // 잘못된 로그인 시 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
        	System.out.println(e);
            // 그 외 에러는 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러 발생");
        }
    }
    

    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // 쿠키 삭제를 위해 동일 이름, Path, Max-Age=0 설정
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 즉시 만료시킴
        response.addCookie(cookie);

        // 필요시 SecurityContext 비우기
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("로그아웃 성공");
    }
}
