package ua.com.vertex.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;
import ua.com.vertex.beans.User;
import ua.com.vertex.context.TestConfig;
import ua.com.vertex.logic.interfaces.CertificateLogic;
import ua.com.vertex.logic.interfaces.UserLogic;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

//import ua.com.vertex.context.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")

public class UserDetailsControllerTest {
    @Autowired
    private WebApplicationContext context;

    @Mock
    private UserLogic logic;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private CertificateLogic certificateLogic;

    private UserDetailsController userDetailsController;

    @Mock
    ModelAndView modelAndView;

    @Mock
    BindingResult bindingResult;

    private User user;
    private Optional<User> optional;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userDetailsController = new UserDetailsController(logic, certificateLogic);
        user = new User.Builder().getInstance();
        optional = Optional.ofNullable(user);
    }

    @Test
    public void userDetailsControllerReturnedPassViewTest() throws Exception {
        when(logic.getUserDetailsByID(1)).thenReturn(optional);
        MockMvc mockMvc = standaloneSetup(userDetailsController)
                .setSingleView(new InternalResourceView("userDetails"))
                .build();
        mockMvc.perform(get("/userDetails")
                .param("userId", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(view().name("userDetails"));
    }

    @Test
    public void userDetailsControllerReturnedFailViewTest() throws Exception {
        when(logic.getUserDetailsByID(-5)).thenThrow(new SQLException());
        MockMvc mockMvc = standaloneSetup(userDetailsController)
                .setSingleView(new InternalResourceView("error"))
                .build();
        mockMvc.perform(get("/userDetails")
                .param("userId", String.valueOf(-5)))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    public void viewAllUsersControllerCheckDataTest() throws Exception {
        String passportScan = "passportScan";
        byte[] passportScanByte = passportScan.getBytes();

        String photo = "photo";
        byte[] photoByte = photo.getBytes();

        User testUser = new User();
        testUser.setUserId(1);
        testUser.setEmail("chewed.mole@gmail.com");
        testUser.setLastName("Bond");
        testUser.setFirstName("James");
        testUser.setPassportScan(passportScanByte);
        testUser.setPhoto(photoByte);
        testUser.setDiscount(10);
        testUser.setPhone("0000000000");

        when(logic.getUserDetailsByID(1)).thenReturn(Optional.ofNullable(testUser));
        Assert.assertNotNull(testUser);

        Assert.assertEquals(1, testUser.getUserId());
        Assert.assertEquals("chewed.mole@gmail.com", testUser.getEmail());
        Assert.assertEquals("Bond", testUser.getLastName());
        Assert.assertEquals("James", testUser.getFirstName());
        Assert.assertEquals("passportScan", new String(testUser.getPassportScan()));
        Assert.assertEquals("photo", new String(testUser.getPhoto()));
        Assert.assertEquals(10, testUser.getDiscount());
        Assert.assertEquals("0000000000", testUser.getPhone());
    }

    @Test
    @WithMockUser
    public void getUserDetailsByIDRequestingWrongId() throws Exception {
        String passportScan = "passportScan";
        byte[] passportScanByte = passportScan.getBytes();

        String photo = "photo";
        byte[] photoByte = photo.getBytes();

        user.setUserId(1);
        user.setEmail("chewed.mole@gmail.com");
        user.setLastName("Bond");
        user.setFirstName("James");
        user.setPassportScan(passportScanByte);
        user.setPhoto(photoByte);
        user.setDiscount(10);
        user.setPhone("0000000000");

        when(logic.getUserDetailsByID(-1)).thenReturn(Optional.empty());

        Optional<User> optional = logic.getUserDetailsByID(-1);
        assertEquals(null, optional.orElse(null));
    }

    @Test
    public void userDetailsControllerSaveUserDataReturnedPassViewTest() throws Exception {

//        MockMvc mockMvc = standaloneSetup(userDetailsController)
//                .setSingleView(new InternalResourceView("userDetails")).build();
        MockMultipartFile passportScan = new MockMultipartFile("data", "fakePassportScan.png", "image/jpeg", "fakePassportScan".getBytes());
        MockMultipartFile photo = new MockMultipartFile("data", "fakePhoto.png", "image/jpeg", "fakePhoto".getBytes());

//        User user = new User();
//        user.setUserId(1);
//        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/saveUserData")
//                .file(passportScan)
//                .file(photo)
//                .flashAttr("user", user))
//                .andExpect(status().isOk())
//                .andExpect(model().size(4))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("allRoles"))
//                .andExpect(model().attributeExists("certificates"));

//        mockMvc.perform(post("/saveUserData"))
//                .andExpect(status().isOk())
//                .andExpect(model().size(4))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("allRoles"))
//                .andExpect(model().attributeExists("certificates"));

        MockMvc mockMvc = standaloneSetup(new ViewAllCoursesController(logic))
                .setSingleView(new InternalResourceView("userDetails"))
                .build();
        mockMvc.perform(get("/userDetails"))
                .andExpect(status().isOk())
                .andExpect(view().name("userDetails"))
                .andExpect(forwardedUrl("userDetails"))
                .andExpect(model().attributeExists("user"))
                //.andExpect(model().attribute("courses", hasSize(2)))
                //.andExpect(model().attribute("courses", hasItem(allOf(hasProperty("id", is(1))))))
                //.andExpect(model().attribute("courses", hasItem(allOf(hasProperty("id", is(2))))))

                .andExpect(model().attributeExists("allRoles"))
//                .andExpect(model().attribute("viewAllCourses", hasProperty("currentNamePage", is("viewAllCourses"))))
//                .andExpect(model().attribute("viewAllCourses", hasProperty("currentNumberPage", is(1))))
//                .andExpect(model().attribute("viewAllCourses", hasProperty("nextPage", is(1))))
//                .andExpect(model().attribute("viewAllCourses", hasProperty("lastPage", is(1))))
//                .andExpect(model().attribute("viewAllCourses", hasProperty("currentRowPerPage", is(25))))
//                .andExpect(model().attribute("viewAllCourses", hasProperty("quantityPages", is(1))))
//                .andExpect(model().attribute("viewAllCourses", hasProperty("dataSize", is(2))))
        ;
    }


}
