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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String EMAIL = "email@teste.com";
    public static final String PASSWORD = "123456";
    public static final String NAME = "User Test";
    public static final Long ID = 1L;
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
                .content(this.getJsonPayload(ID, EMAIL, NAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(ID))
                .andExpect(jsonPath("$.data.name").value(NAME))
                .andExpect(jsonPath("$.data.email").value(EMAIL))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    public void testSaveInvalidUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(this.getJsonPayload(ID, "email", NAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Email inválido"));
    }

    private User getMockUser() {
        User user = new User();
        user.setId(ID);
        user.setName(NAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);

        return user;
    }


    private String getJsonPayload(Long id, String email, String name, String password) throws JsonProcessingException {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setPassword(password);
        dto.setEmail(email);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(dto);
    }


}
