package com.wallet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.dto.UserDTO;
import com.wallet.entity.User;
import com.wallet.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String EMAIL = "email@teste.com";
    public static final String PASSWORD = "123";
    public static final String NAME = "User Test";
    public static final String URL = "/users";

    @MockBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;

    @Before
    public void setUp(){
        BDDMockito.given(userService.save(Mockito.any(User.class))).willReturn(getMockUser());
    }

    @Test
    public void testSave() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .content(this.getJsonPayload())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    private User getMockUser() {
        User user = new User();
        user.setName(NAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);

        return user;
    }


    private String getJsonPayload() throws JsonProcessingException {
        UserDTO dto = new UserDTO();
        dto.setName(NAME);
        dto.setPassword(PASSWORD);
        dto.setEmail(EMAIL);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(dto);
    }


}
