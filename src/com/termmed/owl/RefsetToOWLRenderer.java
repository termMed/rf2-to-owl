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

import java.io.*;

/**
 * Created by alo on 4/18/17.
 */
public class RefsetToOWLRenderer {

    public RefsetToOWLRenderer() {
    }

    public void render(File owlRefsetFile, Writer writer) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(owlRefsetFile), "UTF8"));
        br.readLine();
        String line;
        String[] spl;
        System.out.println("Converting refset from " + owlRefsetFile.getName());
        int count = 0;
        boolean headerWritten = false;
        while ((line=br.readLine())!=null) {
            count++;
            if (count ==1 || count % 10000 == 0) {
                System.out.print(".");
            }
            spl = line.split("\t", -1);
            if (spl.length == 7) {
                String axiom = spl[6];
                if (!headerWritten && !axiom.startsWith("Prefix")) {
                    writer.write("\n");
                    writer.write("Ontology(<http://snomed.org/snomedct>\n");
                    writer.write("\n");
                    headerWritten = true;
                }
                writer.write(axiom + "\n");
            }
        }
        writer.write(")");
    }
}
