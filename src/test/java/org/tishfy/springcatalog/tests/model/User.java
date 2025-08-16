package org.tishfy.springcatalog.tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long userId;

    private String email;

    private String name;

    private Role role;
}

