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
 *
 * @author Alejandro Rodriguez
 */
public class DirectRunner {
    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        String conceptFile = "/Users/alo/Downloads/SnomedCT_RF2Release_INT_20170731/Snapshot/Terminology/sct2_Concept_Snapshot_INT_20170731.txt";
        String relationshipFile = "/Users/alo/Downloads/SnomedCT_RF2Release_INT_20170731/Snapshot/Terminology/sct2_Relationship_Snapshot_INT_20170731.txt";
        String outputFile = "/Users/alo/Downloads/conceptsOwlComplete-cd-alo.xml";
        String descriptionFile = "/Users/alo/Downloads/SnomedCT_RF2Release_INT_20170731/Snapshot/Terminology/sct2_Description_Snapshot-en_INT_20170731.txt";
        String languageFile = "/Users/alo/Downloads/SnomedCT_RF2Release_INT_20170731/Snapshot/Refset/Language/der2_cRefset_LanguageSnapshot-en_INT_20170731.txt";
//        String textDefinitionFile = "/Users/ar/Downloads/SnomedCT_RF2Release_INT_201601731/Snapshot/Terminology/xsct2_TextDefinition_Snapshot-en_INT_20160131.txt";
        String textDefinitionFile = null;
        String iri = "http://snomed.info/id/";

        RF2Parser parser = new RF2Parser(conceptFile, relationshipFile,
        		descriptionFile,textDefinitionFile,languageFile, outputFile,iri, true, true);
//        RF2Parser parser = new RF2Parser(conceptFile, relationshipFile,
//        		null,null,null, outputFile,iri);
        parser.parse();
        System.out.println("Done! The process has generated a new OWL Ontology file: " + outputFile);
    }
}
