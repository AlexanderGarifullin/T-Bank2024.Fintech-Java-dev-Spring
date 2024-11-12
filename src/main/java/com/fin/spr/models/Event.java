package com.fin.spr.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_events", schema = "kudago")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_name", nullable = false)
    private String name;

    @Column(name = "c_start_date", nullable = false)
    private Instant startDate;

    @Column(name = "c_price")
    private String price;

    @Column(name = "c_free", nullable = false)
    private boolean free;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_location_id", nullable = false)
    private Location location;
}
