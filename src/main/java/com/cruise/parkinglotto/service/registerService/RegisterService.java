package com.cruise.parkinglotto.service.registerService;

import com.cruise.parkinglotto.domain.Member;

public interface RegisterService {

    /**
     * 사용자가 등록 요청 하는 메서드
     * 사용자가 등록 요청 버튼을 누르면 -> enrollmentStatus가 null -> pending 으로 바뀐다.
     * 관리자는 애초에 pending, enrollment 상태의 사용자 목록만 관리한다.
     */
    Object requestRegister(Member member);

}
