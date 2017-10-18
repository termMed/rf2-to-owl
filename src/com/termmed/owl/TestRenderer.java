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
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by alo on 4/17/17.
 */
public class TestRenderer {

    private static long startTime = 0;
    private static long stopTime = 0;
    private static OWLFunctionalSyntaxRefsetRenderer fr = new OWLFunctionalSyntaxRefsetRenderer();

    public static void main(String[] args) throws Exception {
        startTime = System.currentTimeMillis();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File testOntology = new File("/Users/alo/IdeaProjects/rf2-to-owl/sct.owl");
//        File testOntology = new File("/Users/alo/Downloads/conceptsOwlComplete-cd-alo.xml");
        System.out.println("testOntology: " + testOntology.getName());
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(testOntology);
        System.out.println("Terminology loaded in: " + (System.currentTimeMillis() - startTime) + " ms.");
        startTime = System.currentTimeMillis();
        PrintWriter writer2 = new PrintWriter("owlRefset-sct-owl.txt", "UTF-8");
        fr.render(ontology,writer2);
        writer2.close();
        System.out.println("OWL Refset created in: " + (System.currentTimeMillis() - startTime) + " ms.");
    }

}
