package org.example.models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Cinema {
    private final String id;
    private final String name;
    private final City city;
    private final List<Screen> screens;
}
