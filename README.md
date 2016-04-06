# RF2 to OWL

This project uses the OWLApi to create an OWL ontology, in RDF/XML format, based on the content of standard SNOMED CT RF2 files.

## Build instructions

Clone and build with:

`mvn clean compile assembly:single`

This will generate the executable JAR file: owl-test-x.x.x-SNAPSHOT-jar-with-dependencies.jar

Binaries are also available in the releases section of the [github repository](https://github.com/termMed/rf2-to-owl/releases).

## Executing the conversion

Run the executable from the command line, for example:

`java -jar owl-test-x.x.x-SNAPSHOT-jar-with-dependencies.jar /folder/sct2_Concept_Snapshot_INT_20150131.txt /folder/sct2_StatedRelationship_Snapshot_INT_20150131.txt /folder/outputFile.owl http://snomed.org/`

Parameters (in this order):
 * Path to RF2 Concepts Snapshot file
 * Path to RF2 Stated Relationships Snapshot file
 * Path to output file (it will be created or replaced by this process)
 * IRI for the resulting ontology

## About the conversion

 The process generates the following OWL artifacts in the ontology:

 * Class declarations for all concepts
 * SubclassOf or EquivalentClass Axioms for the concept definitions
 * ObjectProperty declarations, including SubPropertyOf for Role hierarchies
 * Non groupable attributes are managed in a special way to nerver add them in a Relationship Group
   * *Part of*
   * *Has active ingredient*
   * *Laterality*
   * *Has dose form*
 * A sub-property chain is created between *Direct substance* and *Has active ingredient*