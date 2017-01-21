package ua.com.vertex.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.vertex.beans.User;
import ua.com.vertex.dao.UserDaoImpl;
import ua.com.vertex.logic.interfaces.UserLogic;
import ua.com.vertex.utils.Storage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ua.com.vertex.beans.User.EMPTY_USER;

@Service
public class UserLogicImpl implements UserLogic {

    private final UserDaoImpl userDao;
    private final Storage storage;

    @Override
    public List<String> getAllUserIds() {
        return userDao.getAllUserIds().stream().map(id -> Integer.toString(id)).collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUserById(int id) {
        User user = userDao.getUser(id).orElse(EMPTY_USER);

        if (!EMPTY_USER.equals(user)) {
            user = imagesCheck(user);
        }

        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email).orElse(EMPTY_USER);

        if (!EMPTY_USER.equals(user)) {
            user = imagesCheck(user);
        }

        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> logIn(String email) {
        User user;
        if (email.isEmpty()) {
            user = EMPTY_USER;
        } else {
            user = userDao.logIn(email).orElse(EMPTY_USER);
        }

        return Optional.ofNullable(user);
    }

    @Override
    public User imagesCheck(User user) {
        if (user.getPhoto() != null) {
            storage.setPhoto(user.getPhoto());
            user.setPhoto(new byte[]{(byte) 1});
        }
        if (user.getPassportScan() != null) {
            storage.setPassportScan(user.getPassportScan());
            user.setPassportScan(new byte[]{(byte) 1});
        }

        return user;
    }

    @Override
    public void saveImage(int userId, byte[] image, String imageType) throws Exception {
        User user = userDao.saveImage(userId, image, imageType).orElse(EMPTY_USER);

        if (!EMPTY_USER.equals(user)) {
            imagesCheck(user);
        }
    }

    @Autowired
    public UserLogicImpl(UserDaoImpl userDao, Storage storage) {
        this.userDao = userDao;
        this.storage = storage;
    }
}
