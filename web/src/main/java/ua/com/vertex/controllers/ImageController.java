package ua.com.vertex.controllers;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import ua.com.vertex.beans.User;
import ua.com.vertex.logic.interfaces.UserLogic;

import static ua.com.vertex.controllers.exceptionHandling.GlobalExceptionHandler.ERROR_MESSAGE;

@Controller
public class ImageController {
    private static final Logger LOGGER = LogManager.getLogger(ImageController.class);
    private static final String IMAGE = "image";
    private static final String IMAGE_PHOTO = "imagePhoto";
    private static final String IMAGE_PASSPORT = "imagePassport";
    private static final String IMAGE_TYPE = "imageType";
    private static final String USER = "user";
    private static final String INDEX = "index";
    private static final String IMAGE_ERROR = "imageError";

    private final UserLogic userLogic;

    @PostMapping(value = "/showImagePhoto")
    public String showImagePhoto(@ModelAttribute(USER) User user,
                                 @RequestParam(IMAGE_TYPE) String imageType, Model model) {

        LOGGER.debug(IMAGE_PHOTO + " page accessed");
        String encodedImage = encode(user.getEmail(), imageType);
        model.addAttribute(imageType, encodedImage);
        model.addAttribute(USER, user);
        LOGGER.debug("Passing imagePhoto to JSP");

        return IMAGE_PHOTO;
    }

    @PostMapping(value = "/showImagePassport")
    @PreAuthorize("(principal.username).equals(#user.email) || hasRole('ADMIN')")
    public String showImagePassport(@ModelAttribute(USER) User user,
                                    @RequestParam(IMAGE_TYPE) String imageType, Model model) {

        LOGGER.debug(IMAGE_PASSPORT + " page accessed");
        String encodedImage = encode(user.getEmail(), imageType);
        model.addAttribute(imageType, encodedImage);
        model.addAttribute(USER, user);
        LOGGER.debug("Passing imagePassport to JSP");

        return IMAGE_PASSPORT;
    }

    private String encode(String email, String imageType) {
        return Base64.encodeBase64String(userLogic.getImage(email, imageType).orElse(new byte[]{}));
    }

    @PostMapping(value = "/uploadImage")
    @PreAuthorize("(principal.username).equals(#user.email) || hasRole('ADMIN')")
    public String uploadImage(@ModelAttribute(USER) User user,
                              @RequestPart(value = IMAGE) MultipartFile file,
                              @RequestParam(IMAGE_TYPE) String imageType, Model model) {
        String view;

        if (file.isEmpty()) {
            view = IMAGE_ERROR;
            model.addAttribute(ERROR_MESSAGE, "You did not select any image!");
            LOGGER.debug("no image selected");
        } else {
            userLogic.saveImage(user.getEmail(), file, imageType);
            view = INDEX;
        }
        return view;
    }

    @Autowired
    public ImageController(UserLogic userLogic) {
        this.userLogic = userLogic;
    }
}
