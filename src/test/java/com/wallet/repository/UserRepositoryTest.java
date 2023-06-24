package com.wallet.repository;

import com.wallet.entity.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    private static final String EMAIL = "email@teste.com";

    @Autowired
    UserRepository userRepository;


    @Before
    public void setup() {
        User user = new User();
        user.setName("setupUser");
        user.setPassword("123");
        user.setEmail(EMAIL);

        userRepository.save(user);
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testSave() {
        User user = new User();
        user.setName("Test");
        user.setPassword("123456");
        user.setEmail("test@email.com");

        User response = userRepository.save(user);

        Assert.assertNotNull(response);
    }
    @Test
    public void testFindByEmail() {
        Optional<User> response = userRepository.findByEmailEquals(EMAIL);
        Assert.assertTrue(response.isPresent());
        Assert.assertEquals(response.get().getEmail(), EMAIL);
    }


}
