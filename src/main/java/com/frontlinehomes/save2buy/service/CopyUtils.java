package com.frontlinehomes.save2buy.service;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class CopyUtils {

    public static void copyNonNullFields(Object dest, Object src) throws Exception {

        // Check if the objects are of the same class
        if (dest.getClass() != src.getClass()) {
            throw new IllegalArgumentException("Objects must be of the same class");
        }
        // Get the declared fields of the class
        Field[] fields = dest.getClass().getDeclaredFields();

        // Loop through the fields
        for (Field field : fields) {
            // Make the field accessible
            field.setAccessible(true);

            // Get the value of the field from the source object
            Object value = field.get(src);

            // If the value is not null, copy it to the destination object
            if (value != null) {
                field.set(dest, value);
            }
        }
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
