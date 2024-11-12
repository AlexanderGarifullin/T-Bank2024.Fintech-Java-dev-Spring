package com.fin.spr.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * The {@code Location} class represents a location entity with a slug and a name.
 * It is used to identify and describe different locations within the application.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_locations", schema = "kudago")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "c_name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Event> events;
}
