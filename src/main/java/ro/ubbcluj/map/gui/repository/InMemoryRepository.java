package ro.ubbcluj.map.gui.repository;

import ro.ubbcluj.map.gui.domain.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    Map<ID,E> entities;

    public InMemoryRepository() {
        entities= new HashMap<>();
    }

    @Override
    public Optional<E> findOne(ID id){
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity){
        if(entities.get(entity.getId()) != null) {
            return Optional.of(entity);
        }
        else {
            entities.put(entity.getId(), entity);
            return Optional.empty();
        }
    }

    @Override
    public Optional<E> delete(ID id){
        return Optional.ofNullable(entities.remove(id));
        }

    @Override
    public Optional<E> update(E entity){
        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return Optional.empty();
        }
        return Optional.of(entity);

    }

    @Override
    public int size() {
        return entities.size();
    }
}
