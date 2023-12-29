package ro.ubbcluj.map.gui.repository.paging;


import ro.ubbcluj.map.gui.domain.Entity;
import ro.ubbcluj.map.gui.repository.Repository;

public interface PagingRepository<ID,
        E extends Entity<ID>,SearchableID>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);
    Page<E> findAllPaged(SearchableID id, Pageable pageable);

}
