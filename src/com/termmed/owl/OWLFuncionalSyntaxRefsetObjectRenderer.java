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

import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.EscapeUtils;
import org.semanticweb.owlapi.vocab.OWLXMLVocabulary;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Stream;

import static org.semanticweb.owlapi.model.parameters.Imports.EXCLUDED;
import static org.semanticweb.owlapi.model.parameters.Imports.INCLUDED;
import static org.semanticweb.owlapi.util.CollectionFactory.sortOptionally;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.RDFS_LABEL;
import static org.semanticweb.owlapi.vocab.OWLXMLVocabulary.*;

/**
 * Created by alo on 4/17/17.
 */
public class OWLFuncionalSyntaxRefsetObjectRenderer implements OWLObjectVisitor {

    class AxiomRetriever implements OWLEntityVisitorEx<Stream<? extends OWLAxiom>> {

        @Override
        public Stream<? extends OWLAxiom> visit(OWLClass cls) {
            return ont.axioms(cls, EXCLUDED);
        }

        @Override
        public Stream<? extends OWLAxiom> visit(OWLObjectProperty property) {
            return ont.axioms(property, EXCLUDED);
        }

        @Override
        public Stream<? extends OWLAxiom> visit(OWLDataProperty property) {
            return ont.axioms(property, EXCLUDED);
        }

        @Override
        public Stream<? extends OWLAxiom> visit(OWLNamedIndividual individual) {
            return ont.axioms(individual, EXCLUDED);
        }

        @Override
        public Stream<? extends OWLAxiom> visit(OWLDatatype datatype) {
            return ont.axioms(datatype, EXCLUDED);
        }

        @Override
        public Stream<? extends OWLAxiom> visit(OWLAnnotationProperty property) {
            return ont.axioms(property, EXCLUDED);
        }
    }

    @Nonnull
    private DefaultPrefixManager defaultPrefixManager;
    private PrefixManager prefixManager;
    protected final OWLOntology ont;
    private final Writer writer;
    private boolean writeEntitiesAsURIs = true;
    private boolean addMissingDeclarations = true;
    protected AnnotationValueShortFormProvider labelMaker = null;
    private String rootSctid = "138875005";
    private String generalConceptAxion = "733929006";
    private String owlOntologyHeader = "734147008";
    private String owlOntologyNamespace = "734146004";
    private String moduleId = "900000000000207008";
    private String owlAxiomRefset = "733073007";
    private String owlOntologyRefset = "762103008";

    /**
     * @param ontology
     *        the ontology
     * @param writer
     *        the writer
     */
    public OWLFuncionalSyntaxRefsetObjectRenderer(OWLOntology ontology, Writer writer) {
        ont = ontology;
        this.writer = writer;
        defaultPrefixManager = new DefaultPrefixManager();
        prefixManager = defaultPrefixManager;
        OWLDocumentFormat ontologyFormat = ontology.getFormat();
        // reuse the setting on the existing format
        addMissingDeclarations = ontologyFormat.isAddMissingTypes();
        if (ontologyFormat instanceof PrefixDocumentFormat) {
            prefixManager.copyPrefixesFrom((PrefixDocumentFormat) ontologyFormat);
            prefixManager.setPrefixComparator(((PrefixDocumentFormat) ontologyFormat).getPrefixComparator());
        }
        if (!ontology.isAnonymous()) {
            String existingDefault = prefixManager.getDefaultPrefix();
            String ontologyIRIString = ontology.getOntologyID().getOntologyIRI().get().toString();
            if (existingDefault == null || !existingDefault.startsWith(ontologyIRIString)) {
                String defaultPrefix = ontologyIRIString;
                if (!ontologyIRIString.endsWith("/")) {
                    defaultPrefix = ontologyIRIString + '#';
                }
                prefixManager.setDefaultPrefix(defaultPrefix);
            }
        }
        Map<OWLAnnotationProperty, List<String>> prefLangMap = new HashMap<>();
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLAnnotationProperty labelProp = df.getOWLAnnotationProperty(RDFS_LABEL.getIRI());
        labelMaker = new AnnotationValueShortFormProvider(Collections.singletonList(labelProp), prefLangMap, manager,
                defaultPrefixManager);
    }

    /**
     * Set the add missing declaration flag.
     *
     * @param flag
     *        new value
     */
    public void setAddMissingDeclarations(boolean flag) {
        addMissingDeclarations = flag;
    }

    /**
     * @param prefixManager
     *        the new prefix manager
     */
    public void setPrefixManager(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
        if (prefixManager instanceof DefaultPrefixManager) {
            defaultPrefixManager = (DefaultPrefixManager) prefixManager;
        }
    }

    protected void writePrefix(String prefix, String namespace) {
        writeRefsetColumns(owlOntologyRefset, owlOntologyHeader, moduleId);
        write("Prefix");
        writeOpenBracket();
        write(prefix);
        write("=");
        write("<");
        write(namespace);
        write(">");
        writeCloseBracket();
        writeReturn();
    }

