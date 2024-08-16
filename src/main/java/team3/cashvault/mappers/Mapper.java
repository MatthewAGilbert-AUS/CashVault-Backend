package team3.cashvault.mappers;

import java.util.List;

public interface Mapper<A, B> {
    B mapTo(A a);
    A mapFrom(B b);
    List<B> mapList(List<A> aList);
}
