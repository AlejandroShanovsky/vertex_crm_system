package ua.com.vertex.dao.interfaces;

import org.springframework.dao.DataAccessException;
import ua.com.vertex.beans.Course;
import ua.com.vertex.utils.DataNavigator;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CourseDaoInf {

    int addCourse(Course course) throws SQLException;

    Optional<Course> getCourseById(int courseId);

    List<Course> getAllCourses(DataNavigator dataNavigator);

    int getQuantityCourses() throws SQLException;

    List<Course> getAllCoursesWithDept();

    List<Course> getAllCoursesWithDept() throws DataAccessException;

    List<Course> searchCourseByNameAndStatus(Course course) throws DataAccessException;

    int updateCourseExceptPrice(Course course) throws DataAccessException;

    Optional<Course> getCourseById(int courseId) throws DataAccessException;

}
