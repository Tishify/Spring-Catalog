package org.tishfy.springcatalog.tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tishfy.springcatalog.model.User;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {

    private Long roleId;

    private String roleName;

    private List<User> users = new ArrayList<>();
}

