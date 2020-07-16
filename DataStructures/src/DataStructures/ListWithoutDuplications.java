package DataStructures;

import java.util.ArrayList;
import java.util.Collection;

public class ListWithoutDuplications extends ArrayList {

    @Override
    public boolean contains(Object o) {
        boolean contain = false;
        for (Object object: this){
            if (object.equals(o)){
                contain = true;
            }
        }
        return contain;
    }

    @Override
    public boolean add(Object o) {
        if (!this.contains(o)) {
            super.add(o);
        }
       return true;
    }

    @Override
    public boolean addAll(Collection c) {
        for (Object object: c){
                this.add(object);
        }
        return true;
    }
}
