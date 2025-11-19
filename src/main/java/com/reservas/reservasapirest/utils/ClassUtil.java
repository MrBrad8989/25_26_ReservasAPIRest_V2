package com.reservas.reservasapirest.utils;

import org.apache.commons.beanutils.BeanUtilsBean;
import java.lang.reflect.InvocationTargetException;
import org.springframework.stereotype.Component;

@Component
public class ClassUtil extends BeanUtilsBean {
    @Override
    public void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
        if (value != null) { // Evita establecer propiedades con valores nulos
            super.setProperty(bean, name, value);
        }
    }
}
