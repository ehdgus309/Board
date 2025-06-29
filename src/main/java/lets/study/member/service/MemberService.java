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
            throw new IllegalArgumentException("���̵�� 4~10���� �ҹ��ڿ� ���� �����̾�� �մϴ�.");
        }
        if (!password.matches("^[a-zA-Z0-9]{8,15}$")) {
            throw new IllegalArgumentException("��й�ȣ�� 8~15���� ��ҹ��ڿ� ���� �����̾�� �մϴ�.");
        }
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("�̹� �����ϴ� ���̵��Դϴ�.");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));
        memberRepository.save(member);
    }

    public String login(LoginRequestDto request) {
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("�������� �ʴ� ȸ���Դϴ�."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
        }

        return member.getUsername(); // JWT �߱޿�
    }
}
		