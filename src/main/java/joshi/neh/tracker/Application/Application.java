package joshi.neh.tracker.Application;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import joshi.neh.tracker.User.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long applicationId;
    @Column(nullable = false)
    private LocalDate dateApplied;
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String positionTitle;
    private String location;
    private String compensation;
    private String status;
    private String additionalInfo;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    @JoinColumn(referencedColumnName = "userId", name = "user_id")
    private User user;

}
