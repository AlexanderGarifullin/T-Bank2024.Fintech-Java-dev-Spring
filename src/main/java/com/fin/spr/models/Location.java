package com.fin.spr.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fin.spr.models.memento.LocationMemento;
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
@ToString(exclude = "events")
@Table(name = "t_locations", schema = "kudago")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@EqualsAndHashCode(exclude = "events")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "c_name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Event> events;


    public LocationMemento save(CrudAction action) {
        return new LocationMemento(id, slug, name, action, System.currentTimeMillis());
    }

    public void restore(LocationMemento memento) {
        this.id = memento.getId();
        this.slug = memento.getSlug();
        this.name = memento.getName();
    }
}
