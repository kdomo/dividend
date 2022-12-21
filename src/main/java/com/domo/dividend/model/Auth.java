package com.domo.dividend.model;

import com.domo.dividend.persist.entity.UserEntity;
import java.util.List;
import lombok.Data;

public class Auth {
    @Data
    public static class SignIn {
        private String username;
        private String password;
    }

    @Data
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles;

        public UserEntity toEntity() {
            return UserEntity.builder()
                    .username(username)
                    .password(password)
                    .roles(roles)
                    .build();
        }
    }

}
