package com.termmed.owl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import com.termmed.rf2.model.ConceptDescriptor;
import com.termmed.rf2.model.LightRelationship;

public class RF2Parser {

	private Map<Long, ConceptDescriptor> concepts;
	private Map<Long, HashMap<Integer, List<LightRelationship>>> relationships;

	private HashMap<Long, OWLObjectProperty> hashRoles;
	private Map<Long, List<LightRelationship>> isarelationships;
	private long ISARELATIONSHIPTYPEID=116680003l;
	private String PRIMITIVE="900000000000074008";
	private OWLObjectProperty roleGroupProp;
	private long ROLEGROUPSCTID=609096000l;
	private long CONCEPTMODELATTRIBUTE=410662002l;
	private HashSet<Long> attributes;
	private OWLOntologyManager manager = null;
	private IRI ontologyIRI = null;
	private OWLOntology ont = null;
	private OWLDataFactory factory ;
	private HashSet<Long> wogroup;
	private HashMap<Long, Long> rightIdent;
	private String COREMODULE="900000000000207008";
	private String conceptFile;
	private String relationshipFile;
	private String outputFile;
	private String iri;
	
	public RF2Parser(String conceptFile, String relationshipFile,
					 String outputFile, String iri) {
		super();
		this.conceptFile = conceptFile;
		this.relationshipFile = relationshipFile;
		this.outputFile = outputFile;
		this.iri = iri;
		concepts = new HashMap<Long, ConceptDescriptor>();
		relationships = new HashMap<Long, HashMap<Integer,List<LightRelationship>>>();
		wogroup=new HashSet<Long>();
		wogroup.add(123005000l);
		wogroup.add(127489000l);
		wogroup.add(272741003l);
		wogroup.add(411116001l);

		rightIdent=new HashMap<Long,Long>();
		rightIdent.put(363701004l, 127489000l);
	}

	public void parse()throws OWLOntologyCreationException,
	OWLOntologyStorageException, FileNotFoundException, IOException{
		loadConceptsFile(new File(conceptFile));
		loadIsaRelationshipsFile(new File(relationshipFile));
		loadRelationshipsFile(new File(relationshipFile));

		manager = OWLManager.createOWLOntologyManager();
		ontologyIRI = IRI.create(iri);
		ont = manager.createOntology(ontologyIRI);
		factory = manager.getOWLDataFactory();


		addRoles();
		System.out.println("Attribute count:" + attributes.size());

		roleGroupProp=factory.getOWLObjectProperty(IRI
				.create("id/" + ROLEGROUPSCTID));	
		HashMap <Integer,List<LightRelationship>> listLR = new HashMap<Integer,List<LightRelationship>>();
		List<LightRelationship> listIsas = new ArrayList<LightRelationship>();

		for (Long cptId : concepts.keySet()) {
			if (hashRoles.containsKey(cptId)){
				continue;
			}
			boolean isPrim=(concepts.get(cptId).getDefinitionStatus().equals(PRIMITIVE));
			OWLClass conceptClass = factory.getOWLClass(IRI.create("id/" + concepts.get(cptId).getConceptId()));
			listIsas = isarelationships.get(cptId);
			
			OWLDeclarationAxiom declarationAxiom = factory
					.getOWLDeclarationAxiom(conceptClass);
			manager.addAxiom(ont, declarationAxiom);

			listLR = relationships.get(cptId);
			if (listIsas != null) {
				if (listIsas.size()==0){

				}else if (listIsas.size()==1 && (listLR==null || (listLR!=null && listLR.size()==0))){
					OWLClass targetClass = factory.getOWLClass(IRI.create("id/" + listIsas.get(0).getTarget()));
					manager.addAxiom(ont, factory.getOWLSubClassOfAxiom(conceptClass, targetClass));
				}else{
					Set<OWLClassExpression> set=new HashSet<OWLClassExpression>();
					for (LightRelationship lrel : listIsas) {

						OWLClass targetClass = factory.getOWLClass(IRI.create("id/" + lrel.getTarget()));
						set.add(targetClass);

					}
					
					if (listLR!=null && listLR.size()>0){
						set=addtoSet(set,listLR);
					}
					OWLObjectIntersectionOf intersection=factory.getOWLObjectIntersectionOf(set);

					if (isPrim){
						manager.addAxiom(ont,factory.getOWLSubClassOfAxiom(conceptClass, intersection));
					}else{
						manager.addAxiom(ont,factory.getOWLEquivalentClassesAxiom(conceptClass,intersection));
					}
				}
			}
		}

		File f = new File(outputFile);
		IRI documentIRI = IRI.create(f);
		manager.saveOntology(ont, new RDFXMLOntologyFormat(), documentIRI);
		manager.removeOntology(ont);
		System.gc();

	}
	private Set<OWLClassExpression> addtoSet(
			Set<OWLClassExpression> set, HashMap<Integer, List<LightRelationship>> listLR) {
		List<LightRelationship> listR = new ArrayList<LightRelationship>();
		for (Integer grp:listLR.keySet()){
			if (grp==0){
				listR=listLR.get(0);
				for (LightRelationship lrel:listR){

					OWLClass targetClass = factory.getOWLClass(IRI.create("id/" + lrel.getTarget()));
					OWLObjectProperty property= hashRoles.get(lrel.getType());
					OWLClassExpression role=factory.getOWLObjectSomeValuesFrom(property, targetClass);

					if (wogroup.contains(lrel.getType())){
						set.add(role);
					}else{
						OWLClassExpression singlerolegroup=factory.getOWLObjectSomeValuesFrom(roleGroupProp, role);
						set.add(singlerolegroup);
					}

				}
			}else{
				HashSet<OWLClassExpression> setRoles = new HashSet<OWLClassExpression>();
				listR=listLR.get(grp);
				for (LightRelationship lrel:listR){

					OWLClass targetClass = factory.getOWLClass(IRI.create("id/" + lrel.getTarget()));
					OWLObjectProperty property= hashRoles.get(lrel.getType());
					OWLClassExpression role=factory.getOWLObjectSomeValuesFrom(property, targetClass);
					setRoles.add(role);
				}
				OWLObjectIntersectionOf intersection=factory.getOWLObjectIntersectionOf(setRoles);
				OWLClassExpression roleGroup=factory.getOWLObjectSomeValuesFrom(roleGroupProp, intersection);
				set.add(roleGroup);
			}

		}

		return set;
	}

