package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(message = "UserDto. Field: name не может быть пустым или содержать только пробелы")
    @Size(max = 255, min = 2)
    private String name;
    @NotBlank(message = "UserDto. Field: email не может быть пустым или содержать только пробелы")
    @Email(message = "UserDto. Field: email не соответствует формату электронной почты")
    @Size(max = 254, min = 6)
    private String email;
}
