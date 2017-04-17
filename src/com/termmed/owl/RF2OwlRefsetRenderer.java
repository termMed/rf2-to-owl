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

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.model.*;

import java.io.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by alo on 4/16/17.
 */
public class RF2OwlRefsetRenderer {
    private static OWLOntologyManager onologyManager = OWLManager.createOWLOntologyManager();
    private static OWLFunctionalSyntaxRenderer functionalSyntaxRenderer = new OWLFunctionalSyntaxRenderer();

    public RF2OwlRefsetRenderer() {
        super();
    }

    public void render(OWLOntology ontology, Writer writer) throws OWLOntologyCreationException, OWLRendererException, IOException {
        String row = "UUID\teffectiveTime\tactive\tmoduleId\trefsetId\treferencedComponentId\tannotation\n";
        writer.write(row);
        System.out.println("");
        System.out.println("Starting Owl Refset Generation");
        System.out.println("Declarations: " + ontology.axioms(AxiomType.DECLARATION).count());
        int count = 0;
        Iterator<OWLDeclarationAxiom> declarations = ontology.axioms(AxiomType.DECLARATION).iterator();
        while(declarations.hasNext()) {
            count++;
            if (count == 1 || count % 10000 == 0) {
                System.out.print(".");
            }
            OWLAxiom loopAxiom = declarations.next();
            renderAxiom(loopAxiom, writer);
        }
        System.out.println("");
        System.out.println("OWLSubClassOfAxiom: " + ontology.axioms(AxiomType.SUBCLASS_OF).count());
        count = 0;
        Iterator<OWLSubClassOfAxiom> subclasses = ontology.axioms(AxiomType.SUBCLASS_OF).iterator();
        while(declarations.hasNext()) {
            count++;
            if (count == 1 || count % 10000 == 0) {
                System.out.print(".");
            }
            OWLAxiom loopAxiom = subclasses.next();
            renderAxiom(loopAxiom, writer);
        }
        System.out.println("");
        System.out.println("OWLEquivalentClassesAxiom: " + ontology.axioms(AxiomType.EQUIVALENT_CLASSES).count());
        count = 0;
        Iterator<OWLEquivalentClassesAxiom> equivalents = ontology.axioms(AxiomType.EQUIVALENT_CLASSES).iterator();
        while(declarations.hasNext()) {
            count++;
            if (count == 1 || count % 10000 == 0) {
                System.out.print(".");
            }
            OWLAxiom loopAxiom = equivalents.next();
            renderAxiom(loopAxiom, writer);
        }
    }

    private void renderAxiom(OWLAxiom axiom, Writer writer) throws OWLOntologyCreationException, OWLRendererException, IOException {
        OutputStream os = new ByteArrayOutputStream();
        OWLOntology loopO = onologyManager.createOntology();
        onologyManager.addAxiom(loopO, axiom);
        functionalSyntaxRenderer.render(loopO,os);
        String ontologyText = os.toString();
        int bodyStart = ontologyText.indexOf(axiom.getAxiomType().getName());
        if (bodyStart > -1) {
            String body = ontologyText.substring(bodyStart, ontologyText.length()-2).trim();
            String refsetId = "";
            if (axiom.isOfType(AxiomType.ANNOTATION_ASSERTION)) {
                refsetId = "733073007";
            } else {
                refsetId = "733073007";
            }
            String referencedComponentId = new Scanner(body).useDelimiter("\\D+").next();
            String moduleId = "900000000000207008";
            String row = UUID.randomUUID() + "\t" +
                    "20170731" + "\t1\t" + moduleId + "\t" +
                    refsetId + "\t" + referencedComponentId + "\t" +
                    body + "\n";
            writer.write(row);
        }
        onologyManager.removeOntology(loopO);
    }
}