	private void addRoles() {

		hashRoles=new HashMap<Long,OWLObjectProperty>();
		for(Long concept:attributes){
			OWLObjectProperty property = factory.getOWLObjectProperty(IRI
					.create("id/" + concept));	
			OWLDeclarationAxiom declarationAxiom = factory
					.getOWLDeclarationAxiom(property);
			manager.addAxiom(ont, declarationAxiom);
			if(rightIdent.containsKey(concept)){

				long parentProp=rightIdent.get(concept);
				OWLObjectProperty superProperty = factory.getOWLObjectProperty(IRI
						.create("id/" + parentProp));
				List<OWLObjectProperty> lProp=new ArrayList<OWLObjectProperty>();
				lProp.add(property);
				lProp.add(superProperty);
				OWLSubPropertyChainOfAxiom chainAxiom = factory.getOWLSubPropertyChainOfAxiom(lProp, property);
				manager.addAxiom(ont, chainAxiom);
			}
			hashRoles.put(concept, property);
		}
		getDescendantsRoles(attributes);
	}
	private void getDescendantsRoles(HashSet<Long> attribute) {

		HashSet<Long> tmpSet=new HashSet<Long>();
		for (Long cpt:isarelationships.keySet()){
			List<LightRelationship> rels=isarelationships.get(cpt);
			for (LightRelationship rel:rels){
				if (attribute.contains(rel.getTarget())){
					attributes.add(rel.getSourceId());

					OWLObjectProperty subProperty = factory.getOWLObjectProperty(IRI
							.create("id/" + rel.getSourceId()));

					OWLDeclarationAxiom declarationAxiom = factory
							.getOWLDeclarationAxiom(subProperty);
					manager.addAxiom(ont, declarationAxiom);

					OWLObjectProperty superProperty= hashRoles.get(rel.getTarget());
					hashRoles.put(rel.getSourceId(), subProperty);
					OWLSubObjectPropertyOfAxiom SupPropAxim = factory.getOWLSubObjectPropertyOfAxiom(subProperty, superProperty);
					manager.addAxiom(ont, SupPropAxim);

					tmpSet.add(rel.getSourceId());
				}
			}
		}
		if (tmpSet.size()>0){
			getDescendantsRoles(tmpSet);
		}
	}
	public void loadConceptsFile(File conceptsFile) throws FileNotFoundException, IOException {
		System.out.println("Starting Concepts: " + conceptsFile.getName());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(conceptsFile), "UTF8"));
		try {
			String line = br.readLine();
			line = br.readLine(); // Skip header
			int count = 0;
			while (line != null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] columns = line.split("\\t");
				if ( columns[2].equals("1") && columns[3].equals(COREMODULE)){
					ConceptDescriptor loopConcept = new ConceptDescriptor();
					Long conceptId = Long.parseLong(columns[0]);
					loopConcept.setConceptId(conceptId);
					loopConcept.setActive(columns[2].equals("1"));
					loopConcept.setEffectiveTime(columns[1]);
					loopConcept.setModule(Long.parseLong(columns[3]));
					loopConcept.setDefinitionStatus(columns[4]);
					concepts.put(conceptId, loopConcept);
					count++;
					if (count % 100000 == 0) {
						System.out.print(".");
					}
				}
				line = br.readLine();
			}
			System.out.println(".");
			System.out.println("Concepts loaded = " + concepts.size());
		} finally {
			br.close();
		}

	}


	public void loadRelationshipsFile(File relationshipsFile) throws FileNotFoundException, IOException {
		System.out.println("Starting Relationships: " + relationshipsFile.getName());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(relationshipsFile), "UTF8"));
		try {
			String line = br.readLine();
			line = br.readLine(); // Skip header
			int count = 0;
			while (line != null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] columns = line.split("\\t");
				if (Long.parseLong(columns[7])!=ISARELATIONSHIPTYPEID 
						&& columns[2].equals("1")
						&& columns[3].equals(COREMODULE)){
					LightRelationship loopRelationship = new LightRelationship();

					loopRelationship.setActive(columns[2].equals("1"));
					loopRelationship.setEffectiveTime(columns[1]);
					loopRelationship.setModule(Long.parseLong(columns[3]));

					loopRelationship.setTarget(Long.parseLong(columns[5]));
					loopRelationship.setType(Long.parseLong(columns[7]));
					loopRelationship.setModifier(Long.parseLong(columns[9]));
					Integer groupId=Integer.parseInt(columns[6]);
					loopRelationship.setGroupId(groupId);
					Long sourceId = Long.parseLong(columns[4]);
					loopRelationship.setSourceId(sourceId);
					loopRelationship.setCharType(Long.parseLong(columns[8]));

					HashMap<Integer, List<LightRelationship>> relGrList = relationships.get(sourceId);
					if (relGrList == null) {
						relGrList = new HashMap<Integer,List<LightRelationship>>();
					}
					List<LightRelationship> relList=relGrList.get(groupId);
					if (relList == null) {
						relList = new ArrayList<LightRelationship>();
					}
					relList.add(loopRelationship);

					relGrList.put(groupId, relList);
					relationships.put(sourceId, relGrList);
					count++;
					if (count % 100000 == 0) {
						System.out.print(".");
					}
				}
				line = br.readLine();
			}
			System.out.println(".");
			System.out.println("Relationships loaded = " + relationships.size());
		} finally {
			br.close();
		}
	}

	public void loadIsaRelationshipsFile(File relationshipsFile) throws FileNotFoundException, IOException {
		System.out.println("Starting Isas Relationships from: " + relationshipsFile.getName());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(relationshipsFile), "UTF8"));
		try {
			String line = br.readLine();
			attributes=new HashSet<Long>();
			isarelationships=new HashMap<Long,List<LightRelationship>>();
			line = br.readLine(); // Skip header
			int count = 0;
			while (line != null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] columns = line.split("\\t");
				if (Long.parseLong(columns[7])==ISARELATIONSHIPTYPEID 
						&& columns[2].equals("1")){
					LightRelationship loopRelationship = new LightRelationship();

					loopRelationship.setActive(columns[2].equals("1"));
					loopRelationship.setEffectiveTime(columns[1]);
					loopRelationship.setModule(Long.parseLong(columns[3]));

					loopRelationship.setTarget(Long.parseLong(columns[5]));
					loopRelationship.setType(Long.parseLong(columns[7]));
					loopRelationship.setModifier(Long.parseLong(columns[9]));
					loopRelationship.setGroupId(Integer.parseInt(columns[6]));
					Long sourceId = Long.parseLong(columns[4]);
					loopRelationship.setSourceId(sourceId);
					loopRelationship.setCharType(Long.parseLong(columns[8]));

					List<LightRelationship> relList = isarelationships.get(sourceId);
					if (relList == null) {
						relList = new ArrayList<LightRelationship>();
					}
					relList.add(loopRelationship);
					isarelationships.put(sourceId, relList);
					if (Long.parseLong(columns[5])==CONCEPTMODELATTRIBUTE){
						attributes.add(sourceId);
					}
					count++;
					if (count % 100000 == 0) {
						System.out.print(".");
					}
				}
				line = br.readLine();
			}
			System.out.println(".");
			System.out.println("Isas Relationships loaded = " + isarelationships.size());
		} finally {
			br.close();
		}
	}
}
