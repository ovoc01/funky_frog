package com.ovoc01.funkyfrog.temp;

import java.sql.Date;
import java.sql.Timestamp;

import com.ovoc01.funkyfrog.core.annotation.Column;
import com.ovoc01.funkyfrog.core.annotation.Mapping;
import com.ovoc01.funkyfrog.core.annotation.PrimaryKey;
import com.ovoc01.funkyfrog.core.mapping.FunkyFrogPersist;

import lombok.Getter;
import lombok.Setter;

@Mapping(table = "besoins", database = "module_achat")
@Getter
public class Besoin extends FunkyFrogPersist {
    @Column
    @PrimaryKey(prefix = "", sequence = "")
    Integer idBesoins;

    @Column(applyInheritance = true)
    Timestamp dateCreation;

    @Column
    Integer idEmploye;

    @Column(applyInheritance = true)
    Integer idService;

    @Column
    Integer etat;

    @Column
    String reference;

    public void setIdBesoins(Integer id) {
        this.idBesoins = id;
    }

    public void setDateCreation(Timestamp timestamp) {
        this.dateCreation = timestamp;
    }

    public void setIdEmploye(Integer idEmploye) {
        this.idEmploye = idEmploye;
    }

    public void setIdService(Integer idService) {
        this.idService = idService;
    }

    public void setEtat(Integer etat) {
        this.etat = etat;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
