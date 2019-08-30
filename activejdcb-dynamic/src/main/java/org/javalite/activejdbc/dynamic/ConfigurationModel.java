/*
 * Copyright UniSoma (www.unisoma.com.br).
 * Todos os direitos Reservados.
 * Propriedade confidencial não publicada da UniSoma.
 *
 * Criado em 2019.
*/
package org.javalite.activejdbc.dynamic;

import java.util.List;

import org.javalite.activejdbc.DB;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Getter
@Setter
@Builder
public class ConfigurationModel {

    private String packageGenerator;
    private String tableName;
    private String idColumn;
    private String idGeneratorCode;
    private List<ParentModel> parents;
    @Builder.Default
    private String dbName = DB.DEFAULT_NAME;

    @Tolerate
    public ConfigurationModel() {
        this.dbName = DB.DEFAULT_NAME;
    }

}
