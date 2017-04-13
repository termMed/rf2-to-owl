/*
 *
 *  * Copyright (C) 2014 termMed IT
 *  * www.termmed.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.termmed.rf2.model;

import java.util.UUID;

/**
 * The Class Component.
 *
 * @author Alejandro Rodriguez
 */
public class Component {

    /** The uuid. */
    private UUID uuid;
    
    /** The active. */
    private Boolean active;
    
    /** The effective time. */
    private String effectiveTime;
    
    /** The module. */
    private Long module;
    
    /**
     * Instantiates a new component.
     */
    public Component() {
    }

    /**
     * Gets the effective time.
     *
     * @return the effective time
     */
    public String getEffectiveTime() {
        return effectiveTime;
    }

    /**
     * Sets the effective time.
     *
     * @param effectiveTime the new effective time
     */
    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    /**
     * Gets the active.
     *
     * @return the active
     */
    public Boolean getActive() {
		return active;
	}

	/**
	 * Sets the active.
	 *
	 * @param active the new active
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * Gets the module.
	 *
	 * @return the module
	 */
	public Long getModule() {
		return module;
	}

	/**
	 * Sets the module.
	 *
	 * @param module the new module
	 */
	public void setModule(Long module) {
		this.module = module;
	}

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid.
     *
     * @param uuid the new uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

}
