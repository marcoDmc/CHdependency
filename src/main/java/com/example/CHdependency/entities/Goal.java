package com.example.CHdependency.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Period;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Period period;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}

