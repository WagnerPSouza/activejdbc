/*
 * Copyright UniSoma (www.unisoma.com.br).
 * Todos os direitos Reservados.
 * Propriedade confidencial não publicada da UniSoma.
 *
 * Criado em 2019.
*/
package org.javalite.activejdbc.dynamic;

import java.util.List;

import com.google.common.base.CaseFormat;

public final class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static final String tableNameToPascalCase(String value) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, value);
    }

    public static final Boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }

    public static final Boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static final Boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static final Boolean isNotEmpty(List<?> list) {
        return !isEmpty(list);
    }

}
