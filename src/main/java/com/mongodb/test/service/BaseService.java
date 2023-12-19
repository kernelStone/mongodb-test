package com.mongodb.test.service;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class BaseService<ID, T, M extends MongoRepository<T, ID>> implements MongoRepository<T, ID> {
    @Resource
    protected M dao;
    @Resource
    private MongoTemplate mongoTemplate;

    //@Resource
    //private MongoLockDao mongoLockDao;

    private Class<T> clazz;

    public BaseService(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> iterable) {
        return dao.saveAll(iterable);
    }

    @Override
    public List<T> findAll() {
        return dao.findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return dao.findAll(sort);
    }

    @Override
    public <S extends T> S insert(S s) {
        return dao.insert(s);
    }

    @Override
    public <S extends T> List<S> insert(Iterable<S> iterable) {
        return dao.insert(iterable);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return dao.findAll(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return dao.findAll(example, sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return dao.findAll(pageable);
    }

    @Override
    public <S extends T> S save(S s) {
        return dao.save(s);
    }

    /**
     * 根据Id 更新
     *
     * @param t
     *
     * @return
     */
    public UpdateResult updateSelective(T t) {
        try {
            Class clazz = t.getClass();
            Update update = new Update();
            Field[] fields = clazz.getDeclaredFields();
            Object id = null;
            for (Field field : fields) {
                field.setAccessible(true);
                Id idAnnotation = field.getAnnotation(Id.class);
                if (Objects.nonNull(idAnnotation)) {
                    id = field.get(t);
                }
                Object value = field.get(t);
                if (Objects.nonNull(value)) {
                    update.set(field.getName(), value);
                }
            }
            Assert.notNull(id, "field 【_id】 must not be null!");
            if (id instanceof ObjectId) {
                id = id.toString();
            }
            Query query = new Query(Criteria.where("_id").is(id));
            return mongoTemplate.updateFirst(query, update, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UpdateResult update(Query query, T t) {
        try {
            Class clazz = t.getClass();
            Update update = new Update();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                //过滤id 不能更新
                Id idAnnotation = field.getAnnotation(Id.class);
                if (Objects.nonNull(idAnnotation)) {
                    continue;
                }
                Object value = field.get(t);
                if (Objects.nonNull(value)) {
                    update.set(field.getName(), value);
                }
            }
            return mongoTemplate.updateMulti(query, update, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        return dao.findById(id);
    }

    /**
     * 行级 加锁 查找
     *
     * @param id
     *
     * @return
     */
    public T lock(ID id) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("locked", true);
        return mongoTemplate.findAndModify(query, update, clazz);
    }

    public UpdateResult unLock(ID id) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("locked", false);
        return mongoTemplate.updateFirst(query, update, clazz);
    }

    @Override
    public boolean existsById(ID id) {
        return dao.existsById(id);
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> iterable) {
        return dao.findAllById(iterable);
    }

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public void deleteById(ID id) {
        dao.deleteById(id);
    }

    @Override
    public void delete(T t) {
        dao.delete(t);
    }

    @Override
    public void deleteAll(Iterable<? extends T> iterable) {
        dao.deleteAll(iterable);
    }

    @Override
    public void deleteAll() {
        dao.deleteAll();
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return dao.findOne(example);
    }

    public <S extends T> Optional<S> findOne(S s) {
        return dao.findOne(Example.of(s));
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return dao.findAll(example, pageable);
    }

    public <S extends T> Page<S> findAll(S s, Pageable pageable) {
        return dao.findAll(Example.of(s), pageable);
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return dao.count(example);
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return dao.exists(example);
    }

    public List<T> find(Query query) {
        Assert.notNull(query, "Query must not be null!");
        return mongoTemplate.find(query, clazz);
    }

    public List<T> find(Query query, Pageable pageable) {
        Assert.notNull(query, "Query must not be null!");
        query.with(pageable);
        return mongoTemplate.find(query, clazz);
    }

    /**
     * 虚拟count 查询
     *
     * @return
     */
    public PageImpl<T> find(Pageable pageable) {
        long count = 10_000_000_000L;
        List<T> list = find(new Query().with(pageable));
        return new PageImpl<>(list, pageable, count);
    }

}
