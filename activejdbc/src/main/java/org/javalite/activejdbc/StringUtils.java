/*
 * Copyright UniSoma (www.unisoma.com.br).
 * Todos os direitos Reservados.
 * Propriedade confidencial não publicada da UniSoma.
 *
 * Criado em 2019.
*/
package org.javalite.activejdbc;

public class StringUtils {
    
    public static Boolean isNullOrEmpty(String value) {
        return value == null || "".equals(value);
    }

}
