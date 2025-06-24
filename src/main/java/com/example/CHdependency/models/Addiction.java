package com.example.CHdependency.models;

import com.example.CHdependency.enums.Addictions;
import jakarta.persistence.*;

@Entity
@Table(name = "addictions")
public class Addiction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Addictions type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Addictions getType() {
        return type;
    }

    public void setType(Addictions type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

