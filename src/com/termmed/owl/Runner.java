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

import org.apache.commons.cli.*;
import org.apache.commons.lang.IncompleteArgumentException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by alo on 4/6/16.
 */
public class Runner {
    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("mode", true, "conversion mode, " +
                "expected values: rf2-to-owl, owl-to-refset, refset-to-owl");
        options.addOption("rf2Folder", true, "RF2 Folder");
        options.addOption("cf", true, "Concepts file");
        options.addOption("df", true, "Descriptions file");
        options.addOption("rf", true, "Relationships file");
        options.addOption("lf", true, "language refset file");
        options.addOption("tf", true, "Text definition file");
        options.addOption("of", true, "Owl file");
        options.addOption("orf", true, "Owl Refset file");
        options.addOption("iri", true, "IRI for Owl Generation");
        options.addOption("syntax", true, "OWL Syntax, expected values: owlxml, functional, manchester");
        options.addOption("output", true, "Output file");
        options.addOption("cd", false, "Convert concepts to concrete domains");
        options.addOption("help", false, "Prints help");

        CommandLine cmd = parser.parse( options, args);
        if (cmd.hasOption("help")) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "rf2-to-owl", options );
        } else {
            if (!cmd.hasOption("mode") || !cmd.hasOption("output")) {
                throw new MissingArgumentException("-output and -mode arguments are mandatory," +
                        "  expected values for mode: rf2-to-owl, owl-to-refset, refset-to-owl");
            } else {
                String mode = cmd.getOptionValue("mode");
                String output = cmd.getOptionValue("output");
                boolean cd = cmd.hasOption("cd");
                String syntax = "owlxml";
                if (cmd.hasOption("syntax")) {
                    syntax = cmd.getOptionValue("syntax");
                };
                String iri = "http://snomed.info/id/";
                if (cmd.hasOption("iri")) {
                    iri = cmd.getOptionValue("iri");
                }
                switch (mode) {
                    case "rf2-to-owl":
                        if (cmd.hasOption("rf2Folder") ) {

                            String rf2Folder = cmd.getOptionValue("rf2Folder");
                            RF2Parser rf2parser = new RF2Parser(rf2Folder, output,iri, cd, syntax);
                            rf2parser.parse();
                        } else if (cmd.hasOption("cf") && cmd.hasOption("df") &&
                                cmd.hasOption("rf") && cmd.hasOption("lf")) {
                            String cf = cmd.getOptionValue("cf");
                            String df = cmd.getOptionValue("df");
                            String lf = cmd.getOptionValue("lf");
                            String rf = cmd.getOptionValue("rf");
                            String tf = cmd.getOptionValue("tf");
                            RF2Parser rf2parser = new RF2Parser(cf, rf,
                                    df,tf,lf, output,iri, cd, syntax);
                            rf2parser.parse();
                        } else {
                            throw new MissingArgumentException("Missing arguments, either the rf2Folder or " +
                                    "the set of RF2 files arguments are required (cf,df,rf,lf)");
                        }
                        break;
                    case "owl-to-refset":
                        if (cmd.hasOption("of") ) {
                            String of = cmd.getOptionValue("of");
                            OWLFunctionalSyntaxRefsetRenderer fr = new OWLFunctionalSyntaxRefsetRenderer();
                            long startTime = System.currentTimeMillis();
                            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                            File ontologyFile = new File(of);
                            System.out.println("Loading ontology from: " + ontologyFile.getName());
                            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
                            System.out.println("Ontology loaded in: " + (System.currentTimeMillis() - startTime) + " ms.");
                            startTime = System.currentTimeMillis();
                            PrintWriter writer2 = new PrintWriter(output, "UTF-8");
                            fr.render(ontology,writer2);
                            writer2.close();
                            System.out.println("");
                            System.out.println("OWL Refset created in: " + (System.currentTimeMillis() - startTime) +
                                    " ms. (" + output + ")");
                        } else {
                            throw new MissingArgumentException("Missing arguments, -of with path to Owl file is required");
                        }
                        break;
                    case "refset-to-owl":
                        if (cmd.hasOption("orf") ) {
                            long startTime = System.currentTimeMillis();
                            String orf = cmd.getOptionValue("orf");
                            File owlRefsetFile = new File(orf);
                            RefsetToOWLRenderer rfor = new RefsetToOWLRenderer();
                            PrintWriter writer2 = new PrintWriter(output, "UTF-8");
                            rfor.render(owlRefsetFile, writer2);
                            writer2.close();
                            System.out.println("");
                            System.out.println("OWL Ontology recreated in: " + (System.currentTimeMillis() - startTime) +
                                    " ms. (" + output + ")");
                        } else {
                            throw new MissingArgumentException("Missing arguments, -orf with path to Owl Refset file is required");
                        }
                        break;
                    default:
                        throw new IncompleteArgumentException("-mode argument error," +
                                "  expected values: rf2-to-owl, owl-to-refset, refset-to-owl");
                }
            }
        }
    }
}
