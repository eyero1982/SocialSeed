/*
 * Copyright 2011-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.social.seed.repository;

import com.social.seed.model.HashTag;
import com.social.seed.model.SocialUser;

import java.time.LocalDateTime;

public class TestUtils {

    /**
     * Creates a SocialUser object with the specified properties.
     *
     * @param userName   The username of the social user.
     * @param email      The email of the social user.
     * @param dateBorn   The date of birth of the social user in the format "yyyy-MM-dd'T'HH:mm:ss".
     * @param fullName   The full name of the social user.
     * @param language   The language preference of the social user.
     * @return A SocialUser object with the specified properties.
     */
    public static SocialUser createSocialUser(String userName, String email, String dateBorn, String fullName, String language) {
        return SocialUser.builder()
                .userName(userName)
                .email(email)
                .dateBorn(LocalDateTime.parse(dateBorn))
                .fullName(fullName)
                .language(language)
                .registrationDate(LocalDateTime.now())
                .isActive(true)
                .isDeleted(false)
                .onVacation(false)
                .followersCount(0)
                .friendCount(0)
                .followingCount(0)
                .friendRequestCount(0)
                .build();
    }

    /**
     * Creates a new HashTag instance with the given properties.
     *
     * @param id                    The unique identifier of the hashtag.
     * @param name                  The name of the hashtag.
     * @param socialUserInterestIn The count of social users interested in this hashtag.
     * @param postTaggedIn          The count of posts tagged with this hashtag.
     * @return A new HashTag instance with the specified properties.
     */
    public static HashTag createHashTag(String id, String name, int socialUserInterestIn, int postTaggedIn) {
        return HashTag.builder()
                .id(id)
                .name(name)
                .socialUserInterestIn(socialUserInterestIn)
                .postTaggedIn(postTaggedIn)
                .build();
    }
}
