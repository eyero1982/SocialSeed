package com.social.seed.service;

import com.social.seed.model.SocialUser;
import com.social.seed.repository.SocialUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SocialUserService {
    @Autowired
    SocialUserRepository socialUserRepository;

    //region CRUD
    public Optional<SocialUser> getSocialUserById(String id) {
        return socialUserRepository.findById(id);
    }


    public ResponseEntity<Object> createNewSocialUser(SocialUser socialUser) {

        HttpStatus httpStatus;
        Object response;

        boolean existByEmail = socialUserRepository.existByEmail(socialUser.getEmail());
        boolean existByUserName = socialUserRepository.existByUserName(socialUser.getUserName());

        //si no existe el email y el username entonces se procede a crear el usuario
        if (!existByEmail && !existByUserName){
            //create the social user with the base data
            response = socialUserRepository.save(
                    SocialUser.builder()
                            .userName(socialUser.getUserName())
                            .email(socialUser.getEmail())
                            .dateBorn(socialUser.getDateBorn())
                            .fullName(socialUser.getFullName())
                            .language(socialUser.getLanguage())
                            .registrationDate(LocalDateTime.now())
                            .isActive(true)
                            .onVacation(false)
                            .followersCount(0)
                            .friendCount(0)
                            .followingCount(0)
                            .build()
            );

            httpStatus = HttpStatus.CREATED;
        }else {
            httpStatus = HttpStatus.CONFLICT;

            if (existByUserName && existByEmail){
                response = "The email and the username already exists";
            } else if (existByEmail) {
                response = String.format("The email [ %s ] already exists", socialUser.getEmail());
            } else {
                response = String.format("The username [ %s ] already exists", socialUser.getUserName());
            }
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    public HttpStatus deleteSocialUser(String userId, String id) {
        if (!userId.equals(id)) {
            return HttpStatus.CONFLICT;
        }

        return socialUserRepository.findById(id)
                .map(user -> {
                    socialUserRepository.deleteById(id);
                    return HttpStatus.OK;
                })
                .orElse(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<SocialUser> updateSocialUser(String userId, SocialUser newSocialUser) {

        if (!userId.equals(newSocialUser.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }else {
            Optional<SocialUser> socialUser = socialUserRepository.findById(newSocialUser.getId());

            if (socialUser.isPresent()){
                socialUserRepository.update(
                        newSocialUser.getId(),
                        newSocialUser.getFullName(),
                        newSocialUser.getDateBorn(),
                        newSocialUser.getLanguage()
                );

                SocialUser savedSocialUser = socialUserRepository.findById(userId).get();
                return ResponseEntity.status(HttpStatus.OK).body(savedSocialUser);
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }

    }
    //endregion

    //region Update Special Props
    public ResponseEntity<Object> updateSocialUserName(String idUserRequest, String idUserToUpdate, String newUserName) {
        if (idUserToUpdate.equals(idUserRequest)) {
            if (socialUserRepository.existsById(idUserToUpdate)){
                if (!socialUserRepository.existByUserName(newUserName)){
                    socialUserRepository.updateSocialUserName(idUserToUpdate, newUserName);
                    SocialUser savedSocialUser = socialUserRepository.findById(idUserToUpdate).get();
                    return ResponseEntity.status(HttpStatus.OK).body(savedSocialUser);
                }else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("The userName [ %s ] already exists", newUserName));
                }
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The user who is requesting the userName change is not the owner of this");
        }
    }

    public ResponseEntity<Object> updateSocialUserEmail(String idUserRequest, String idUserToUpdate, String newEmail) {
        if (idUserToUpdate.equals(idUserRequest)) {
            if (socialUserRepository.existsById(idUserToUpdate)){
                if (!socialUserRepository.existByEmail(newEmail)){
                    socialUserRepository.updateSocialUserEmail(idUserToUpdate, newEmail);
                    SocialUser savedSocialUser = socialUserRepository.findById(idUserToUpdate).get();
                    return ResponseEntity.status(HttpStatus.OK).body(savedSocialUser);
                }else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("The Email [ %s ] already exists", newEmail));
                }
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The user who is requesting the Email change is not the owner of this");
        }
    }
    //endregion

    //region FOLLOW
    @Transactional
    public ResponseEntity<Object> followSocialUser(String idUserRequest, String idUserToFollow) {
        if (idUserRequest.equals(idUserToFollow))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("the user to be followed cannot be the same");

        if (socialUserRepository.existsById(idUserRequest)){
            if (socialUserRepository.existsById(idUserToFollow)){
                if (!socialUserRepository.IsUserBFollowerOfUserA(idUserRequest, idUserToFollow)){
                    socialUserRepository.createUserBFollowUserA(
                            idUserRequest,
                            idUserToFollow,
                            LocalDateTime.now());

                        return ResponseEntity.status(HttpStatus.OK).body(null);
                }else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("You are already following the user with id [ %s ]", idUserToFollow));
                }
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The User to follow has not been found");
            }
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("The User with [ %s ] not found", idUserRequest));
        }
    }

    @Transactional
    public ResponseEntity<Object> unfollowSocialUser(String idUserRequest, String idUserToUnFollow) {
        if (idUserRequest.equals(idUserToUnFollow))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("the user to be unfollowed cannot be the same");

        if (socialUserRepository.existsById(idUserRequest)){
            if (socialUserRepository.existsById(idUserToUnFollow)){
                if (socialUserRepository.IsUserBFollowerOfUserA(idUserRequest, idUserToUnFollow)){
                    socialUserRepository.unFollowTheUserA(idUserRequest, idUserToUnFollow);

                    return ResponseEntity.status(HttpStatus.OK).body(null);
                }else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("You not following the user with id [ %s ]", idUserToUnFollow));
                }
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The User to unfollow has not been found");
            }
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("The User with [ %s ] not found", idUserRequest));
        }
    }
    //endregion
}
