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

package com.termmed.owl;

import org.semanticweb.owlapi.functional.renderer.FunctionalSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.AbstractOWLRenderer;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.io.OWLRendererIOException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import java.io.PrintWriter;

/**
 * Created by alo on 4/17/17.
 */
public class OWLFunctionalSyntaxRefsetRenderer extends AbstractOWLRenderer {
    @Override
    public void render(OWLOntology ontology, PrintWriter writer) throws OWLRendererException {
        try {
            OWLFuncionalSyntaxRefsetObjetRenderer ren = new OWLFuncionalSyntaxRefsetObjetRenderer(ontology,
                    writer);
            ontology.accept(ren);
            writer.flush();
        } catch (OWLRuntimeException e) {
            throw new OWLRendererIOException(e);
        }
    }
}
