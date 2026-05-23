package com.dioni.financeiro.infra;

import com.dioni.financeiro.infra.annotation.TableName;
import com.dioni.financeiro.infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QueryExecutor {

    private final JdbcTemplate jdbcTemplate;

    private final ConversionService conversionService;

    @SuppressWarnings("java:S2077")
    public <T> Page<T> findPageWithMapper(String sql, RowMapper<T> rowMapper, Pageable pageable, Object... params) {
        String countSql = "SELECT COUNT(*) FROM (%s) AS count_table".formatted(sql);
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, params);

        String paginatedSql = "%s LIMIT ? OFFSET ?".formatted(sql);
        Object[] finalParams = new Object[params.length + 2];
        System.arraycopy(params, 0, finalParams, 0, params.length);
        finalParams[params.length] = pageable.getPageSize();
        finalParams[params.length + 1] = pageable.getOffset();

        List<T> content = jdbcTemplate.query(paginatedSql, rowMapper, finalParams);
        return new PageImpl<>(content, pageable, total);
    }

    @SuppressWarnings("java:S2077")
    public <T> Page<T> findPage(String sql, Class<T> resultType, Pageable pageable, Object... params) {
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") as count_table";
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, params);

        String paginatedSql = sql + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<>(resultType);
        rowMapper.setConversionService(conversionService);

        List<T> content = jdbcTemplate.query(paginatedSql, rowMapper, params);
        return new PageImpl<>(content, pageable, total);
    }

    public <T> List<T> findList(String sql, Class<T> resultType) {
        BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<>(resultType);
        rowMapper.setConversionService(conversionService);
        return jdbcTemplate.query(sql, rowMapper);
    }

    public <T> List<T> findList(String sql, Class<T> resultType, Object... params) {
        BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<>(resultType);
        rowMapper.setConversionService(conversionService);
        return jdbcTemplate.query(sql, rowMapper, params);
    }

    public <T> Optional<T> findOptional(String sql, Class<T> resultType) {
        return findOptional(sql, resultType, new Object[]{});
    }

    public <T> Optional<T> findOptional(String sql, Class<T> resultType, Object... params) {
        try {
            BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<>(resultType);
            rowMapper.setConversionService(conversionService);
            T result = jdbcTemplate.queryForObject(sql, rowMapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public <T> T findOne(String sql, Class<T> resultType) {
        return findOptional(sql, resultType).orElseThrow(() -> new ResourceNotFoundException("register.not.found"));
    }

    public <T> T findOne(String sql, Class<T> resultType, Object... params) {
        return findOptional(sql, resultType, params)
                .orElseThrow(() -> new ResourceNotFoundException("register.not.found"));
    }

    private String extractTableName(Class<?> entity) {
        TableName tableNameAnnotation = entity.getAnnotation(TableName.class);
        if (tableNameAnnotation != null) {
            return tableNameAnnotation.value();
        }
        return convertToSnakeCase(entity.getSimpleName());
    }

    private String convertToSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    public Long countAll(Class<?> entity) {
        String tableName = extractTableName(entity);
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Long.class);
    }

    public Long countWithCondition(Class<?> entity, String whereClause, Object... params) {
        String tableName = extractTableName(entity);
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName + " WHERE " + whereClause, Long.class, params);
    }
}
