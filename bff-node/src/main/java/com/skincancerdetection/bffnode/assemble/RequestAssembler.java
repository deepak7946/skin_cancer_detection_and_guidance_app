package com.skincancerdetection.bffnode.assemble;

import com.skincancerdetection.bffnode.model.*;
import com.skincancerdetection.bffnode.utils.AESEncryptionDecryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestAssembler {

    @Autowired
    private AESEncryptionDecryption encryptionDecryption;

    public UserInfoRequestDto assembleUserInfoRequestDto(AuthenticationRequest request) {
        UserInfoRequestDto userInfoRequestDto = new UserInfoRequestDto();
        userInfoRequestDto.setUser_id(request.getUsername());
        return userInfoRequestDto;
    }

    public UserDetailsDto assembleUserDetailsDto(RegistrationRequest request) {
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setDob(request.getDob());
        userDetailsDto.setFirst_name(request.getFirstname());
        userDetailsDto.setLast_name(request.getLastname());
        final String encypted = encryptionDecryption.encrypt(request.getPassword());
        userDetailsDto.setPassword(encypted);
        userDetailsDto.setUser_id(request.getUsername());
        userDetailsDto.setGender(request.getGender());
        return userDetailsDto;
    }

    public AuthenticationResponse assembleAuthenticationResponse(UserInfoResponseDto userInfoResponseDto
            , String username) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setFirstname(userInfoResponseDto.getFirst_name());
        response.setLastname(userInfoResponseDto.getLast_name());
        response.setUsername(username);
        return response;
    }
}
