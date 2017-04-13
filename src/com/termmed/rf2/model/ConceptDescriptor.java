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
 * The Class ConceptDescriptor.
 *
 * @author Alejandro Rodriguez
 */
public class ConceptDescriptor extends Component{

	/** The concept id. */
	Long conceptId;
	
	/** The default term. */
	String defaultTerm;
    
    /** The definition status. */
    String definitionStatus;
	
	/**
	 * Gets the concept id.
	 *
	 * @return the concept id
	 */
	public Long getConceptId() {
		return conceptId;
	}
	
	/**
	 * Sets the concept id.
	 *
	 * @param conceptId the new concept id
	 */
	public void setConceptId(Long conceptId) {
		this.conceptId = conceptId;
	}
	
	/**
	 * Gets the default term.
	 *
	 * @return the default term
	 */
	public String getDefaultTerm() {
		return defaultTerm;
	}
	
	/**
	 * Sets the default term.
	 *
	 * @param defaultTerm the new default term
	 */
	public void setDefaultTerm(String defaultTerm) {
		this.defaultTerm = defaultTerm;
	}
	
	/**
	 * Gets the definition status.
	 *
	 * @return the definition status
	 */
	public String getDefinitionStatus() {
		return definitionStatus;
	}
	
	/**
	 * Sets the definition status.
	 *
	 * @param definitionStatus the new definition status
	 */
	public void setDefinitionStatus(String definitionStatus) {
		this.definitionStatus = definitionStatus;
	}
	
}
