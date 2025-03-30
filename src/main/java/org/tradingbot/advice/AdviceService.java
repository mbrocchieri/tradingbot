package org.tradingbot.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdviceService {
    @Autowired
    AdviceRepository adviceRepository;

    @Transactional
    public void updateToExecuted(int adviceId) {
        adviceRepository.setExecuted(adviceId);
    }

    @Transactional
    public void deleteAdvice(int adviceId) {
        adviceRepository.setDeleted(adviceId);
    }
}