    protected void writePrefixes() {
        prefixManager.getPrefixName2PrefixMap().forEach((k, v) -> writePrefix(k, v));
    }

    private void write(OWLXMLVocabulary v) {
        write(v.getShortForm());
    }

    private void write(String s) {
        try {
            writer.write(s);
        } catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

    private void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

    private void write(IRI iri) {
        String qname = prefixManager.getPrefixIRI(iri);
        if (qname != null) {
            boolean lastCharIsColon = qname.charAt(qname.length() - 1) == ':';
            if (!lastCharIsColon) {
                write(qname);
                return;
            }
        }
        writeFullIRI(iri);
    }

    private void writeFullIRI(IRI iri) {
        write("<");
        write(iri.toString());
        write(">");
    }

    @Override
    public void visit(OWLOntology ontology) {
        writeRefsetHeader();
        writePrefixes();
        //writeReturn();
        //writeReturn();
        //write(ONTOLOGY);
        //writeOpenBracket();
//        if (!ontology.isAnonymous()) {
//            writeFullIRI(ontology.getOntologyID().getOntologyIRI().get());
//            Optional<IRI> versionIRI = ontology.getOntologyID().getVersionIRI();
//            if (versionIRI.isPresent()) {
//                writeReturn();
//                writeFullIRI(versionIRI.get());
//            }
//            writeReturn();
//        }
//        ontology.importsDeclarations().forEach(decl -> {
//            write(IMPORT);
//            writeOpenBracket();
//            writeFullIRI(decl.getIRI());
//            writeCloseBracket();
//            writeReturn();
//        });
        sortOptionally(ontology.annotations()).forEach(this::acceptAndReturn);
//        writeReturn();
        Set<OWLAxiom> writtenAxioms = new HashSet<>();
        List<OWLEntity> signature = sortOptionally(ontology.signature());
        Collection<IRI> illegals = OWLDocumentFormat.determineIllegalPunnings(addMissingDeclarations, signature
                .stream(), ont.getPunnedIRIs(INCLUDED));
        signature.forEach(e -> writeDeclarations(e, writtenAxioms, illegals));
        writeSortedEntities("Annotation Properties", "Annotation Property", ontology.annotationPropertiesInSignature(
                EXCLUDED), writtenAxioms);
        writeSortedEntities("Object Properties", "Object Property", ontology.objectPropertiesInSignature(EXCLUDED),
                writtenAxioms);
        writeSortedEntities("Data Properties", "Data Property", ontology.dataPropertiesInSignature(EXCLUDED),
                writtenAxioms);
        writeSortedEntities("Datatypes", "Datatype", ontology.datatypesInSignature(EXCLUDED), writtenAxioms);
        writeSortedEntities("Classes", "Class", ontology.classesInSignature(EXCLUDED), writtenAxioms);
        writeSortedEntities("Named Individuals", "Individual", ontology.individualsInSignature(EXCLUDED),
                writtenAxioms);
        signature.forEach(e -> writeAxioms(e, writtenAxioms));
        sortOptionally(ontology.axioms().filter(ax -> !writtenAxioms.contains(ax))).forEach(this::acceptAndReturn);
        writeCloseBracket();
        flush();
    }

    private void writeSortedEntities(String bannerComment, String entityTypeName, Stream<? extends OWLEntity> entities,
                                     Set<OWLAxiom> writtenAxioms) {
        List<? extends OWLEntity> sortOptionally = sortOptionally(entities);
        if (!sortOptionally.isEmpty()) {
            writeEntities(bannerComment, entityTypeName, sortOptionally, writtenAxioms);
            //writeReturn();
        }
    }

    private void writeln(String s) {
        write(s);
        writeReturn();
    }

    private void writeEntities(String comment, String entityTypeName, List<? extends OWLEntity> entities,
                               Set<OWLAxiom> writtenAxioms) {
        boolean haveWrittenBanner = false;
        for (OWLEntity owlEntity : entities) {
            List<? extends OWLAxiom> axiomsForEntity = asList(getUnsortedAxiomsForEntity(owlEntity).filter(
                    ax -> !writtenAxioms.contains(ax)));
            List<OWLAnnotationAssertionAxiom> list = asList(ont.annotationAssertionAxioms(owlEntity.getIRI()).filter(
                    ax -> !writtenAxioms.contains(ax)));
            if (axiomsForEntity.isEmpty() && list.isEmpty()) {
                continue;
            }
            if (!haveWrittenBanner) {
                //writeln("############################");
                //writeln("#   " + comment);
                //writeln("############################");
                //writeReturn();
                haveWrittenBanner = true;
            }
            writeEntity2(owlEntity, entityTypeName, sortOptionally(axiomsForEntity), sortOptionally(list),
                    writtenAxioms);
        }
    }

    /**
     * Writes out the axioms that define the specified entity.
     *
     * @param entity
     *        The entity
     * @return The set of axioms that was written out
     */
    protected Set<OWLAxiom> writeAxioms(OWLEntity entity) {
        Set<OWLAxiom> writtenAxioms = new HashSet<>();
        writeAxioms(entity, writtenAxioms);
        return writtenAxioms;
    }

    /**
     * Writes out the axioms that define the specified entity.
     *
     * @param entity
     *        The entity
     * @return The set of axioms that was written out
     */
    protected Set<OWLAxiom> writeEntity(OWLEntity entity) {
        Set<OWLAxiom> writtenAxioms = new HashSet<>();
        writeEntity(entity, writtenAxioms);
        return writtenAxioms;
    }

    protected void writeEntity(OWLEntity entity, Set<OWLAxiom> alreadyWrittenAxioms) {
        writeEntity2(entity, "", sortOptionally(getUnsortedAxiomsForEntity(entity)), sortOptionally(ont
                .annotationAssertionAxioms(entity.getIRI())), alreadyWrittenAxioms);
    }

    protected void writeEntity2(OWLEntity entity, String entityTypeName, List<? extends OWLAxiom> axiomsForEntity,
                                List<OWLAnnotationAssertionAxiom> annotationAssertionAxioms, Set<OWLAxiom> alreadyWrittenAxioms) {
        //writeln("# " + entityTypeName + ": " + getIRIString(entity) + " (" + getEntityLabel(entity) + ")");
        //writeReturn();
        annotationAssertionAxioms.stream().filter(alreadyWrittenAxioms::add).forEach(this::acceptAndReturn);
        axiomsForEntity.stream().filter(this::shouldWrite).filter(alreadyWrittenAxioms::add).forEach(
                this::acceptAndReturn);
        //writeReturn();
    }

    private boolean shouldWrite(OWLAxiom ax) {
        if (ax.getAxiomType().equals(AxiomType.DIFFERENT_INDIVIDUALS)) {
            return false;
        }
        if (ax.getAxiomType().equals(AxiomType.DISJOINT_CLASSES) && ((OWLDisjointClassesAxiom) ax).classExpressions()
                .count() > 2) {
            return false;
        }
        return true;
    }

    private Stream<? extends OWLAxiom> getUnsortedAxiomsForEntity(OWLEntity entity) {
        return entity.accept(new AxiomRetriever());
    }

    private String getIRIString(OWLEntity entity) {
        return defaultPrefixManager.getShortForm(entity);
    }

    private String getEntityLabel(OWLEntity entity) {
        return labelMaker.getShortForm(entity);
    }

    private void writeAxioms(OWLEntity entity, Set<OWLAxiom> alreadyWrittenAxioms) {
        writeAnnotations(entity, alreadyWrittenAxioms);
        List<OWLAxiom> writtenAxioms = new ArrayList<>();
        Stream<? extends OWLAxiom> stream = getUnsortedAxiomsForEntity(entity).filter(alreadyWrittenAxioms::contains)
                .filter(ax -> ax.getAxiomType().equals(AxiomType.DIFFERENT_INDIVIDUALS)).filter(ax -> ax.getAxiomType()
                        .equals(AxiomType.DISJOINT_CLASSES) && ((OWLDisjointClassesAxiom) ax).classExpressions().count() > 2);
        sortOptionally(stream).forEach(ax -> {
            ax.accept(this);
            writtenAxioms.add(ax);
            writeReturn();
        });
        alreadyWrittenAxioms.addAll(writtenAxioms);
    }

    /**
     * Writes out the declaration axioms for the specified entity.
     *
     * @param entity
     *        The entity
     * @return The axioms that were written out
     */
    protected Set<OWLAxiom> writeDeclarations(OWLEntity entity) {
        Set<OWLAxiom> axioms = new HashSet<>();
        sortOptionally(ont.declarationAxioms(entity)).forEach(ax -> {
            ax.accept(this);
            axioms.add(ax);
            writeReturn();
        });
        return axioms;
    }

    private void writeDeclarations(OWLEntity entity, Set<OWLAxiom> alreadyWrittenAxioms, Collection<IRI> illegals) {
        Collection<OWLDeclarationAxiom> axioms = sortOptionally(ont.declarationAxioms(entity));
        axioms.stream().filter(alreadyWrittenAxioms::add).forEach(this::acceptAndReturn);
        // if multiple illegal declarations already exist, they have already
        // been outputted the renderer cannot take responsibility for removing
        // them. It should not add declarations for illegally punned entities
        // here, though
        if (addMissingDeclarations && axioms.isEmpty() && !entity.isBuiltIn() && !illegals.contains(entity.getIRI())
                && !ont.isDeclared(entity, Imports.INCLUDED)) {
            OWLDeclarationAxiom declaration = ont.getOWLOntologyManager().getOWLDataFactory().getOWLDeclarationAxiom(
                    entity);
            acceptAndReturn(declaration);
        }
    }

    protected void acceptAndReturn(OWLObject ax) {
        // Filtering out components not included in SNOMED RF2 OWL Refset
        if (!(ax instanceof OWLDeclarationAxiom) &&
                !(ax instanceof OWLAnnotationAssertionAxiom)) {
            ax.accept(this);
            writeReturn();
        }
    }

    protected void acceptAndSpace(OWLObject ax) {
        ax.accept(this);
        writeSpace();
    }

    protected void spaceAndAccept(OWLObject ax) {
        writeSpace();
        ax.accept(this);
    }

    /**
     * Writes of the annotation for the specified entity.
     *
     * @param entity
     *        The entity
     * @param alreadyWrittenAxioms
     *        already written axioms, to be updated with the newly written
     *        axioms
     */
    protected void writeAnnotations(OWLEntity entity, Set<OWLAxiom> alreadyWrittenAxioms) {
        sortOptionally(ont.annotationAssertionAxioms(entity.getIRI()).filter(alreadyWrittenAxioms::add)).forEach(
                this::acceptAndReturn);
    }

    /**
     * Write.
     *
     * @param v
     *        the v
     * @param o
     *        the o
     */
    protected void write(OWLXMLVocabulary v, OWLObject o) {
        write(v);
        writeOpenBracket();
        o.accept(this);
        writeCloseBracket();
    }

    private void write(Stream<? extends OWLObject> objects) {
        write(asList(objects));
    }

    private void write(List<? extends OWLObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            if (i > 0) {
                writeSpace();
            }
            objects.get(i).accept(this);
        }
    }

