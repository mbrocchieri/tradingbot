package org.tradingbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.tradingbot.strategy.ErrorResponse;

@RestController
public abstract class AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> error(Throwable t) {
        LOG.error(t.getMessage(), t);
        return ResponseEntity.internalServerError().body(new ErrorResponse(t.getMessage()));
    }
}
