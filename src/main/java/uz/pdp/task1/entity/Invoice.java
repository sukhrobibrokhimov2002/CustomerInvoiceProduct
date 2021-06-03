package uz.pdp.task1.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    private Orders order;
    @Column(nullable = false)
    private double amount;
    @Column(nullable = false)
    private Date issued;
    @Column(nullable = false)
    private Date due;
}
