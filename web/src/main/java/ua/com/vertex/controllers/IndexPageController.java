package ua.com.vertex.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.vertex.utils.Storage;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexPageController {

    private final Storage storage;

    private static final Logger LOGGER = LogManager.getLogger(IndexPageController.class);

    private static final String LOG_SESSION_START = "Session start";
    private static final String LOG_ENTRY = " page entered";

    private static final String ERROR = "error";
    private static final String INDEX = "index";

    @RequestMapping(value = "/")
    public String showIndexPage(HttpServletRequest request) {
        String view = INDEX;
        try {
            if (storage.getSessionId() == null && storage.getCount() > 2) {
                storage.setSessionId(request.getSession().getId());
                LOGGER.info(storage.getId() + LOG_SESSION_START);
            }
            if (request.getUserPrincipal() != null && storage.getEmail() == null) {
                storage.setEmail(request.getUserPrincipal().getName());
            }

            LOGGER.debug(storage.getId() + INDEX + LOG_ENTRY);

        } catch (Throwable t) {
            LOGGER.error(storage.getId(), t, t);
            view = ERROR;
        }

        return view;
    }

    @Autowired
    public IndexPageController(Storage storage) {
        this.storage = storage;
    }
}