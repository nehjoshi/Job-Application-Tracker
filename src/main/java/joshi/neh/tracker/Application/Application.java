package joshi.neh.tracker.Application;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import joshi.neh.tracker.User.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Application implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_seq")
    @SequenceGenerator(name = "application_seq", sequenceName = "application_id_seq", allocationSize = 1)
    private Long applicationId;
    @Column(nullable = false)
    private LocalDateTime dateApplied;
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String positionTitle;
    private String location;
    private String compensation;

    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus status;
    private String additionalInfo;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(referencedColumnName = "userId", name = "user_id")
    private User user;

}
