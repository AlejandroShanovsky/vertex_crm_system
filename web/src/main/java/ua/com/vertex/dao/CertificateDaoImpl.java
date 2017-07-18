package ua.com.vertex.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ua.com.vertex.beans.Certificate;
import ua.com.vertex.dao.interfaces.CertificateDaoInf;
import ua.com.vertex.utils.LogInfo;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CertificateDaoImpl implements CertificateDaoInf {

    private static final String USER_ID = "userId";
    private static final String USER_EMAIL = "userEmail";
    private static final String CERTIFICATE_ID = "certificateId";
    private static final String CERTIFICATE_UID = "certificateUid";
    private static final String CERTIFICATION_DATE = "certificationDate";
    private static final String COURSE_NAME = "courseName";
    private static final String LANGUAGE = "language";
    private static final String COLUMN_CERTIFICATE_ID = "certification_id";
    private static final String COLUMN_CERTIFICATE_UID = "certificate_uid";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_CERTIFICATION_DATE = "certification_date";
    private static final String COLUMN_COURSE_NAME = "course_name";
    private static final String COLUMN_LANGUAGE = "language";

    private static final Logger LOGGER = LogManager.getLogger(CertificateDaoImpl.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final LogInfo logInfo;

    @Override
    public List<Certificate> getAllCertificatesByUserEmail(String eMail) throws DataAccessException {

        String query = "SELECT c.certification_id, c.certificate_uid, c.user_id, c.certification_date, c.course_name " +
                "FROM Certificate c INNER JOIN  Users u ON c.user_id = u.user_id WHERE email = :userEmail";

        LOGGER.debug("Retrieved all certificates by eMail=" + eMail);

        return jdbcTemplate.query(query, new MapSqlParameterSource(USER_EMAIL, eMail),
                (resultSet, i) -> new Certificate.Builder()
                        .setCertificationId(resultSet.getInt(COLUMN_CERTIFICATE_ID))
                        .setCertificateUid(String.valueOf(resultSet.getLong(COLUMN_CERTIFICATE_UID)))
                        .setUserId(0)
                        .setCertificationDate(resultSet.getDate(COLUMN_CERTIFICATION_DATE).toLocalDate())
                        .setCourseName(resultSet.getString(COLUMN_COURSE_NAME))
                        .setLanguage(null)
                        .getInstance());
    }

    private MapSqlParameterSource addParameterToMapSqlParameterSourceFromCertificate(Certificate certificate) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue(USER_ID, certificate.getUserId());
        source.addValue(CERTIFICATION_DATE, Date.valueOf(certificate.getCertificationDate()));
        source.addValue(COURSE_NAME, certificate.getCourseName());
        source.addValue(LANGUAGE, certificate.getLanguage());
        source.addValue(CERTIFICATE_UID, Long.parseLong(certificate.getCertificateUidWithoutDashes()));
        return source;
    }

    @Override
    public int addCertificate(Certificate certificate) throws DataAccessException {
        String query = "INSERT INTO Certificate (user_id, certification_date, course_name, language, certificate_uid)" +
                "VALUES ( :userId, :certificationDate, :courseName, :language, :certificateUid)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(query, addParameterToMapSqlParameterSourceFromCertificate(certificate), keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<Certificate> getAllCertificatesByUserIdFullData(int userId) throws DataAccessException {

        String query = "SELECT certification_id, certificate_uid, user_id, certification_date, course_name, language "
                + "FROM Certificate WHERE user_id =:userId";

        LOGGER.debug("Retrieved all certificates by id=" + userId);

        return jdbcTemplate.query(query, new MapSqlParameterSource(USER_ID, userId), new CertificateRowMapper());
    }

    @Override
    public Optional<Certificate> getCertificateById(int certificateId) throws DataAccessException {
        String query = "SELECT certification_id, certificate_uid, user_id, certification_date, course_name, language "
                + "FROM Certificate WHERE certification_id =:certificateId";

        Certificate certificate = null;
        try {
            certificate = jdbcTemplate.queryForObject(query,
                    new MapSqlParameterSource(CERTIFICATE_ID, certificateId), new CertificateRowMapper());
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug(logInfo.getId() + "No certificate in DB, id=" + certificateId);
        }

        if (certificate != null) {
            LOGGER.debug(logInfo.getId() + "Retrieved certificate id=" + certificateId);
        }

        return Optional.ofNullable(certificate);
    }

    @Override
    public Optional<Certificate> getCertificateByUid(String certificateUid) throws DataAccessException {
        String query = "SELECT certification_id, certificate_uid, user_id, certification_date, course_name, language "
                + "FROM Certificate WHERE certificate_uid =:certificateUid";

        Certificate certificate = null;
        try {
            certificate = jdbcTemplate.queryForObject(query,
                    new MapSqlParameterSource(CERTIFICATE_UID, certificateUid), new CertificateRowMapper());
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug(logInfo.getId() + "No certificate in DB, UID=" + certificateUid);
        }

        if (certificate != null) {
            LOGGER.debug(logInfo.getId() + "Retrieved certificate UID=" + certificateUid);
        }

        return Optional.ofNullable(certificate);
    }

    private static final class CertificateRowMapper implements RowMapper<Certificate> {
        public Certificate mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Certificate.Builder()
                    .setCertificationId(resultSet.getInt(COLUMN_CERTIFICATE_ID))
                    .setCertificateUid(resultSet.getString(COLUMN_CERTIFICATE_UID))
                    .setUserId(resultSet.getInt(COLUMN_USER_ID))
                    .setCertificationDate(resultSet.getDate(COLUMN_CERTIFICATION_DATE).toLocalDate())
                    .setCourseName(resultSet.getString(COLUMN_COURSE_NAME))
                    .setLanguage(resultSet.getString(COLUMN_LANGUAGE))
                    .getInstance();
        }
    }

    @Autowired
    public CertificateDaoImpl(DataSource dataSource, LogInfo logInfo) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.logInfo = logInfo;
    }
}
