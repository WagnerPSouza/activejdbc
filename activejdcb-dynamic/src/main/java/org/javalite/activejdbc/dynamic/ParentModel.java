/*
 * Copyright UniSoma (www.unisoma.com.br).
 * Todos os direitos Reservados.
 * Propriedade confidencial não publicada da UniSoma.
 *
 * Criado em 2019.
*/
package org.javalite.activejdbc.dynamic;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParentModel {

    private String tableName;
    private String foreignKeyName;

}
