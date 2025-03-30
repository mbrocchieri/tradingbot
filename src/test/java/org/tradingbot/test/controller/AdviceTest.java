package org.tradingbot.test.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.advice.AdviceCategoryBean;
import org.tradingbot.advice.AdviceCategoryCreationBean;

@ExtendWith(MockitoExtension.class)
public class AdviceTest extends TradingBotApplicationTests {

    @Test
    void testAddAdviceCategory() throws Exception {
        AdviceCategoryBean res =
                adviceController.createCategory(new AdviceCategoryCreationBean("toto"));
        assertEquals("toto", res.getName());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adviceController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/advices/categories").
                contentType(MediaType.APPLICATION_JSON)).
                andDo(MockMvcResultHandlers.print());
    }
}
