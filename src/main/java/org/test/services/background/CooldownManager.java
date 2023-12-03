package org.test.services.background;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.implementation.CooldownDAO;
import org.test.entity.CommandID;
import org.test.entity.Cooldown;
import org.test.entity.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CooldownManager {
    @Autowired
    private CooldownDAO cooldownDAO;
    public void setCooldown(Long userID, CommandID commandID, LocalDateTime endTime) throws SQLException {
        Optional<Cooldown> cooldownOptional = cooldownDAO.getByCommandIDAndUserID(commandID, userID);
        if (cooldownOptional.isPresent())
        {
            Cooldown cooldown = cooldownOptional.get();
            cooldown.setEndTime(endTime);
            cooldownDAO.update(cooldown);
        }
        else {
            Cooldown cooldown = Cooldown.builder()
                    .commandID(commandID)
                    .user(User.builder().id(userID).build())
                    .endTime(endTime)
                    .build();
            cooldownDAO.add(cooldown);
        }
    }

    public Optional<Cooldown> getByCommandAndUserID(Long userID, CommandID commandID) throws SQLException {
        return cooldownDAO.getByCommandIDAndUserID(commandID, userID);
    }

    public boolean isCooldownActive(Long userID, CommandID commandID) throws SQLException {
        Optional<Cooldown> cooldownOptional = cooldownDAO.getByCommandIDAndUserID(commandID, userID);
        if (cooldownOptional.isPresent())
        {
            Cooldown cooldown = cooldownOptional.get();
            return LocalDateTime.now().isBefore(cooldown.getEndTime());
        }
        return false;
    }
}
