package ua.com.vertex.logic.interfaces;

import ua.com.vertex.beans.Course;
import ua.com.vertex.utils.DataNavigator;

import java.util.List;

public interface CourseLogic {
    List<Course> getCoursesPerPages(DataNavigator dataNavigator);
}
