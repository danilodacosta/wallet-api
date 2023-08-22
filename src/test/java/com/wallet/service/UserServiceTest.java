package com.wallet.service;

import com.wallet.entity.User;
import com.wallet.repository.UserRepository;
import com.wallet.service.impl.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final String EMAIL = "email@teste.com";
    public static final String PASSWORD = "123456";
    public static final String NAME = "User Test";
    public static final Long ID = 1L;

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
     }
    @Test
    public void testeFindByEmail() {

        when(repository.findByEmailEquals(EMAIL)).thenReturn(Optional.of(new User()));

        Optional<User> user = userService.findByEmail(EMAIL);
        Assert.assertTrue(user.isPresent());
    }

    @Test
    public void testeSave() {

        when(repository.save(Mockito.any(User.class))).thenReturn(getMockUser());

        User user = getMockUser();
        user.setId(null);

        User expectedUser = userService.save(user);
        Assert.assertNotNull(expectedUser);
    }

    private User getMockUser() {
        User user = new User();
        user.setId(ID);
        user.setName(NAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);

        return user;
    }

}
