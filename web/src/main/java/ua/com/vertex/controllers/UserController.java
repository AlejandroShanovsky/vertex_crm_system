package ua.com.vertex.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ua.com.vertex.beans.Certificate;
import ua.com.vertex.logic.interfaces.CertificateLogic;
import ua.com.vertex.utils.Aes;
import ua.com.vertex.utils.LogInfo;

import java.util.List;


@Controller
public class UserController {


    static final String CERTIFICATES = "certificates";
    static final String USER_JSP = "user";
    private static final String LIST_CERTIFICATE_IS_EMPTY = "listCertificatesIsEmpty";
    private static final String LOG_REQ_IN = "Request to '/getCertificateByUserId' with userEmail=";
    private static final String LOG_REQ_OUT = "Request to '/getCertificateByUserId' return 'user.jsp' ";
    private static final String KEY = "ArgentinaJamaica";


    private final LogInfo logInfo;
    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    private final CertificateLogic certificateLogic;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ModelAndView user() {
        return new ModelAndView(USER_JSP);
    }

    @RequestMapping(value = "/getCertificateByUserId", method = RequestMethod.GET)
    public String getAllCertificatesByUserEmail(Model model) {

        String eMail = logInfo.getEmail();
        LOGGER.debug(LOG_REQ_IN + eMail);
        List<Certificate> result = certificateLogic.getAllCertificatesByUserEmail(eMail);
        result.forEach(e -> e.setEncodedCertificationId(Aes.encrypt(String.valueOf(e.getCertificationId()), KEY)));
        model.addAttribute(CERTIFICATES, result);

        model.addAttribute(LIST_CERTIFICATE_IS_EMPTY, result.isEmpty());

        LOGGER.debug(LOG_REQ_OUT);

        return USER_JSP;
    }

    @Autowired
    public UserController(LogInfo logInfo, CertificateLogic certificateLogic) {
        this.logInfo = logInfo;
        this.certificateLogic = certificateLogic;
    }

}
