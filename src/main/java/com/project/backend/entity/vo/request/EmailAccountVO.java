package com.project.backend.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailAccountVO {
    @Length(min = 1,max = 10)
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    String username;
    @Length(min=6,max = 20)
    String password;
    @Email
    @Length(min=5)
    String email;
    @Length(min = 6,max = 6)
    String code;
}
