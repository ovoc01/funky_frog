package com.ovoc01.funkyfrog.core.tools;

import java.lang.reflect.*;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodAndParameters {
    Method method;
    Parameter [] parameters;

    public MethodAndParameters(Method method,Parameter [] parameters) {
        this.method = method;
        this.parameters = parameters;
    }
}
