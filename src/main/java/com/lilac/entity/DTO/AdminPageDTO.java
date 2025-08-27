package com.lilac.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPageDTO {
    private Integer pageNum;
    private Integer pageSize;
    private String name;
}
