package org.ntapia.dao;

import org.ntapia.model.Ride;
import org.ntapia.model.Unicorn;

public interface UnicornDAO {

    Unicorn getRandom();

    void save(Ride ride);
}
