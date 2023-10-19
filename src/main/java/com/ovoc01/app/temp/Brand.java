package com.ovoc01.app.temp;

import com.ovoc01.app.core.annotation.Column;
import com.ovoc01.app.core.annotation.Mapping;
import com.ovoc01.app.core.annotation.PrimaryKey;

import lombok.Data;

@Mapping(database = "web_service_tp")
@Data
public class Brand {
    @Column
    @PrimaryKey(prefix = "BRAN", sequence = "brand_seq")
    String idBrand;
    @Column
    String name;
}
