package com.skincancerdetection.bffnode.router;

import com.skincancerdetection.bffnode.enums.ErrorEnum;
import com.skincancerdetection.bffnode.exception.BffNodeException;
import com.skincancerdetection.bffnode.exception.DuplicateEntryException;
import com.skincancerdetection.bffnode.model.CommonResponse;
import com.skincancerdetection.bffnode.model.FileUploadDto;
import com.skincancerdetection.bffnode.model.UserDetailsDto;
import com.skincancerdetection.bffnode.model.UserInfoRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CommonServiceRouterImpl implements CommonServiceRouter{

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceRouterImpl.class);

    @Value("${common.service.url:http://localhost:5000}")
    private String commonServiceUrl;

    @Value("${common.service.user.registration.endpoint}")
    private String userRegistrationEndpoint;

    @Value("${common.service.user.retrieve.endpoint}")
    private String userRetrieveEndpoint;

    @Value("${common.service.questionnaire.endpoint}")
    private String questionnaireEndpoint;

    @Value("${common.service.doctors.endpoint}")
    private String doctorsEndpoint;

    @Value("${common.service.user.document.upload.endpoint}")
    private String docUploadEndpoint;

    @Value("${common.service.user.documents.endpoint}")
    private String userDocEndpoint;

    @Value("${common.service.user.document.download.endpoint}")
    private String userDocDownloadEndpoint;

    @Value("${common.service.user.document.delete.endpoint}")
    private String userDocDeleteEndpoint;

    @Value("${common.service.cancer.type.info.endpoint}")
    private String typeInfoEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public CommonResponse registerUser(UserDetailsDto userDetailsDto) {

        final String url = new StringBuilder(commonServiceUrl).append(userRegistrationEndpoint).toString();
        ResponseEntity<CommonResponse> responseEntity = null;

        try {
            responseEntity = restTemplate
                    .postForEntity(url, userDetailsDto, CommonResponse.class);

           if (responseEntity.getStatusCode().value()!= HttpStatus.OK.value()) {
                throw new BffNodeException(responseEntity.getStatusCode().getReasonPhrase()
                        , ErrorEnum.COMMON_SERVICE_ERROR.getErrMessage()
                        , new RuntimeException());

            }

        } catch (HttpClientErrorException e) {
            LOGGER.error(e.getMessage());

            if (e.getStatusCode().value()== HttpStatus.CONFLICT.value()) {
                throw new DuplicateEntryException(e.getStatusCode().getReasonPhrase()
                        , ErrorEnum.USER_EXISTS_FOUND.getErrMessage()
                        , new RuntimeException());

            }

            throw new BffNodeException(e.getMessage(), e);
        }
        return responseEntity.getBody();

    }

    @Override
    public CommonResponse retrieveUser(UserInfoRequestDto userInfoRequestDto) {
        final String url = new StringBuilder(commonServiceUrl).append(userRetrieveEndpoint).toString();
        ResponseEntity<CommonResponse> responseEntity = null;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_id", userInfoRequestDto.getUser_id());

        try {
            responseEntity = restTemplate
                    .getForEntity(builder.toUriString(), CommonResponse.class);

            if (responseEntity.getStatusCode().value()!= HttpStatus.OK.value()) {
                throw new BffNodeException(responseEntity.getStatusCode().getReasonPhrase()
                        , ErrorEnum.COMMON_SERVICE_ERROR.getErrMessage()
                        , new RuntimeException());

            }

        } catch (HttpClientErrorException e) {
            LOGGER.error(e.getMessage());
            throw new BffNodeException(e.getMessage(), ErrorEnum.USER_NOT_FOUND.getErrMessage(), e);
        }
        return responseEntity.getBody();

    }

    @Override
    public CommonResponse getQuestionnaire() {
        final String url = new StringBuilder(commonServiceUrl).append(questionnaireEndpoint).toString();
        ResponseEntity<CommonResponse> responseEntity = null;
        try {
            responseEntity = restTemplate
                    .getForEntity(url, CommonResponse.class);
            if (responseEntity.getStatusCode().value()!= HttpStatus.OK.value()) {
                throw new BffNodeException(responseEntity.getStatusCode().getReasonPhrase()
                        , ErrorEnum.COMMON_SERVICE_ERROR.getErrMessage()
                        , new RuntimeException());

            }

        } catch (HttpClientErrorException e) {
            LOGGER.error(e.getMessage());
            throw new BffNodeException(e.getMessage(), e);
        }
        return responseEntity.getBody();
    }

    @Override
    public CommonResponse getDoctors(double longitude, double latitude) {
        final String url = new StringBuilder(commonServiceUrl).append(doctorsEndpoint).toString();
        ResponseEntity<CommonResponse> responseEntity = null;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("latitude",latitude)
                .queryParam("longitude", longitude);

        LOGGER.info("latitude: " +  latitude + ", longitude: " + longitude);

        try {
            responseEntity = restTemplate
                    .getForEntity(builder.toUriString(), CommonResponse.class);
            if (responseEntity.getStatusCode().value()!= HttpStatus.OK.value()) {
                throw new BffNodeException(responseEntity.getStatusCode().getReasonPhrase()
                        , ErrorEnum.COMMON_SERVICE_ERROR.getErrMessage()
                        , new RuntimeException());

            }

        } catch (HttpClientErrorException e) {
            LOGGER.error(e.getMessage());
            throw new BffNodeException(e.getMessage(), e);
        }
        return responseEntity.getBody();
    }

    @Override
    public CommonResponse uploadDocument(FileUploadDto fileUploadDto) {
        final String url = new StringBuilder(commonServiceUrl).append(docUploadEndpoint).toString();
        ResponseEntity<CommonResponse> responseEntity = null;

        try {
            responseEntity = restTemplate
                    .postForEntity(url, fileUploadDto, CommonResponse.class);

            if (responseEntity.getStatusCode().value()!= HttpStatus.OK.value()) {
                throw new BffNodeException(responseEntity.getStatusCode().getReasonPhrase()
                        , ErrorEnum.COMMON_SERVICE_ERROR.getErrMessage()
                        , new RuntimeException());

            }

        } catch (HttpClientErrorException e) {
            LOGGER.error(e.getMessage());

            throw new BffNodeException(e.getMessage(), e);
        }
        return responseEntity.getBody();


    }

    @Override
    public CommonResponse getUserDocuments(String username) {
        final String url = new StringBuilder(commonServiceUrl).append(userDocEndpoint).toString();
        ResponseEntity<CommonResponse> responseEntity = null;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("userId",username);

        try {
            responseEntity = restTemplate
                    .getForEntity(builder.toUriString(), CommonResponse.class);

            if (responseEntity.getStatusCode().value()!= HttpStatus.OK.value()) {
                throw new BffNodeException(responseEntity.getStatusCode().getReasonPhrase()
                        , ErrorEnum.COMMON_SERVICE_ERROR.getErrMessage()
                        , new RuntimeException());

            }

        } catch (HttpClientErrorException e) {
            LOGGER.error(e.getMessage());

            throw new BffNodeException(e.getMessage(), e);
        }
        return responseEntity.getBody();
    }

    @Override
    public CommonResponse getFile(String username, String filename) {
        final String url = new StringBuilder(commonServiceUrl).append(userDocDownloadEndpoint).toString();
        ResponseEntity<CommonResponse> responseEntity = null;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("userId",username)
                .queryParam("filename",filename);

        try {
            responseEntity = restTemplate
                    .getForEntity(builder.toUriString(), CommonResponse.class);

            if (responseEntity.getStatusCode().value()!= HttpStatus.OK.value()) {
                throw new BffNodeException(responseEntity.getStatusCode().getReasonPhrase()
                        , ErrorEnum.COMMON_SERVICE_ERROR.getErrMessage()
                        , new RuntimeException());

            }

        } catch (HttpClientErrorException e) {
            LOGGER.error(e.getMessage());

            throw new BffNodeException(e.getMessage(), e);
        }
        return responseEntity.getBody();
    }

    @Override
    public void deleteDocument(String username, String filename) {
        final String url = new StringBuilder(commonServiceUrl).append(userDocDeleteEndpoint).toString();
        ResponseEntity<CommonResponse> responseEntity = null;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("userId",username)
                .queryParam("filename",filename);

        try {
           restTemplate.delete(builder.toUriString());
        } catch (HttpClientErrorException e) {
            LOGGER.error(e.getMessage());

            throw new BffNodeException(e.getMessage(), e);
        }
    }

    @Override
    public CommonResponse getCancerDetails(String type) {
        final String url = new StringBuilder(commonServiceUrl)
                .append(typeInfoEndpoint)
                .append(type).toString();
        ResponseEntity<CommonResponse> responseEntity = null;

        try {
            responseEntity = restTemplate
                    .getForEntity(url, CommonResponse.class);

            if (responseEntity.getStatusCode().value()!= HttpStatus.OK.value()) {
                throw new BffNodeException(responseEntity.getStatusCode().getReasonPhrase()
                        , ErrorEnum.COMMON_SERVICE_ERROR.getErrMessage()
                        , new RuntimeException());

            }

        } catch (HttpClientErrorException e) {
            LOGGER.error(e.getMessage());
            throw new BffNodeException(e.getMessage(), e);
        }
        return responseEntity.getBody();
    }
}