    protected void writeOpenBracket() {
        write("(");
    }

    protected void writeCloseBracket() {
        write(")");
    }

    protected void writeSpace() {
        write(" ");
    }

    protected void writeReturn() {
        write("\n");
    }

    protected void writeRefsetColumns(String refsetId, String referencedComponentId, String moduleId) {
        String row = UUID.randomUUID() + "\t" +
                "20170731" + "\t1\t" + moduleId + "\t" +
                refsetId + "\t" + referencedComponentId + "\t";
        write(row);
    }

    protected void writeAnnotations(OWLAxiom ax) {
        ax.annotations().forEach(this::acceptAndSpace);
    }

    protected void writeRefsetHeader() {
        String row = "UUID\teffectiveTime\tactive\tmoduleId\trefsetId\treferencedComponentId\tannotation";
        write(row);
        writeReturn();
    }

    protected void writeAxiomStart(OWLXMLVocabulary v, OWLAxiom axiom) {
        if (axiom.isOfType(AxiomType.DECLARATION)) {
            OWLDeclarationAxiom da = (OWLDeclarationAxiom) axiom;
            String referencedComponentId = rootSctid;
            try {
                referencedComponentId = new Scanner(da.getEntity().toStringID()).useDelimiter("\\D+").next();
            } catch (Exception e) {
                // No id in the identity
            }
            writeRefsetColumns(owlAxiomRefset, referencedComponentId, moduleId);
        } else if (axiom.isOfType(AxiomType.ANNOTATION_ASSERTION)) {
            OWLAnnotationAssertionAxiom da = (OWLAnnotationAssertionAxiom) axiom;
            String referencedComponentId = rootSctid;
            try {
                referencedComponentId = new Scanner(da.getSubject().asIRI().toString()).useDelimiter("\\D+").next();
            } catch (Exception e) {
                // No id in the identity
            }
            writeRefsetColumns(owlAxiomRefset, referencedComponentId, moduleId);
        } else if (axiom.isOfType(AxiomType.EQUIVALENT_CLASSES)){
            OWLEquivalentClassesAxiom da = (OWLEquivalentClassesAxiom) axiom;
            String referencedComponentId = generalConceptAxion;
            try {
                referencedComponentId = new Scanner(da.namedClasses().findFirst().toString()).useDelimiter("\\D+").next();
            } catch (Exception e) {
                // No id in the identity
            }
            writeRefsetColumns(owlAxiomRefset, referencedComponentId, moduleId);
        } else if (axiom.isOfType(AxiomType.SUBCLASS_OF)){
            OWLSubClassOfAxiom da = (OWLSubClassOfAxiom) axiom;
            String referencedComponentId = generalConceptAxion;
            try {
                referencedComponentId = new Scanner(da.getSubClass().toString()).useDelimiter("\\D+").next();
            } catch (Exception e) {
                // No id in the identity
            }
            writeRefsetColumns(owlAxiomRefset, referencedComponentId, moduleId);
        } else if (axiom.isOfType(AxiomType.SUB_OBJECT_PROPERTY)){
            OWLSubObjectPropertyOfAxiom so = (OWLSubObjectPropertyOfAxiom) axiom;
            String referencedComponentId = rootSctid;
            try {
                referencedComponentId = new Scanner(so.getSubProperty().toString()).useDelimiter("\\D+").next();
            } catch (Exception e) {
                // No id in the identity
            }
            writeRefsetColumns(owlAxiomRefset, referencedComponentId, moduleId);
        } else {
            writeRefsetColumns(owlAxiomRefset, rootSctid, moduleId);
        }
        write(v);
        writeOpenBracket();
        writeAnnotations(axiom);
    }

