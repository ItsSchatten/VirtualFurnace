package com.shanebeestudios.api.property;

import com.shanebeestudios.api.machine.Machine;

/**
 * Interface for a {@link Machine} that holds properties
 */
@SuppressWarnings("unused")
public interface PropertyHolder<P extends Properties> {

    /**
     * Get the properties associated with this property holder
     *
     * @return Properties associated with this property holder
     */
    P getProperties();

}
