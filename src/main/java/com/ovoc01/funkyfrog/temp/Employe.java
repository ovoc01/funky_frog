package com.ovoc01.funkyfrog.temp;

import com.ovoc01.funkyfrog.core.annotation.Column;
import com.ovoc01.funkyfrog.core.annotation.Mapping;
import com.ovoc01.funkyfrog.core.annotation.PrimaryKey;
import com.ovoc01.funkyfrog.core.annotation.ProvideFkOnCreation;
import com.ovoc01.funkyfrog.core.annotation.ci.InitializationType;
import com.ovoc01.funkyfrog.core.mapping.FunkyFrogPersist;

import lombok.Getter;
import lombok.Setter;

@Mapping(database = "module_achat")
@ProvideFkOnCreation(type = InitializationType.SPECIFIED_FIELD_ONLY)
@Getter
@Setter
public class Employe extends FunkyFrogPersist{
    @Column
    @PrimaryKey(sequence = "",prefix = "")
    Integer idEmploye;

    @Column
    String nom;

    @Column
    String prenom;

    @Column
    String email;

    @Column(name = "motdepasse")
    String mdp;

}
