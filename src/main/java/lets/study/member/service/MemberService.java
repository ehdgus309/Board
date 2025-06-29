package lets.study.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lets.study.member.controller.dto.LoginRequestDto;
import lets.study.member.controller.dto.SignupRequestDto;
import lets.study.member.entity.Member;
import lets.study.member.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(SignupRequestDto request) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (!username.matches("^[a-z0-9]{4,10}$")) {
            throw new IllegalArgumentException("아이디는 4~10자의 소문자와 숫자 조합이어야 합니다.");
        }
        if (!password.matches("^[a-zA-Z0-9]{8,15}$")) {
            throw new IllegalArgumentException("비밀번호는 8~15자의 대소문자와 숫자 조합이어야 합니다.");
        }
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));
        memberRepository.save(member);
    }

    public String login(LoginRequestDto request) {
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member.getUsername(); // JWT 발급용
    }
}
		