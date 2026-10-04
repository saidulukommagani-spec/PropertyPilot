package com.propertypilot.web.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(TestController.class)
@AutoConfigureMockMvc(addFilters = false)
class TestControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
void currentUser_ShouldReturnSuccess()
        throws Exception {

    mockMvc.perform(
                    get("/api/v1/test/me"))
            .andDo(result -> {

                System.out.println(
                        "STATUS = "
                                + result.getResponse().getStatus());

                System.out.println(
                        "BODY = "
                                + result.getResponse()
                                .getContentAsString());
            });
}

    
}