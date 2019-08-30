/*
 * Copyright UniSoma (www.unisoma.com.br).
 * Todos os direitos Reservados.
 * Propriedade confidencial não publicada da UniSoma.
 *
 * Criado em 2019.
*/
package org.javalite.activejdbc.dynamic;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.ModelDelegate;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.BelongsToParents;

public final class ModelDelegateDynamic {

    private ModelDelegateDynamic() {
    }

    public static Long count(String tableName) {
        return ModelDelegate.count(getClassByTable(tableName));
    }

    public static Long count(String tableName, String query, Object... params) {
        return ModelDelegate.count(getClassByTable(tableName), query, params);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> T create(String tableName, Object... namesAndValues) {
        return (T) ModelDelegate.create(getClassByTable(tableName), namesAndValues);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> T createIt(String tableName, Object... namesAndValues) {
        return (T) ModelDelegate.createIt(getClassByTable(tableName), namesAndValues);
    }

    public static int delete(String tableName, String query, Object... params) {
        return ModelDelegate.delete(getClassByTable(tableName), query, params);
    }

    public static int deleteAll(String tableName) {
        return ModelDelegate.deleteAll(getClassByTable(tableName));
    }

    public static boolean exists(String tableName, Object id) {
        return ModelDelegate.exists(getClassByTable(tableName), id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> LazyList<T> findAll(String tableName) {
        Class<? extends Model> clazz = getClassByTable(tableName);
        if (clazz.isAnnotationPresent(BelongsToParents.class)) {
            List<Class<? extends Model>> includes = Arrays.asList(clazz.getAnnotation(BelongsToParents.class).value())
                    .stream().map(BelongsTo::parent).collect(Collectors.toList());
            return ModelDelegate.findAll(clazz).include(includes.toArray(new Class[0]));
        } else {
            return (LazyList<T>) ModelDelegate.findAll(clazz);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> T findById(String tableName, Object id) {
        return (T) ModelDelegate.findById(getClassByTable(tableName), id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> T findByCompositeKeys(String tableName, Object... values) {
        return (T) ModelDelegate.findByCompositeKeys(getClassByTable(tableName), values);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> LazyList<T> findBySql(String tableName, String fullQuery, Object... params) {
        return (LazyList<T>) ModelDelegate.findBySql(getClassByTable(tableName), fullQuery, params);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> T findFirst(String tableName, String subQuery, Object... params) {
        return (T) ModelDelegate.findFirst(getClassByTable(tableName), subQuery, params);
    }

    public static void purgeCache(String tableName) {
        ModelDelegate.purgeCache(getClassByTable(tableName));
    }

    public static int update(String tableName, String updates, String conditions, Object... params) {
        return ModelDelegate.update(getClassByTable(tableName), updates, conditions, params);
    }

    public static int updateAll(String tableName, String updates, Object... params) {
        return ModelDelegate.updateAll(getClassByTable(tableName), updates, null, params);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> LazyList<T> where(String tableName, String subquery, Object... params) {
        return (LazyList<T>) ModelDelegate.where(getClassByTable(tableName), subquery, params);
    }

    public static Class<? extends Model> getClassByTable(final String tableName) {
        return ModelDelegate.metaModelFor(tableName).getModelClass();
    }

}
