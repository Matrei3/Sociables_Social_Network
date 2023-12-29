package ro.ubbcluj.map.gui.repository.paging;

import ro.ubbcluj.map.gui.domain.Friendship;
import ro.ubbcluj.map.gui.domain.Tuple;
import ro.ubbcluj.map.gui.repository.DataBaseRepositoryFriendships;

public class FriendshipDBPagingRepository extends DataBaseRepositoryFriendships implements PagingRepository<Tuple<Long,Long>, Friendship<Long>,Long> {
    public FriendshipDBPagingRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Page<Friendship<Long>> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Friendship<Long>> findAllPaged(Long id, Pageable pageable) {
        return null;
    }
}
