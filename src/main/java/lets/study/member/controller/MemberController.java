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
        return ResponseEntity.ok("ȸ������ ����");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
    	System.out.println("����");
    	try {
            String username = memberService.login(request); // ���̵�, ��й�ȣ Ȯ��
            String token = jwtUtil.generateToken(username); // JWT �߱�
            System.out.println("token ==> " +token);
            
            if (token != null && jwtUtil.validateToken(token)) {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER")); // �ʿ�� ���� ����
                Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/"); // ��� ��ο� ���� ��ȿ
            cookie.setMaxAge(60 * 60); // 1�ð� ��ȿ
            response.addCookie(cookie);
            
            return ResponseEntity.ok("�α��� ����");
        } catch (IllegalArgumentException e) {
        	System.out.println(e);
            // �߸��� �α��� �� 401 Unauthorized ��ȯ
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
        	System.out.println(e);
            // �� �� ������ 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("���� ���� �߻�");
        }
    }
    

    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // ��Ű ������ ���� ���� �̸�, Path, Max-Age=0 ����
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // ��� �����Ŵ
        response.addCookie(cookie);

        // �ʿ�� SecurityContext ����
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("�α׾ƿ� ����");
    }
}