    protected void writeAxiomEnd() {
        writeCloseBracket();
    }

    protected void writePropertyCharacteristic(OWLXMLVocabulary v, OWLAxiom ax, OWLPropertyExpression prop) {
        writeAxiomStart(v, ax);
        prop.accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(ASYMMETRIC_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }

    @Override
    public void visit(OWLClassAssertionAxiom axiom) {
        writeAxiomStart(CLASS_ASSERTION, axiom);
        acceptAndSpace(axiom.getClassExpression());
        axiom.getIndividual().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        writeAxiomStart(DATA_PROPERTY_ASSERTION, axiom);
        acceptAndSpace(axiom.getProperty());
        acceptAndSpace(axiom.getSubject());
        axiom.getObject().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLDataPropertyDomainAxiom axiom) {
        writeAxiomStart(DATA_PROPERTY_DOMAIN, axiom);
        acceptAndSpace(axiom.getProperty());
        axiom.getDomain().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLDataPropertyRangeAxiom axiom) {
        writeAxiomStart(DATA_PROPERTY_RANGE, axiom);
        acceptAndSpace(axiom.getProperty());
        axiom.getRange().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        writeAxiomStart(SUB_DATA_PROPERTY_OF, axiom);
        acceptAndSpace(axiom.getSubProperty());
        axiom.getSuperProperty().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLDeclarationAxiom axiom) {
        writeAxiomStart(DECLARATION, axiom);
        writeEntitiesAsURIs = false;
        axiom.getEntity().accept(this);
        writeEntitiesAsURIs = true;
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLDifferentIndividualsAxiom axiom) {
        List<OWLIndividual> individuals = asList(axiom.individuals());
        if (individuals.size() < 2) {
            // TODO log
            return;
        }
        writeAxiomStart(DIFFERENT_INDIVIDUALS, axiom);
        write(individuals);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLDisjointClassesAxiom axiom) {
        List<OWLClassExpression> classExpressions = asList(axiom.classExpressions());
        if (classExpressions.size() < 2) {
            // TODO log
            return;
        }
        writeAxiomStart(DISJOINT_CLASSES, axiom);
        write(classExpressions);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        List<OWLDataPropertyExpression> properties = asList(axiom.properties());
        if (properties.size() < 2) {
            // TODO log
            return;
        }
        writeAxiomStart(DISJOINT_DATA_PROPERTIES, axiom);
        write(properties);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        List<OWLObjectPropertyExpression> properties = asList(axiom.properties());
        if (properties.size() < 2) {
            // TODO log
            return;
        }
        writeAxiomStart(DISJOINT_OBJECT_PROPERTIES, axiom);
        write(properties);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLDisjointUnionAxiom axiom) {
        writeAxiomStart(DISJOINT_UNION, axiom);
        acceptAndSpace(axiom.getOWLClass());
        write(axiom.classExpressions());
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
        writeAxiomStart(ANNOTATION_ASSERTION, axiom);
        acceptAndSpace(axiom.getProperty());
        acceptAndSpace(axiom.getSubject());
        axiom.getValue().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        List<OWLClassExpression> classExpressions = asList(axiom.classExpressions());
        if (classExpressions.size() < 2) {
            // TODO log
            return;
        }
        writeAxiomStart(EQUIVALENT_CLASSES, axiom);
        write(classExpressions);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        List<OWLDataPropertyExpression> properties = asList(axiom.properties());
        if (properties.size() < 2) {
            // TODO log
            return;
        }
        writeAxiomStart(EQUIVALENT_DATA_PROPERTIES, axiom);
        write(properties);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        List<OWLObjectPropertyExpression> properties = asList(axiom.properties());
        if (properties.size() < 2) {
            // TODO log
            return;
        }
        writeAxiomStart(EQUIVALENT_OBJECT_PROPERTIES, axiom);
        write(properties);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        writePropertyCharacteristic(FUNCTIONAL_DATA_PROPERTY, axiom, axiom.getProperty());
    }

    @Override
    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(FUNCTIONAL_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }

    @Override
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(INVERSE_FUNCTIONAL_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }

    @Override
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        writeAxiomStart(INVERSE_OBJECT_PROPERTIES, axiom);
        acceptAndSpace(axiom.getFirstProperty());
        axiom.getSecondProperty().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(IRREFLEXIVE_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }

    @Override
    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        writeAxiomStart(NEGATIVE_DATA_PROPERTY_ASSERTION, axiom);
        acceptAndSpace(axiom.getProperty());
        acceptAndSpace(axiom.getSubject());
        axiom.getObject().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        writeAxiomStart(NEGATIVE_OBJECT_PROPERTY_ASSERTION, axiom);
        acceptAndSpace(axiom.getProperty());
        acceptAndSpace(axiom.getSubject());
        axiom.getObject().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        writeAxiomStart(OBJECT_PROPERTY_ASSERTION, axiom);
        acceptAndSpace(axiom.getProperty());
        acceptAndSpace(axiom.getSubject());
        axiom.getObject().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        writeAxiomStart(SUB_OBJECT_PROPERTY_OF, axiom);
        write(OBJECT_PROPERTY_CHAIN);
        writeOpenBracket();
        write(axiom.getPropertyChain());
        writeCloseBracket();
        spaceAndAccept(axiom.getSuperProperty());
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        writeAxiomStart(OBJECT_PROPERTY_DOMAIN, axiom);
        acceptAndSpace(axiom.getProperty());
        axiom.getDomain().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        writeAxiomStart(OBJECT_PROPERTY_RANGE, axiom);
        acceptAndSpace(axiom.getProperty());
        axiom.getRange().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        writeAxiomStart(SUB_OBJECT_PROPERTY_OF, axiom);
        acceptAndSpace(axiom.getSubProperty());
        axiom.getSuperProperty().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(REFLEXIVE_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }

    @Override
    public void visit(OWLSameIndividualAxiom axiom) {
        List<OWLIndividual> individuals = axiom.getIndividualsAsList();
        if (individuals.size() < 2) {
            // TODO log
            return;
        }
        writeAxiomStart(SAME_INDIVIDUAL, axiom);
        write(individuals);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        writeAxiomStart(SUB_CLASS_OF, axiom);
        acceptAndSpace(axiom.getSubClass());
        axiom.getSuperClass().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(SYMMETRIC_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }

    @Override
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(TRANSITIVE_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }

    @Override
    public void visit(OWLClass ce) {
        if (!writeEntitiesAsURIs) {
            write(CLASS);
            writeOpenBracket();
        }
        ce.getIRI().accept(this);
        if (!writeEntitiesAsURIs) {
            writeCloseBracket();
        }
    }

    private <F extends OWLPropertyRange> void writeRestriction(OWLXMLVocabulary v,
                                                               OWLCardinalityRestriction<F> restriction, OWLPropertyExpression p) {
        write(v);
        writeOpenBracket();
        write(Integer.toString(restriction.getCardinality()));
        spaceAndAccept(p);
        if (restriction.isQualified()) {
            spaceAndAccept(restriction.getFiller());
        }
        writeCloseBracket();
    }

    private void writeRestriction(OWLXMLVocabulary v, OWLQuantifiedDataRestriction restriction) {
        writeRestriction(v, restriction.getProperty(), restriction.getFiller());
    }

    private void writeRestriction(OWLXMLVocabulary v, OWLQuantifiedObjectRestriction restriction) {
        writeRestriction(v, restriction.getProperty(), restriction.getFiller());
    }

    private void writeRestriction(OWLXMLVocabulary v, OWLPropertyExpression prop, OWLObject filler) {
        write(v);
        writeOpenBracket();
        acceptAndSpace(prop);
        filler.accept(this);
        writeCloseBracket();
    }

    @Override
    public void visit(OWLDataAllValuesFrom ce) {
        writeRestriction(DATA_ALL_VALUES_FROM, ce);
    }

    @Override
    public void visit(OWLDataExactCardinality ce) {
        writeRestriction(DATA_EXACT_CARDINALITY, ce, ce.getProperty());
    }

    @Override
    public void visit(OWLDataMaxCardinality ce) {
        writeRestriction(DATA_MAX_CARDINALITY, ce, ce.getProperty());
    }

    @Override
    public void visit(OWLDataMinCardinality ce) {
        writeRestriction(DATA_MIN_CARDINALITY, ce, ce.getProperty());
    }

    @Override
    public void visit(OWLDataSomeValuesFrom ce) {
        writeRestriction(DATA_SOME_VALUES_FROM, ce);
    }

    @Override
    public void visit(OWLDataHasValue ce) {
        writeRestriction(DATA_HAS_VALUE, ce.getProperty(), ce.getFiller());
    }

    @Override
    public void visit(OWLObjectAllValuesFrom ce) {
        writeRestriction(OBJECT_ALL_VALUES_FROM, ce);
    }

    @Override
    public void visit(OWLObjectComplementOf ce) {
        write(OBJECT_COMPLEMENT_OF, ce.getOperand());
    }

    @Override
    public void visit(OWLObjectExactCardinality ce) {
        writeRestriction(OBJECT_EXACT_CARDINALITY, ce, ce.getProperty());
    }

    @Override
    public void visit(OWLObjectIntersectionOf ce) {
        if (ce.operands().count() == 1) {
            ce.operands().forEach(x -> x.accept(this));
            return;
        }
        write(OBJECT_INTERSECTION_OF);
        writeOpenBracket();
        write(ce.operands());
        writeCloseBracket();
    }

    @Override
    public void visit(OWLObjectMaxCardinality ce) {
        writeRestriction(OBJECT_MAX_CARDINALITY, ce, ce.getProperty());
    }

    @Override
    public void visit(OWLObjectMinCardinality ce) {
        writeRestriction(OBJECT_MIN_CARDINALITY, ce, ce.getProperty());
    }

    @Override
    public void visit(OWLObjectOneOf ce) {
        write(OBJECT_ONE_OF);
        writeOpenBracket();
        write(ce.individuals());
        writeCloseBracket();
    }

    @Override
    public void visit(OWLObjectHasSelf ce) {
        write(OBJECT_HAS_SELF, ce.getProperty());
    }

    @Override
    public void visit(OWLObjectSomeValuesFrom ce) {
        writeRestriction(OBJECT_SOME_VALUES_FROM, ce);
    }

    @Override
    public void visit(OWLObjectUnionOf ce) {
        if (ce.operands().count() == 1) {
            ce.operands().forEach(x -> x.accept(this));
            return;
        }
        write(OBJECT_UNION_OF);
        writeOpenBracket();
        write(ce.operands());
        writeCloseBracket();
    }

    @Override
    public void visit(OWLObjectHasValue ce) {
        writeRestriction(OBJECT_HAS_VALUE, ce.getProperty(), ce.getFiller());
    }

    @Override
    public void visit(OWLDataComplementOf node) {
        write(DATA_COMPLEMENT_OF, node.getDataRange());
    }

    @Override
    public void visit(OWLDataOneOf node) {
        write(DATA_ONE_OF);
        writeOpenBracket();
        write(node.values());
        writeCloseBracket();
    }

    @Override
    public void visit(OWLDatatype node) {
        if (!writeEntitiesAsURIs) {
            write(DATATYPE);
            writeOpenBracket();
        }
        node.getIRI().accept(this);
        if (!writeEntitiesAsURIs) {
            writeCloseBracket();
        }
    }

    @Override
    public void visit(OWLDatatypeRestriction node) {
        write(DATATYPE_RESTRICTION);
        writeOpenBracket();
        node.getDatatype().accept(this);
        node.facetRestrictions().forEach(this::spaceAndAccept);
        writeCloseBracket();
    }

    @Override
    public void visit(OWLFacetRestriction node) {
        write(node.getFacet().getIRI());
        spaceAndAccept(node.getFacetValue());
    }

    @Override
    public void visit(OWLLiteral node) {
        write("\"");
        write(EscapeUtils.escapeString(node.getLiteral()));
        write("\"");
        if (node.hasLang()) {
            write("@");
            write(node.getLang());
        } else if (!node.isRDFPlainLiteral()) {
            write("^^");
            write(node.getDatatype().getIRI());
        }
    }

    @Override
    public void visit(OWLDataProperty property) {
        if (!writeEntitiesAsURIs) {
            write(DATA_PROPERTY);
            writeOpenBracket();
        }
        property.getIRI().accept(this);
        if (!writeEntitiesAsURIs) {
            writeCloseBracket();
        }
    }

    @Override
    public void visit(OWLObjectProperty property) {
        if (!writeEntitiesAsURIs) {
            write(OBJECT_PROPERTY);
            writeOpenBracket();
        }
        property.getIRI().accept(this);
        if (!writeEntitiesAsURIs) {
            writeCloseBracket();
        }
    }

    @Override
    public void visit(OWLObjectInverseOf property) {
        write(OBJECT_INVERSE_OF);
        writeOpenBracket();
        property.getInverse().accept(this);
        writeCloseBracket();
    }

    @Override
    public void visit(OWLNamedIndividual individual) {
        if (!writeEntitiesAsURIs) {
            write(NAMED_INDIVIDUAL);
            writeOpenBracket();
        }
        individual.getIRI().accept(this);
        if (!writeEntitiesAsURIs) {
            writeCloseBracket();
        }
    }

    @Override
    public void visit(OWLHasKeyAxiom axiom) {
        writeAxiomStart(HAS_KEY, axiom);
        acceptAndSpace(axiom.getClassExpression());
        writeOpenBracket();
        write(asList(axiom.objectPropertyExpressions()));
        writeCloseBracket();
        writeSpace();
        writeOpenBracket();
        write(asList(axiom.dataPropertyExpressions()));
        writeCloseBracket();
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
        writeAxiomStart(ANNOTATION_PROPERTY_DOMAIN, axiom);
        acceptAndSpace(axiom.getProperty());
        axiom.getDomain().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
        writeAxiomStart(ANNOTATION_PROPERTY_RANGE, axiom);
        acceptAndSpace(axiom.getProperty());
        axiom.getRange().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        writeAxiomStart(SUB_ANNOTATION_PROPERTY_OF, axiom);
        acceptAndSpace(axiom.getSubProperty());
        axiom.getSuperProperty().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(OWLDataIntersectionOf node) {
        if (node.operands().count() == 1) {
            node.operands().forEach(x -> x.accept(this));
            return;
        }
        write(DATA_INTERSECTION_OF);
        writeOpenBracket();
        write(node.operands());
        writeCloseBracket();
    }

    @Override
    public void visit(OWLDataUnionOf node) {
        if (node.operands().count() == 1) {
            node.operands().forEach(x -> x.accept(this));
            return;
        }
        write(DATA_UNION_OF);
        writeOpenBracket();
        write(node.operands());
        writeCloseBracket();
    }

    @Override
    public void visit(OWLAnnotationProperty property) {
        if (!writeEntitiesAsURIs) {
            write(ANNOTATION_PROPERTY);
            writeOpenBracket();
        }
        property.getIRI().accept(this);
        if (!writeEntitiesAsURIs) {
            writeCloseBracket();
        }
    }

    @Override
    public void visit(OWLAnonymousIndividual individual) {
        write(individual.getID().toString());
    }

    @Override
    public void visit(IRI iri) {
        write(iri);
    }

    @Override
    public void visit(OWLAnnotation node) {
        write(ANNOTATION);
        writeOpenBracket();
        node.annotations().forEach(this::acceptAndSpace);
        acceptAndSpace(node.getProperty());
        node.getValue().accept(this);
        writeCloseBracket();
    }

    @Override
    public void visit(OWLDatatypeDefinitionAxiom axiom) {
        writeAxiomStart(DATATYPE_DEFINITION, axiom);
        acceptAndSpace(axiom.getDatatype());
        axiom.getDataRange().accept(this);
        writeAxiomEnd();
    }

    @Override
    public void visit(SWRLRule rule) {
        writeAxiomStart(DL_SAFE_RULE, rule);
        write(BODY);
        writeOpenBracket();
        write(rule.body());
        writeCloseBracket();
        write(HEAD);
        writeOpenBracket();
        write(rule.head());
        writeCloseBracket();
        writeAxiomEnd();
    }

    @Override
    public void visit(SWRLIndividualArgument node) {
        node.getIndividual().accept(this);
    }

    @Override
    public void visit(SWRLClassAtom node) {
        write(CLASS_ATOM);
        writeOpenBracket();
        acceptAndSpace(node.getPredicate());
        node.getArgument().accept(this);
        writeCloseBracket();
    }

    @Override
    public void visit(SWRLDataRangeAtom node) {
        write(DATA_RANGE_ATOM);
        writeOpenBracket();
        acceptAndSpace(node.getPredicate());
        node.getArgument().accept(this);
        writeCloseBracket();
    }

    @Override
    public void visit(SWRLObjectPropertyAtom node) {
        write(OBJECT_PROPERTY_ATOM);
        writeOpenBracket();
        acceptAndSpace(node.getPredicate());
        acceptAndSpace(node.getFirstArgument());
        node.getSecondArgument().accept(this);
        writeCloseBracket();
    }

    @Override
    public void visit(SWRLDataPropertyAtom node) {
        write(DATA_PROPERTY_ATOM);
        writeOpenBracket();
        acceptAndSpace(node.getPredicate());
        acceptAndSpace(node.getFirstArgument());
        node.getSecondArgument().accept(this);
        writeCloseBracket();
    }

    @Override
    public void visit(SWRLBuiltInAtom node) {
        write(BUILT_IN_ATOM);
        writeOpenBracket();
        acceptAndSpace(node.getPredicate());
        write(node.getArguments());
        writeCloseBracket();
    }

    @Override
    public void visit(SWRLVariable node) {
        write(VARIABLE);
        writeOpenBracket();
        node.getIRI().accept(this);
        writeCloseBracket();
    }

    @Override
    public void visit(SWRLLiteralArgument node) {
        node.getLiteral().accept(this);
    }

    @Override
    public void visit(SWRLDifferentIndividualsAtom node) {
        write(DIFFERENT_INDIVIDUALS_ATOM);
        writeOpenBracket();
        acceptAndSpace(node.getFirstArgument());
        node.getSecondArgument().accept(this);
        writeCloseBracket();
    }

    @Override
    public void visit(SWRLSameIndividualAtom node) {
        write(SAME_INDIVIDUAL_ATOM);
        writeOpenBracket();
        acceptAndSpace(node.getFirstArgument());
        node.getSecondArgument().accept(this);
        writeCloseBracket();
    }
}
