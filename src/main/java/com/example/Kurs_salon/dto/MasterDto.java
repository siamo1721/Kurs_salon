package com.example.Kurs_salon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterDto {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String specialization;
    private String workSchedule;
}