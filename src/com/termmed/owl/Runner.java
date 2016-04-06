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

/**
 * Created by alo on 4/6/16.
 */
public class Runner {

    public static void main(String[] args) throws Exception {

        if (args.length != 4)
            throw new IllegalArgumentException("Arguments error, required: conceptsFile relationshipsFile outputFile iri");

        String conceptFile = args[0];
        String relationshipFile = args[1];
        String outputFile = args[2];
        String iri = args[3];

        RF2Parser parser = new RF2Parser(conceptFile, relationshipFile, outputFile, iri);
        parser.parse();
        System.out.println("Done! The process has generated a new OWL Ontology file: " + outputFile);
    }

}
