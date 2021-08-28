package com.hex.netty.chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author: hs
 * 处理职责链
 */
public class DealingChain {

    private List<Dealing> dealings = Collections.synchronizedList(new ArrayList<>(8));

    private Iterator<Dealing> iterator;

    public void deal(DealingContext context) {
        if (iterator == null) {
            iterator = dealings.iterator();
        }
        if (iterator.hasNext()) {
            Dealing next = iterator.next();
            next.deal(context);
        }
    }

    public void addDealing(Dealing dealing) {
        this.dealings.add(dealing);
    }

    public List<Dealing> getDealings() {
        return dealings;
    }

    public void setDealings(List<Dealing> dealings) {
        this.dealings = dealings;
    }

}