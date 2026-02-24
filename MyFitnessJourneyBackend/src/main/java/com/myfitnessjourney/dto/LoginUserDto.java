package com.myfitnessjourney.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDto {
    private Long id;
    private String email;
    private String username;
    private String name;
    private Integer streak;
    private String pictureUrl;
}
