# RF2 to OWL

This project uses the OWLApi to create an OWL ontology, in RDF/XML format, based on the content of standard SNOMED CT RF2 files.

## Build instructions

Clone and build with:

`mvn clean compile assembly:single`

This will generate the executable JAR file: owl-test-x.x.x-SNAPSHOT-jar-with-dependencies.jar

Binaries are also available in the releases section of the [github repository](https://github.com/termMed/rf2-to-owl/releases).

## Executing the conversion

Run the executable from the command line, for example:

`java -jar owl-test-x.x.x-SNAPSHOT-jar-with-dependencies.jar /folder/sct2_Concept_Snapshot_INT_20150131.txt /folder/sct2_StatedRelationship_Snapshot_INT_20150131.txt /folder/sct2_Description_Snapshot_INT_20150131.txt /folder/der2_cRefset_LanguageSnapshot-en_INT_20150131 /folder/outputFile.owl http://snomed.org/ TRUE`

Parameters (in this order):
 * Path to RF2 Concepts Snapshot file
 * Path to RF2 Stated Relationships Snapshot file
 * Path to RF2 Stated Descriptons Snapshot file
 * Path to RF2 Stated Language Refset Snapshot file
 * Path to output file (it will be created or replaced by this process)
 * IRI for the resulting ontology
 * Boolean value to enable transformation of to concrete domains (Data Properties) ("TRUE"/"FALSE")

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
 
## Concrete domains conversion
 
 Since the July 2017 release of the international edition, some relationships that specify numerical values are 
 represented using concepts for the example:

 * Source: 318420003 | Product containing atenolol 50 mg/1 each oral tablet (virtual clinical drug) |
 * Attribute: 732947008 | Has presentation strength denominator unit (attribute) |
 * Destination: 732774003 | 50 (qualifier value) |

 If the "Concrete domains conversion" parameter is sent as "TRUE", those relationships are converted into OWL Data 
 Properties, instead of the normal OWL Object Properties. Classification results should be identical, but having 
 actual values instead of concept references may allow for more detailed queries and computations.
 
 Attributes that are currently converted as data properties:
 
 * 732944001 | Has presentation strength numerator value (attribute) | - DataType: float
 * 732947008 | Has presentation strength denominator unit (attribute) | - DataType: float

