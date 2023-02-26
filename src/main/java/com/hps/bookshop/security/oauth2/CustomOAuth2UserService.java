package com.hps.bookshop.security.oauth2;

import com.hps.bookshop.entity.AuthProvider;
import com.hps.bookshop.entity.RoleType;
import com.hps.bookshop.entity.StatusAccount;
import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.exception.NotMatchException;
import com.hps.bookshop.model.Role;
import com.hps.bookshop.model.User;
import com.hps.bookshop.repository.RoleRepository;
import com.hps.bookshop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        System.out.println(userRequest.getClientRegistration().getRegistrationId());
        System.out.println(oAuth2User.getAttributes());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (oAuth2UserInfo.getEmail().isEmpty()) {
            throw new NotFoundException("Email not found from OAuth2 provider");
        }
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(AuthProvider.valueOf(
                    userRequest.getClientRegistration().getRegistrationId().toUpperCase()))) {
                throw new NotMatchException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userRequest, oAuth2UserInfo);
        }
        System.out.println(user);
        return UserPrincipal.build(user, oAuth2User.getAttributes());
    }

    @Transactional
    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        if (!oAuth2UserInfo.getImageUrl().isEmpty()) {
            user.setImgUrl(oAuth2UserInfo.getImageUrl());
        }
        user.setProvider(AuthProvider.valueOf(
                oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setStatus(StatusAccount.ACTIVATED);
        user.setCreatedDate(new Date());
        Role role = roleRepository.findByName(RoleType.CUSTOMER).get();
        user.addRole(role);
        return userRepository.save(user);
    }

    @Transactional
    private User updateExistingUser(User user, OAuth2UserInfo oAuth2UserInfo) {
        user.setName(oAuth2UserInfo.getName());
        if(user.getImgName() == null) {
            user.setImgUrl(oAuth2UserInfo.getImageUrl());
        }
        return userRepository.save(user);
    }
}
