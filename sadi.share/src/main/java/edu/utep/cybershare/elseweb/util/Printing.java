package edu.utep.cybershare.elseweb.util;

import java.io.StringWriter;

import com.hp.hpl.jena.rdf.model.Model;

public class Printing {

	public static void print(Model model){
		StringWriter wtr = new StringWriter();
		model.write(wtr, "RDF/XML-ABBREV");
		System.out.println(wtr.toString());
	}
}
