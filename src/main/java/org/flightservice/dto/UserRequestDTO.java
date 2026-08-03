package org.flightservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    @NotNull(message = "Name can not be null")
    private String name;
    @NotNull
    @Email
    private String email;
    @NotNull(message = "Password can not be null")
    private String password;
}
