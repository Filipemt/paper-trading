package com.filipecode.papertrading.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assets")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticker;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AssetType type;

// Todo: Adicionar relacionamento com Order
}
