package org.test.services;

import org.test.db.UserRepository;
import org.test.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Класс бросает Exception в случае, если есть проблемы с подключением к БД
@Service
public class BankService {

    @Autowired
    private UserRepository userRepository;

    private User createNewUser(String username) throws Exception
    {
        User user = new User();
        // что делаем, если у юзера нет денег
        user.setMoney(0);
        user.setUsername(username);
        boolean result = userRepository.createUser(user);
        if (!result) throw new Exception("User cant be created and money cant be setted, check DB connection!");

        return user;
    }
    public Integer getUserMoney(String username) throws Exception
    {
        User user = userRepository.findUserByUsername(username);
        if (user == null) user = createNewUser(username);

        return user.getMoney();
    }

    public void setUserMoney(String username, Integer money) throws Exception
    {
        User user = userRepository.findUserByUsername(username);
        if (user == null)
        {
            user = createNewUser(username);
        }

        user.setMoney(money);

        boolean result = userRepository.updateUser(user);
        if (!result) throw new Exception("Error! DB isnt available. Check the connection!");

    }
}
