package com.ovoc01.funkyfrog.temp;

import java.sql.Connection;

import com.ovoc01.funkyfrog.core.annotation.Column;
import com.ovoc01.funkyfrog.core.annotation.CustomQuery;
import com.ovoc01.funkyfrog.core.annotation.ForeignKey;
import com.ovoc01.funkyfrog.core.annotation.JoinSplate;
import com.ovoc01.funkyfrog.core.annotation.Mapping;
import com.ovoc01.funkyfrog.core.annotation.PrimaryKey;
import com.ovoc01.funkyfrog.core.mapping.FunkyFrogPersist;

import lombok.Data;

@Mapping(database = "web_service_tp")
@Data
public class Brand extends FunkyFrogPersist {
    @Column
    @PrimaryKey(prefix = "BRAN", sequence = "brand_seq")
    String idBrand;
    @Column
    String name;


    @CustomQuery(queryID = "brand.someFunction", value = "SELECT * FROM brand where idBrand=?1 ")
    public Brand someFunction(String idBrand, Connection c) throws Exception {
        return (Brand) executeDtbQuery(Brand.class, c, "brand.someFunction", idBrand);
    }

}
