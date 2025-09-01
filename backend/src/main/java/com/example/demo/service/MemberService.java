package com.example.demo.service;

import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public Member addMember(Member member) {
        return memberRepository.save(member);
    }

    public Member findMemberById(UUID memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    public void removeMember(UUID memberId) {
        memberRepository.deleteById(memberId);
    }
}