package org.javalite.activejdbc.dynamic;
/*
 * Copyright UniSoma (www.unisoma.com.br).
 * Todos os direitos Reservados.
 * Propriedade confidencial não publicada da UniSoma.
 *
 * Criado em 2019.
*/

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.Registry;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.BelongsToParents;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdGenerator;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class RegistryModel {

    private static Map<String, Class<? extends Model>> tableClass = new HashMap<>();

    private RegistryModel() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(RegistryModel.class);
    private static final String VALUE = "value";

    public static Class<? extends Model> registryNewModel(ConfigurationModel model) {
        Class<? extends Model> clazz = loadModelClass(model);
        Registry.instance().registryModel(clazz);
        try {
            Registry.instance().initModelConfiguration(model.getDbName());
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Erro registry new model", e);
        }
        tableClass.put(model.getTableName(), clazz);
        return clazz;
    }
    
    public static Class<? extends Model> getClassByTableName(String tableName) {
        return tableClass.get(tableName);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Model> loadModelClass(ConfigurationModel model) {
        String className = model.getPackageGenerator() + "." + Utils.tableNameToPascalCase(model.getTableName());
        try {
            return (Class<? extends Model>) Class.forName(className);
        } catch (ClassNotFoundException ignore) {
            logger.debug("Ignore error ClassNotFound {}", className);
        }

        // Create the class.
        try {
            ClassPool pool = ClassPool.getDefault();

            // Create the class.
            CtClass subClass = pool.makeClass(className);

            // ***set annotations
            ConstPool constPool = subClass.getClassFile().getConstPool();
            AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            subClass.getClassFile().addAttribute(attr);
            if (Utils.isNotEmpty(model.getDbName())) {
                Annotation anno = new Annotation(constPool, pool.get(DbName.class.getName()));
                anno.addMemberValue(VALUE, new StringMemberValue(model.getDbName(), constPool));
                attr.addAnnotation(anno);
            }

            if (Utils.isNotEmpty(model.getTableName())) {
                Annotation anno = new Annotation(constPool, pool.get(Table.class.getName()));
                anno.addMemberValue(VALUE, new StringMemberValue(model.getTableName(), constPool));
                attr.addAnnotation(anno);
            }

            if (Utils.isNotEmpty(model.getIdColumn())) {
                Annotation anno = new Annotation(constPool, pool.get(IdName.class.getName()));
                anno.addMemberValue(VALUE, new StringMemberValue(model.getIdColumn(), constPool));
                attr.addAnnotation(anno);
            }

            if (Utils.isNotEmpty(model.getIdGeneratorCode())) {
                Annotation anno = new Annotation(constPool, pool.get(IdGenerator.class.getName()));
                anno.addMemberValue(VALUE, new StringMemberValue(model.getIdGeneratorCode(), constPool));
                attr.addAnnotation(anno);
            }

            if (Utils.isNotEmpty(model.getParents())) {
                List<Annotation> parents = new ArrayList<>();
                model.getParents().forEach(p -> {
                    try {
                        Annotation anno = new Annotation(constPool, pool.get(BelongsTo.class.getName()));
                        anno.addMemberValue("foreignKeyName", new StringMemberValue(p.getForeignKeyName(), constPool));
                        anno.addMemberValue("parent",
                                new ClassMemberValue(tableClass.get(p.getTableName()).getName(), constPool));
                        parents.add(anno);
                    } catch (NotFoundException e) {
                    }
                });
                
                ArrayList<AnnotationMemberValue> members = new ArrayList<>();
                AnnotationMemberValue annotationValue;
                for (Annotation a: parents) {
                    annotationValue =  new AnnotationMemberValue(constPool);
                    annotationValue.setValue(a);
                    members.add(annotationValue);
                }
                
                ArrayMemberValue arrayValue = new ArrayMemberValue(constPool);
                arrayValue.setValue((members.toArray(new MemberValue[0])));
                Annotation anno = new Annotation(constPool, pool.get(BelongsToParents.class.getName()));
                anno.addMemberValue(VALUE, arrayValue);
                attr.addAnnotation(anno);
            }

            // TODO FALTA MAPEAR AS RELAÇÕES

            // set super class
            final CtClass superClass = pool.get(Model.class.getName());
            subClass.setSuperclass(superClass);
            subClass.setModifiers(Modifier.PUBLIC);

            CtConstructor constr = CtNewConstructor.defaultConstructor(subClass);
            constr.setModifiers(Modifier.PUBLIC);
            subClass.addConstructor(constr);
            subClass.defrost();
            return subClass.toClass(pool.getClass().getClassLoader(), pool.getClass().getProtectionDomain());
        } catch (CannotCompileException ex) {
            throw new RuntimeException(ex);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
