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
