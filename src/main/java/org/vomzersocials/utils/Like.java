package org.vomzersocials.utils;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
//import org.vomzersocials.user.data.model.User;

@Setter
@Getter
@Entity
public class Like {
    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
//    @Id
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "Liker_Id", nullable = false)
////    private User id;

}
