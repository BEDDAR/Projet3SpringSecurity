package fr.openclassrooms.rental.entite;


import fr.openclassrooms.rental.enumer.TypeDeRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private TypeDeRole libelle;

    public void setId(int id) {
        this.id = id;
    }

    public void setLibelle(TypeDeRole libelle) {
        this.libelle = libelle;
    }

    public TypeDeRole getLibelle() {
        return libelle;
    }
}
