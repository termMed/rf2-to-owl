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


/**
 * The Class LightRelationship.
 *
 * @author Alejandro Rodriguez
 */

public class LightRelationship extends Component {

    /** The type. */
    private Long type;
    
    /** The target. */
    private Long target;
    
    /** The source id. */
    private Long sourceId;
    
    /** The group id. */
    private Integer groupId;
    
    /** The char type. */
    private Long charType;
    
    /** The modifier. */
    private Long modifier;
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public Long getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(Long type) {
		this.type = type;
	}
	
	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public Long getTarget() {
		return target;
	}
	
	/**
	 * Sets the target.
	 *
	 * @param target the new target
	 */
	public void setTarget(Long target) {
		this.target = target;
	}
	
	/**
	 * Gets the source id.
	 *
	 * @return the source id
	 */
	public Long getSourceId() {
		return sourceId;
	}
	
	/**
	 * Sets the source id.
	 *
	 * @param sourceId the new source id
	 */
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	
	/**
	 * Gets the group id.
	 *
	 * @return the group id
	 */
	public Integer getGroupId() {
		return groupId;
	}
	
	/**
	 * Sets the group id.
	 *
	 * @param groupId the new group id
	 */
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	
	/**
	 * Gets the char type.
	 *
	 * @return the char type
	 */
	public Long getCharType() {
		return charType;
	}
	
	/**
	 * Sets the char type.
	 *
	 * @param charType the new char type
	 */
	public void setCharType(Long charType) {
		this.charType = charType;
	}
	
	/**
	 * Gets the modifier.
	 *
	 * @return the modifier
	 */
	public Long getModifier() {
		return modifier;
	}
	
	/**
	 * Sets the modifier.
	 *
	 * @param modifier the new modifier
	 */
	public void setModifier(Long modifier) {
		this.modifier = modifier;
	}



}
