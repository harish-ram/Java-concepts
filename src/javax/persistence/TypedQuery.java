package javax.persistence;

import java.util.List;

public interface TypedQuery<T> {
    List<T> getResultList();
}
