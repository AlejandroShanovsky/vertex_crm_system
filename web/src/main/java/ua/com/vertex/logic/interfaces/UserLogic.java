package ua.com.vertex.logic.interfaces;


import ua.com.vertex.beans.Role;
import ua.com.vertex.beans.User;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface UserLogic {

    List<String> getAllUserIds();

    List<User> getListUsers() throws SQLException;

    User getUserDetailsByID(int userId) throws SQLException;

    String convertImage(byte[] image);

    HashMap<Role, Role> getListAllRoles();

    int saveUserData(User user);

}
