package com.project.backend.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ResetPasswordVO {
    @Email
    @Length(min = 5)
    String email;
    @Length(min =6,max = 6)
    String code;
    @Length(min =6 ,max=20)
    String password;
}
